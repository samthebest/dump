

import shapeless._, labelled.{FieldType, field}

trait FromMap[L <: HList] {
  def apply(m: Map[String, Any]): Option[L]
}

trait LowPriorityFromMap {
  implicit def hconsFromMap1[K <: Symbol, V, T <: HList](implicit
                                                         witness: Witness.Aux[K],
                                                         typeable: Typeable[V],
                                                         fromMapT: Lazy[FromMap[T]]
                                                        ): FromMap[FieldType[K, V] :: T] = new FromMap[FieldType[K, V] :: T] {
    def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = for {
      v <- m.get(witness.value.name)
      h <- typeable.cast(v)
      t <- fromMapT.value(m)
    } yield field[K](h) :: t
  }
}

object FromMap extends LowPriorityFromMap {
  implicit val hnilFromMap: FromMap[HNil] = new FromMap[HNil] {
    def apply(m: Map[String, Any]): Option[HNil] = Some(HNil)
  }

  implicit def hconsFromMap0[K <: Symbol, V, R <: HList, T <: HList](implicit
                                                                     witness: Witness.Aux[K],
                                                                     gen: LabelledGeneric.Aux[V, R],
                                                                     fromMapH: FromMap[R],
                                                                     fromMapT: FromMap[T]
                                                                    ): FromMap[FieldType[K, V] :: T] =
    new FromMap[FieldType[K, V] :: T] {
      def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = for {
        v <- m.get(witness.value.name)
        r <- Typeable[Map[String, Any]].cast(v)
        h <- fromMapH(r)
        t <- fromMapT(m)
      } yield field[K](gen.from(h)) :: t
    }
}

class MapToCaseClass[A] {
  def from[R <: HList](m: Map[String, Any])(implicit
                                            gen: LabelledGeneric.Aux[A, R],
                                            fromMap: FromMap[R]): Option[A] = fromMap(m).map(gen.from(_))
}

object MapToCaseClass {
  // TODO Can inline?
  def toMapToCaseClass[A]: MapToCaseClass[A] = new MapToCaseClass[A]
}

import FromMap._
import MapToCaseClass.toMapToCaseClass

trait CaseClassFromMap[P <: Product] {
  def apply(m: Map[String, Any]): Option[P]
}

object CaseClassFromMap {
  implicit def mk[P <: Product, R <: HList](implicit gen: LabelledGeneric.Aux[P, R],
                                            fromMap: FromMap[R]): CaseClassFromMap[P] = new CaseClassFromMap[P] {
    def apply(m: Map[String, Any]): Option[P] = toMapToCaseClass[P].from[R](m)
  }

  def apply[P <: Product](map: Map[String, Any])(implicit fromMap: CaseClassFromMap[P]): P = {
    fromMap(map).get
  }
}

import CaseClassFromMap._

case class Address(street: String, zip: Int)
case class Person(name: String, address: Address)

object Example {


  val mp = Map(
    "name" -> "Tom",
    "address" -> Map("street" -> "Jefferson st", "zip" -> 10000)
  )

  val converted = toMapToCaseClass[Person].from(mp)

  import shapeless._, labelled._

  val converted2 = apply[Person](mp)
}





import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import spray.json._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

sealed trait Format
case class JSON(dateFormat: Option[String] = None, ignoreAdditional: Boolean = true) extends Format
case class CSV(hasHeader: Boolean,
               quotedStrings: Boolean,
               ignoreAdditional: Boolean = true,
               dateFormat: Option[String] = None) extends Format

// I hate Either, want to use Xor from cats, or \/ from Scalaz instead


// Could have a second case class without recordLine and populate it later, would mean we don't have to keep passing it down
case class NotProcessableRecord(recordLine: String,
                                notProcessableReasonType: String,
                                notProcessableReasonMessage: String,
                                stackTrace: Option[String])

