import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.ScalaReflection._
import org.apache.spark.sql.types._
import spray.json.JsonParser.ParsingException
import spray.json._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import io.circe._
import io.circe.parser._


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
                                stackTrace: Option[String] = None)

object ReadValidated {
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

  def apply[LT <: Product : TypeTag : ClassTag : Decoder, T: TypeTag](session: SparkSession,
                                                                      path: String,
                                                                      format: Format): (Dataset[LT], Dataset[NotProcessableRecord]) = {
    val processingAttempted: RDD[Either[NotProcessableRecord, LT]] =
      session.sparkContext.textFile(path)
      .mapPartitions(parsePartitionToFieldValueMaps(_, format))
      .map {
        case Right((line: String, fieldsToValues)) =>
          implicit val l = line
          validateAndConvertTypes(fieldsToValues, structFor[T], format) match {
            case Left(fail) => Left[NotProcessableRecord, LT](fail.toNotProcessableRecord)
            case Right(map) => Right[NotProcessableRecord, LT](CirceJsonUtils.mapToCaseClass[LT](map))
          }
        case Left(parseFail) => Left[NotProcessableRecord, LT](parseFail.toNotProcessableRecord)
      }

    (session.createDataset(processingAttempted.flatMap(_.right.toOption))(Encoders.product[LT]),
      session.createDataset(processingAttempted.flatMap(_.left.toOption))(Encoders.product[NotProcessableRecord]))
  }

  // Will return None for nulls/missing fields if and only if the field is nullable
  // This will never return an actual null in the map since nullability is determined by Optionality
  def validateAndConvertTypes(fieldsToValues: Map[String, Any],
                              expectedSchema: StructType,
                              format: Format,
                              fieldPrefix: String = "")
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
                notProcessableReasonMessage =
                  s"Field ${fieldPath(fieldPrefix, name)}. Expected String but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, BooleanType, nullable, _) =>
          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case correctType@(Right(None) | Right(Some(_: Boolean))) if nullable => correctType
            case correctType@Right(_: Boolean) if !nullable => correctType
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage =
                  s"Field ${fieldPath(fieldPrefix, name)}. Expected Boolean but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, TimestampType, nullable, _) =>
          require(format.dateFormat.isDefined)

