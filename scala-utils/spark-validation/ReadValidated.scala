

import java.io.PrintWriter
import java.io.StringWriter
import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import JsonUtils._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.ScalaReflection._
import org.apache.spark.sql.types._
import spray.json.JsonParser.ParsingException
import spray.json._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

sealed trait Format {
  val dateFormat: Option[String]
}
case class JSON(dateFormat: Option[String] = None) extends Format
case class CSV(hasHeader: Boolean,
               quotedStrings: Boolean,
               ignoreAdditional: Boolean = true,
               dateFormat: Option[String] = None) extends Format

// I hate Either, want to use Xor from cats, or \/ from Scalaz instead

sealed trait NotProcessableReasonType

case object IncorrectType extends NotProcessableReasonType
case object MissingField extends NotProcessableReasonType
case object InvalidTimestamp extends NotProcessableReasonType
case object InvalidJSON extends NotProcessableReasonType

// Could have a second case class without recordLine and populate it later, would mean we don't have to keep passing it down
case class NotProcessableRecordTyped(recordLine: String,
                                     notProcessableReasonType: NotProcessableReasonType,
                                     notProcessableReasonMessage: String,
                                     stackTrace: Option[String])

case class NotProcessableRecord(recordLine: String,
                                notProcessableReasonType: String,
                                notProcessableReasonMessage: String,
                                stackTrace: Option[String])

object ReadValidated {
  // TODO Move logging & counting here with a version that only returns the Dataset[T], and returns it cached
  def readLogAndCache() = ???

  val leftAny: NotProcessableRecordTyped => Left[NotProcessableRecordTyped, Any] = Left[NotProcessableRecordTyped, Any]
  val rightAny: Any => Right[NotProcessableRecordTyped, Any] = Right[NotProcessableRecordTyped, Any]


  implicit class PimpedNotProcessableRecordTyped(r: NotProcessableRecordTyped) {
    def toNotProcessableRecord: NotProcessableRecord = NotProcessableRecord(
      recordLine = r.recordLine,
      notProcessableReasonType = r.notProcessableReasonType.toString,
      notProcessableReasonMessage = r.notProcessableReasonMessage,
      stackTrace = r.stackTrace
    )
  }

  def apply[T <: Product : TypeTag : ClassTag : CaseClassFromMap](session: SparkSession,
                                                                  path: String,
                                                                  format: Format): (Dataset[T], Dataset[NotProcessableRecord]) = {
    val processingAttempted: RDD[Either[NotProcessableRecord, T]] =
      session.sparkContext.textFile(path)
      .mapPartitions(parsePartitionToFieldValueMaps(_, format))
      .map {
        case Right((line: String, fieldsToValues)) =>
          implicit val l = line
          validateAndConvertTypes(fieldsToValues, structFor[T], format) match {
            case Left(fail) => Left[NotProcessableRecord, T](fail.toNotProcessableRecord)
            case Right(map) => Right[NotProcessableRecord, T](CaseClassFromMap[T](map))
          }
        case Left(parseFail) => Left[NotProcessableRecord, T](parseFail.toNotProcessableRecord)
      }

    (session.createDataset(processingAttempted.flatMap(_.right.toOption))(Encoders.product[T]),
      session.createDataset(processingAttempted.flatMap(_.left.toOption))(Encoders.product[NotProcessableRecord]))
  }

  def validateAndConvertTypes(fieldsToValues: Map[String, Any],
                              expectedSchema: StructType,
                              format: Format)
                             (implicit line: String): Either[NotProcessableRecordTyped, Map[String, Any]] = {
    val validatedFields: Map[String, Either[NotProcessableRecordTyped, Any]] =
      expectedSchema.fields.map {
        case StructField(name, StringType, nullable, _) =>
          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case correctType@(Right(None) | Right(Some(_: String))) if nullable => correctType
            case correctType@Right(_: String) if !nullable => correctType
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage = "Expected String but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, TimestampType, nullable, _) =>
          def parseTimeStampString(field: String): Either[NotProcessableRecordTyped, Any] = try {
            if (nullable) rightAny(Some(dateTimeStringToTimestamp(field, format.dateFormat.get)))
            else rightAny(dateTimeStringToTimestamp(field, format.dateFormat.get))
          } catch {
            case e: Throwable =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = InvalidTimestamp,
                notProcessableReasonMessage = "Could not parse timestamp see stack trace",
                stackTrace = Some(stackTraceToString(e))
              ))
          }

          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case Right(field: String) if !nullable => parseTimeStampString(field)
            case Right(Some(field: String)) if nullable => parseTimeStampString(field)
            case correctType@Right(None) if nullable => correctType
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage = "Expected Timestamp String but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, IntegerType, nullable, _) =>
          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case correctType@(Right(None) | Right(Some(_: Int))) if nullable => correctType
            case correctType@Right(_: Int) => correctType
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage = "Expected Int but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, LongType, nullable, _) =>
          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case correctType@(Right(None) | Right(Some(_: Long))) if nullable => correctType
            case correctType@Right(_: Long) => correctType
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage = "Expected Long but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, DoubleType, nullable, _) =>
          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case correctType@(Right(None) | Right(Some(_: Double))) if nullable => correctType
            case correctType@Right(_: Double) if !nullable => correctType
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage = "Expected Double but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, ArrayType(dataType, containsNull), nullable, _) =>
          require(containsNull, "We do not currently support arrays that are not allowed to contain null as one would" +
            " have to explicitly create a StructType rather than using schemaFor")