object ReadValidated {
  def apply[T <: Product : TypeTag : ClassTag : CaseClassFromMap](session: SparkSession,
                                                                  path: String,
                                                                  format: Format,
                                                                  expectedSchema: StructType): (Dataset[T], Dataset[NotProcessableRecord]) = {

    val processingAttempted: RDD[Either[NotProcessableRecord, T]] =
      session.sparkContext.textFile(path)
      .mapPartitions(parsePartitionToFieldValueMaps(_, format))
      .map {
        case (line: String, jsObject) =>
          implicit val l = line
          jsObjectToCaseClass(jsObject, expectedSchema)
      }

    (session.createDataset(processingAttempted.flatMap(_.right.toOption))(Encoders.product[T]),
      session.createDataset(processingAttempted.flatMap(_.left.toOption))(Encoders.product[NotProcessableRecord]))
  }

  // BELOW NOT FULLY IMPLEMENTED AND NEEDS TIDY

  def jsObjectToCaseClass[T <: Product : CaseClassFromMap](fieldsToValues: Map[String, Any],
                                                           expectedSchema: StructType)
                                                          (implicit line: String): Either[NotProcessableRecord, T] = {
    validateAndConvertTypes(fieldsToValues, expectedSchema) match {
      case Left(fail) => Left[NotProcessableRecord, T](fail)
      case Right(map) => Right[NotProcessableRecord, T](CaseClassFromMap[T](map))
    }
  }

  def validateAndConvertTypes(fieldsToValues: Map[String, Any],
                              expectedSchema: StructType)(implicit line: String): Either[NotProcessableRecord, Map[String, Any]] = {
    val validatedFields: Map[String, Either[NotProcessableRecord, Any]] =
      expectedSchema.fields.flatMap {
        case StructField(name, StringType, nullable, metadata) =>
          getField(name, fieldsToValues, nullable)

        // Need to convert strings to timestamps
        case StructField(name, TimestampType, nullable, metadata) => ???
        // need to convert BigDecimal to one of these
        case StructField(name, IntegerType, nullable, metadata) => ???
        case StructField(name, LongType, nullable, metadata) => ???
        case StructField(name, DoubleType, nullable, metadata) => ???
        case StructField(name, structType: StructType, nullable, metadata) =>
          getField(name, fieldsToValues, nullable).map {
            case (name, Right(struct: Map[_, _])) if struct.keySet.forall(_.isInstanceOf[String]) && struct.nonEmpty =>
              name -> validateAndConvertTypes(struct.asInstanceOf[Map[String, Any]], structType)
            case (name, Right(value)) =>
              // Error expected struct field but got something else
              ???
            case (name, Left(error)) => ???
          }

          ???
      }
      .toMap

    // Should we aggregate all errors into a List? Or is taking the first error enough?

    validatedFields.find(_._2.isLeft).map(_._2.left.get).map(Left[NotProcessableRecord, Map[String, Any]])
    .getOrElse(Right[NotProcessableRecord, Map[String, Any]](validatedFields.mapValues(_.right.get)))
  }

  def getField(name: String,
               fieldsToValues: Map[String, Any],
               nullable: Boolean)
              (implicit line: String): Option[(String, Either[NotProcessableRecord, Any])] = {
    val valueOption: Option[Any] = fieldsToValues.get(name)

    if (nullable)
      if (valueOption.isDefined)
        Some(name -> Right[NotProcessableRecord, Any](valueOption.get))
      else
        None
    else
      Some(name -> valueOption.toRight[NotProcessableRecord](NotProcessableRecord(
        recordLine = line,
        notProcessableReasonType = "MissingField",
        notProcessableReasonMessage = "Missing non nullable field",
        None
      )))
  }

  // Dates will be left as strings, and converted by jsObjectToCaseClass
  def parsePartitionToFieldValueMaps(partition: Iterator[String], format: Format): Iterator[(String, Map[String, Any])] = {
    format match {
      case JSON(_, _) => partition.map(line => line -> jsObjectToMap(line.parseJson.asJsObject))
      case CSV(hasHeader, quotedStrings, _, _) =>
        // Should use apache commons CSV parser

        ???
    }
  }

  // TODO Easy
  def jsObjectToMap(jsObject: JsObject): Map[String, Any] = ???
}
