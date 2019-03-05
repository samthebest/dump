
import java.io.StringWriter

import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.ScalaReflection._
import org.apache.spark.sql.types._
import spray.json._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

sealed trait Format
case class JSON(dateFormat: Option[String] = None) extends Format
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
                                                                  expectedSchema: StructType = structFor[T]): (Dataset[T], Dataset[NotProcessableRecord]) = {
    // TODO Move logging & counting here
    val processingAttempted: RDD[Either[NotProcessableRecord, T]] =
      session.sparkContext.textFile(path)
      .mapPartitions(parsePartitionToFieldValueMaps(_, format))
      .map {
        case Right((line: String, fieldsToValues)) =>
          implicit val l = line
          validateAndConvertTypes(fieldsToValues, expectedSchema, format) match {
            case Left(fail) => Left[NotProcessableRecord, T](fail)
            case Right(map) => Right[NotProcessableRecord, T](CaseClassFromMap[T](map))
          }
        case Left(parseFail) => Left[NotProcessableRecord, T](parseFail)
      }

    (session.createDataset(processingAttempted.flatMap(_.right.toOption))(Encoders.product[T]),
      session.createDataset(processingAttempted.flatMap(_.left.toOption))(Encoders.product[NotProcessableRecord]))
  }

  def validateAndConvertTypes(fieldsToValues: Map[String, Any],
                              expectedSchema: StructType,
                              format: Format)
                             (implicit line: String): Either[NotProcessableRecord, Map[String, Any]] = {
    val validatedFields: Map[String, Either[NotProcessableRecord, Any]] =
      expectedSchema.fields.flatMap {
        case StructField(name, StringType, nullable, metadata) =>
          getField(name, fieldsToValues, nullable)

        // Need to convert strings to timestamps
        case StructField(name, TimestampType, nullable, metadata) =>
          getField(name, fieldsToValues, nullable).map {
            case (name, nullFail: Left[_, _]) => (name, nullFail)
            case (name, Right(field)) => ???
          }
        // need to convert BigDecimal to one of these
        case StructField(name, IntegerType, nullable, metadata) => ???
        case StructField(name, LongType, nullable, metadata) => ???
        case StructField(name, DoubleType, nullable, metadata) => ???
        case StructField(name, structType: StructType, nullable, metadata) =>
          getField(name, fieldsToValues, nullable).map {
            case (name, Right(struct: Map[_, _])) if struct.keySet.forall(_.isInstanceOf[String]) && struct.nonEmpty =>
              name -> validateAndConvertTypes(struct.asInstanceOf[Map[String, Any]], structType, format)
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

  // Unit tests to handle:
  //
  // nullable and exists
  // nullable and not exist
  // nullable and exists as null
  // not nullable and exists
  // not nullable and not exist
  // not nullable and exists as null
  def getField(name: String,
               fieldsToValues: Map[String, Any],
               nullable: Boolean)
              (implicit line: String): Option[(String, Either[NotProcessableRecord, Any])] = {
    val valueOption: Option[Any] = fieldsToValues.get(name).flatMap(Option(_))

    if (nullable)
      if (valueOption.isDefined)
        if (valueOption.get == null)
          None
        else
          Some(name -> Right[NotProcessableRecord, Any](valueOption.get))
      else
        None
    else
      Some(name -> valueOption.toRight[NotProcessableRecord](NotProcessableRecord(
        recordLine = line,
        notProcessableReasonType = "MissingField",
        notProcessableReasonMessage = "Missing non nullable field: " + name,
        None
      )))
  }

  // Dates will be left as strings, and converted by validateAndConvertTypes
  def parsePartitionToFieldValueMaps(partition: Iterator[String], format: Format): Iterator[Either[NotProcessableRecord, (String, Map[String, Any])]] = {
    format match {
      case JSON(_) => partition.map(line =>
        try {
          Right[NotProcessableRecord, (String, Map[String, Any])](line -> jsObjectToMap(line.parseJson.asJsObject))
        } catch {
          case e: DeserializationException =>
            Left[NotProcessableRecord, (String, Map[String, Any])](NotProcessableRecord(
              recordLine = line,
              notProcessableReasonType = "InvalidJSON",
              notProcessableReasonMessage = "Invalid JSON see stack trace",
              stackTrace = Some(stackTraceToString(e))
            ))
        })
      case CSV(hasHeader, quotedStrings, _, _) =>
        // Should use apache commons CSV parser

        ???
    }
  }

  def jsObjectToMap(jsObject: JsObject): Map[String, Any] = {
    jsObject.fields.mapValues {
      case JsNumber(n) => n
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case jsObject: JsObject => jsObjectToMap(jsObject)
    }
  }

  val bigIntDecimal = DecimalType(38, 0)

  def structFor[T: TypeTag]: StructType = schemaFor(localTypeOf[T]).dataType.asInstanceOf[StructType]

  // Copy and pasted from Spark, with all nullable = true change to false for everything except Option
  // We should write our own that doesn't depend on any Spark and stop using StructType
  def schemaFor(tpe: `Type`): Schema = cleanUpReflectionObjects {
    tpe.dealias match {
      // this must be the first case, since all objects in scala are instances of Null, therefore
      // Null type would wrongly match the first of them, which is Option as of now
      case t if t <:< definitions.NullTpe => Schema(NullType, nullable = true)
      //      case t if t.typeSymbol.annotations.exists(_.tree.tpe =:= typeOf[SQLUserDefinedType]) =>
      //        val udt = getClassFromType(t).getAnnotation(classOf[SQLUserDefinedType]).udt().newInstance()
      //        Schema(udt, nullable = true)
      //      case t if UDTRegistration.exists(getClassNameFromType(t)) =>
      //        val udt = UDTRegistration.getUDTFor(getClassNameFromType(t)).get.newInstance()
      //                  .asInstanceOf[UserDefinedType[_]]
      //        Schema(udt, nullable = true)
      case t if t <:< localTypeOf[Option[_]] =>
        val TypeRef(_, _, Seq(optType)) = t
        Schema(schemaFor(optType).dataType, nullable = true)
      case t if t <:< localTypeOf[Array[Byte]] => Schema(BinaryType, nullable = false)
      case t if t <:< localTypeOf[Array[_]] =>
        val TypeRef(_, _, Seq(elementType)) = t
        val Schema(dataType, nullable) = schemaFor(elementType)
        Schema(ArrayType(dataType, containsNull = nullable), nullable = false)
      case t if t <:< localTypeOf[Seq[_]] =>
        val TypeRef(_, _, Seq(elementType)) = t
        val Schema(dataType, nullable) = schemaFor(elementType)
        Schema(ArrayType(dataType, containsNull = nullable), nullable = false)
      case t if t <:< localTypeOf[Map[_, _]] =>
        val TypeRef(_, _, Seq(keyType, valueType)) = t
        val Schema(valueDataType, valueNullable) = schemaFor(valueType)
        Schema(MapType(schemaFor(keyType).dataType,
          valueDataType, valueContainsNull = valueNullable), nullable = false)
      case t if t <:< localTypeOf[Set[_]] =>
        val TypeRef(_, _, Seq(elementType)) = t
        val Schema(dataType, nullable) = schemaFor(elementType)
        Schema(ArrayType(dataType, containsNull = nullable), nullable = false)
      case t if t <:< localTypeOf[String] => Schema(StringType, nullable = false)
      case t if t <:< localTypeOf[java.sql.Timestamp] => Schema(TimestampType, nullable = false)
      case t if t <:< localTypeOf[java.sql.Date] => Schema(DateType, nullable = false)
      case t if t <:< localTypeOf[BigDecimal] => Schema(DecimalType.SYSTEM_DEFAULT, nullable = false)
      case t if t <:< localTypeOf[java.math.BigDecimal] =>
        Schema(DecimalType.SYSTEM_DEFAULT, nullable = false)
      case t if t <:< localTypeOf[java.math.BigInteger] =>
        Schema(bigIntDecimal, nullable = false)
      case t if t <:< localTypeOf[scala.math.BigInt] =>
        Schema(bigIntDecimal, nullable = false)
      case t if t <:< localTypeOf[Decimal] => Schema(DecimalType.SYSTEM_DEFAULT, nullable = false)
      case t if t <:< localTypeOf[java.lang.Integer] => Schema(IntegerType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Long] => Schema(LongType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Double] => Schema(DoubleType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Float] => Schema(FloatType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Short] => Schema(ShortType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Byte] => Schema(ByteType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Boolean] => Schema(BooleanType, nullable = false)
      case t if t <:< definitions.IntTpe => Schema(IntegerType, nullable = false)
      case t if t <:< definitions.LongTpe => Schema(LongType, nullable = false)
      case t if t <:< definitions.DoubleTpe => Schema(DoubleType, nullable = false)
      case t if t <:< definitions.FloatTpe => Schema(FloatType, nullable = false)
      case t if t <:< definitions.ShortTpe => Schema(ShortType, nullable = false)
      case t if t <:< definitions.ByteTpe => Schema(ByteType, nullable = false)
      case t if t <:< definitions.BooleanTpe => Schema(BooleanType, nullable = false)
      case t if definedByConstructorParams(t) =>
        val params = getConstructorParameters(t)
        Schema(StructType(
          params.map { case (fieldName, fieldType) =>
            val Schema(dataType, nullable) = schemaFor(fieldType)
            StructField(fieldName, dataType, nullable)
          }), nullable = false)
      case other =>
        throw new UnsupportedOperationException(s"Schema for type $other is not supported")
    }
  }

  def stackTraceToString(e: Exception): String = {
    import java.io.PrintWriter
    val sw = new StringWriter()
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}