          def recurse(array: List[Any]): Either[NotProcessableRecordTyped, Any] = {
            val indexed: Map[String, Any] = array.zipWithIndex.toMap.mapValues(_.toString).map(_.swap)

            validateAndConvertTypes(indexed, StructType(indexed.map {
              case (index, _) => StructField(index, dataType, containsNull)
            }.toSeq), format) match {
              case Right(map: Map[String, Any]) =>
                rightAny(map.map(_.swap).mapValues(_.toInt).toList.sortBy(_._2).map {
                  case (Some(thing), _) => thing
                  case (None, _) => null
                })
              case Left(fail) => leftAny(fail)
            }
          }

          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case Right(array: List[_]) if containsNull && !nullable => recurse(array)
            case Right(Some(array: List[_])) if containsNull && nullable => recurse(array) match {
              case Right(map) => rightAny(Some(map))
              case Left(fail) => leftAny(fail)
            }
            case correctType@Right(None) if containsNull && nullable => correctType
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage = "Expected Array but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, structType: StructType, nullable, _) =>
          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case Right(struct: Map[_, _]) if struct.keySet.forall(_.isInstanceOf[String]) && struct.nonEmpty && !nullable =>
              validateAndConvertTypes(struct.asInstanceOf[Map[String, Any]], structType, format)
            case Right(Some(struct: Map[_, _])) if struct.keySet.forall(_.isInstanceOf[String]) && struct.nonEmpty && nullable =>
              validateAndConvertTypes(struct.asInstanceOf[Map[String, Any]], structType, format) match {
                case Left(fail) => leftAny(fail)
                case Right(map) => rightAny(Some(map))
              }
            case Right(None) if nullable => rightAny(None)
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage = "Expected Struct but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }
      }
      .zip(expectedSchema.fields.map(_.name)).map(_.swap)
      .toMap

    validatedFields.find(_._2.isLeft).map(_._2.left.get).map(Left[NotProcessableRecordTyped, Map[String, Any]])
    .getOrElse(Right[NotProcessableRecordTyped, Map[String, Any]](validatedFields.mapValues(_.right.get)))
  }

  def getField(name: String,
               fieldsToValues: Map[String, Any],
               nullable: Boolean)
              (implicit line: String): Either[NotProcessableRecordTyped, Any] = {
    val valueOption: Option[Any] = fieldsToValues.get(name).flatMap(Option(_))

    //    println(s"getField:  name = $name, fieldsToValues = $fieldsToValues, nullable = $nullable")

    if (nullable)
      Right[NotProcessableRecordTyped, Any](valueOption)
    else
      valueOption.toRight[NotProcessableRecordTyped](NotProcessableRecordTyped(
        recordLine = line,
        notProcessableReasonType = MissingField,
        notProcessableReasonMessage = "Missing non nullable field: " + name,
        None
      ))
  }

  def parsePartitionToFieldValueMaps(partition: Iterator[String], format: Format): Iterator[Either[NotProcessableRecordTyped, (String, Map[String, Any])]] = {
    format match {
      case JSON(_) => partition.map(line =>
        try {
          Right[NotProcessableRecordTyped, (String, Map[String, Any])](line -> jsObjectToMap(line.parseJson.asJsObject))
        } catch {
          case e@(_: DeserializationException | _: ParsingException) =>
            Left[NotProcessableRecordTyped, (String, Map[String, Any])](NotProcessableRecordTyped(
              recordLine = line,
              notProcessableReasonType = InvalidJSON,
              notProcessableReasonMessage = "Could not parse json see stack trace",
              stackTrace = Some(stackTraceToString(e))
            ))
        })
      case CSV(hasHeader, quotedStrings, _, _) =>
        // Should use apache commons CSV parser

        ???
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
          params.map {
            case (fieldName, fieldType) =>
              val Schema(dataType, nullable) = schemaFor(fieldType)
              StructField(fieldName, dataType, nullable)
          }), nullable = false)
      case other =>
        throw new UnsupportedOperationException(s"Schema for type $other is not supported")
    }
  }

  def stackTraceToString(e: Throwable): String = {
    val sw = new StringWriter()
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }

  def dateTimeStringToTimestamp(s: String, pattern: String): Timestamp =
    new Timestamp(LocalDateTime.parse(s, DateTimeFormatter.ofPattern(pattern)).toEpochSecond(ZoneOffset.UTC) * 1000)
}