          def parseTimeStampString(field: String): Either[NotProcessableRecordTyped, Any] = try {
            if (nullable) rightAny(Some(DateTimeStringToLong(field, format.dateFormat.get)))
            else rightAny(DateTimeStringToLong(field, format.dateFormat.get))


            //            if (nullable) rightAny(Some(dateTimeStringToLong(field, format.dateFormat.get)))
            //            else rightAny(dateTimeStringToLong(field, format.dateFormat.get))
          } catch {
            case e: Throwable =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = InvalidTimestamp,
                notProcessableReasonMessage =
                  s"Field ${fieldPath(fieldPrefix, name)}. Could not parse timestamp see stack trace",
                stackTrace = Some(StackTraceToString(e))
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
                notProcessableReasonMessage =
                  s"Field ${fieldPath(fieldPrefix, name)}. Expected Timestamp String but found field: " + wrongTypeField,
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
                notProcessableReasonMessage =
                  s"Field ${fieldPath(fieldPrefix, name)}. Expected Int but found field: " + wrongTypeField,
                //                  + ", class: " + wrongTypeField.asInstanceOf[Option[_]].get.getClass,
                stackTrace = None
              ))
          }

        case StructField(name, LongType, nullable, _) =>
          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case correctType@(Right(None) | Right(Some(_: Long))) if nullable => correctType
            case correctType@Right(_: Long) => correctType

            // TODO Need tests for these two cases
            case correctType@Right(None) if nullable => correctType
            case Right(Some(i: Int)) if nullable => rightAny(Some(i.toLong))
            case Right(i: Int) => rightAny(i.toLong)


            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage =
                  s"Field ${fieldPath(fieldPrefix, name)}. Expected Long but found field: " + wrongTypeField,
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
                notProcessableReasonMessage =
                  s"Field ${fieldPath(fieldPrefix, name)}. Expected Double but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, ArrayType(dataType, containsNull), nullable, _) =>
          // For some reason even though the default for ArrayType is true, using schemaFor it gives false
          val containsNull = true

          def recurse(array: Vector[Any]): Either[NotProcessableRecordTyped, Any] = {
            val indexed: Map[String, Any] = array.zipWithIndex.toMap.mapValues(_.toString).map(_.swap)

            validateAndConvertTypes(indexed, StructType(indexed.map {
              case (index, _) => StructField(index, dataType, containsNull)
            }.toSeq), format, fieldPath(fieldPrefix, name)) match {
              case Right(map: Map[String, Any]) =>
                // Circe version
                //                rightAny(map.map(_.swap).mapValues(_.toInt).toVector.sortBy(_._2).map {
                // Spray version
                rightAny(map.map(_.swap).mapValues(_.toInt).toList.sortBy(_._2).map {
                  case (Some(thing), _) => thing
                  case (None, _) => null
                })
              case Left(fail) => leftAny(fail)
            }
          }

          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail

            // For Spray version
            //            case Right(array: List[_]) if containsNull && !nullable => recurse(array.toVector)
            //            case Right(Some(array: List[_])) if containsNull && nullable => recurse(array.toVector) match {
            //              case Right(map) => rightAny(Some(map))
            //              case Left(fail) => leftAny(fail)
            //            }

            // For Circe version
            case Right(array: Vector[_]) if containsNull && !nullable => recurse(array)
            case Right(Some(array: Vector[_])) if containsNull && nullable => recurse(array) match {
              case Right(map) => rightAny(Some(map))
              case Left(fail) => leftAny(fail)
            }


            case correctType@Right(None) if containsNull && nullable => correctType
            case Right(wrongTypeField) =>
              leftAny(NotProcessableRecordTyped(
                recordLine = line,
                notProcessableReasonType = IncorrectType,
                notProcessableReasonMessage =
                  s"Field ${fieldPath(fieldPrefix, name)}. Expected Array but found field: " + wrongTypeField,
                stackTrace = None
              ))
          }

        case StructField(name, structType: StructType, nullable, _) =>
          getField(name, fieldsToValues, nullable) match {
            case nullFail: Left[NotProcessableRecordTyped, Any] => nullFail
            case Right(struct: Map[_, _]) if struct.keySet.forall(_.isInstanceOf[String]) && struct.nonEmpty && !nullable =>
              validateAndConvertTypes(struct.asInstanceOf[Map[String, Any]], structType, format, fieldPath(fieldPrefix, name))
            case Right(Some(struct: Map[_, _])) if struct.keySet.forall(_.isInstanceOf[String]) && struct.nonEmpty && nullable =>
              validateAndConvertTypes(struct.asInstanceOf[Map[String, Any]], structType, format, fieldPath(fieldPrefix, name)) match {
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

  def fieldPath(fieldPrefix: String, name: String): String =
    if (fieldPrefix == "") name else fieldPrefix + "." + name

  def getField(name: String,
               fieldsToValues: Map[String, Any],
               nullable: Boolean)
              (implicit line: String): Either[NotProcessableRecordTyped, Any] = {
    val valueOption: Option[Any] = fieldsToValues.get(name).flatMap(Option(_))

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
  // Using Circe because
  // (a) it returns Either,
  // (b) the error message is concise, so can be included in the notProcessableReasonMessage
  // (c) intend to use it instead of spray
  // TODO We should replace all other occurances of spray with Circe
  def parsePartitionToFieldValueMaps(partition: Iterator[String],
                                     format: Format): Iterator[Either[NotProcessableRecordTyped, (String, Map[String, Any])]] = {
    format match {
      case JSON(_) =>

        partition.map(line => parse(line) match {
          case Right(json) => Right[NotProcessableRecordTyped, (String, Map[String, Any])](line -> CirceJsonUtils.jsonToMap(json))
          case Left(parsingFailure) => Left[NotProcessableRecordTyped, (String, Map[String, Any])](NotProcessableRecordTyped(
            recordLine = line,
            notProcessableReasonType = InvalidJSON,
            notProcessableReasonMessage = "Could not parse json: " + parsingFailure.message,
            stackTrace = Some(StackTraceToString(parsingFailure.underlying))
          ))
        })

      case CSV(hasHeader, quotedStrings, _, _) =>
        // Should use apache commons CSV parser

        ???
    }
  }
}
