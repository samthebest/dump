
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import spray.json._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

sealed trait Format
case class JSON(ignoreAdditional: Boolean, dateFormat: Option[String]) extends Format
case class CSV(hasHeader: Boolean,
               quotedStrings: Boolean,
               ignoreAdditional: Boolean,
               dateFormat: Option[String]) extends Format

case class NotProcessableRecord(recordLine: String,
                                notProcessableReasonType: String,
                                notProcessableReasonMessage: String,
                                stackTrace: Option[String])

object ReadValidated {
  def apply[T <: Product : TypeTag : JsonReader : ClassTag](session: SparkSession,
                                                            path: String,
                                                            format: Format,
                                                            expectedSchema: StructType): (Dataset[T], Dataset[NotProcessableRecord]) = {

    val processingAttempted: RDD[Either[NotProcessableRecord, T]] =
      session.sparkContext.textFile(path)
      .mapPartitions(parsePartitionToFieldValueMaps(_, format))
      .map {
        case (line, jsObject) =>
          jsObjectToCaseClass(line, jsObject, expectedSchema)
      }

    (session.createDataset(processingAttempted.flatMap(_.right.toOption))(Encoders.product[T]),
      session.createDataset(processingAttempted.flatMap(_.left.toOption))(Encoders.product[NotProcessableRecord]))
  }

  def jsObjectToCaseClass[T: JsonReader](line: String, jsObject: JsObject,
                                         expectedSchema: StructType): Either[NotProcessableRecord, T] = {
    JsObject()

    // Dates need to be converted to spark.sql.Timestamp
    // Numbers need to be convereted to Double, Long, Int
    val jsObjectWithDates: JsObject = ???

    Right[NotProcessableRecord, T](jsObjectWithDates.convertTo[T])
  }

  def validateAndConvertDates(line: String, jsObject: JsObject,
                              expectedSchema: StructType): Either[NotProcessableRecord, JsObject] = {

    val validatedFields: Map[String, Either[NotProcessableRecord, JsObject]] =
      expectedSchema.fields.flatMap {
        case StructField(name, StringType, nullable, metadata) =>
          val valueOption: Option[JsObject] = jsObject.fields.get(name).map(_.asJsObject)
          
          if (nullable)
            if (valueOption.isDefined)
              Some(name -> Right[NotProcessableRecord, JsObject](valueOption.get))
            else
              None
          else
            Some(name -> valueOption.toRight[NotProcessableRecord](NotProcessableRecord(
              recordLine = line,
              notProcessableReasonType = "MissingField",
              notProcessableReasonMessage = "Missing non nullable field",
              None
            )))

        case StructField(name, TimestampType, nullable, metadata) => ??? // TODO
        case StructField(name, IntegerType, nullable, metadata) => ???
        case StructField(name, LongType, nullable, metadata) => ???
        case StructField(name, DoubleType, nullable, metadata) => ???
      }
      .toMap

    //    JsObject(expectedSchema.fields.map {
    //      case StructField(name, dataType, nullable, metadata) => jsObject.fields
    //    })

    ???
  }

  // Dates will be left as strings, and converted by jsObjectToCaseClass
  def parsePartitionToFieldValueMaps(partition: Iterator[String], format: Format): Iterator[(String, JsObject)] = {
    format match {
      case JSON(_, _) => partition.map(line => line -> line.parseJson.asJsObject)
      case CSV(hasHeader, quotedStrings, _, _) =>
        // Should use apache commons CSV parser

        ???
    }
  }
}
