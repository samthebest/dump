package foo

import org.apache.spark.sql._
import org.apache.spark.sql.types._
import spray.json._

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
  def apply[T <: Product : TypeTag : JsonReader](session: SparkSession,
                                                 path: String,
                                                 format: Format,
                                                 expectedSchema: StructType): (Dataset[T], Dataset[NotProcessableRecord]) = {

    val processingAttempted =
      session.sparkContext.textFile(path)
      .mapPartitions(parsePartitionToFieldValueMaps(_, format))
      .map(jsObjectToCaseClass(_, expectedSchema))

    (session.createDataset(processingAttempted.flatMap(_.right.toOption))(Encoders.product[T]),
      session.createDataset(processingAttempted.flatMap(_.left.toOption))(Encoders.product[NotProcessableRecord]))
  }

  def jsObjectToCaseClass[T: JsonReader](jsObject: JsObject,
                                         expectedSchema: StructType): Either[NotProcessableRecord, T] = {
    JsObject()

    // Dates need to be converted to spark.sql.Timestamp
    // Numbers need to be convereted to Double, Long, Int
    val jsObjectWithDates: JsObject = ???

    Right[NotProcessableRecord, T](jsObjectWithDates.convertTo[T])
  }

  def validateAndConvertDates(jsObject: JsObject, expectedSchema: StructType): Either[NotProcessableRecord, JsObject] = {

    val validatedFields: Map[String, Either[NotProcessableRecord, JsObject]]
    expectedSchema.fields.map {
      case StructField(name, StringType, nullable, metadata) => jsObject.fields
      case StructField(name, TimestampType, nullable, metadata) => ??? // TODO
      case StructField(name, IntegerType, nullable, metadata) => jsObject.fields
      case StructField(name, LongType, nullable, metadata) => jsObject.fields
      case StructField(name, DoubleType, nullable, metadata) => jsObject.fields
    }

    JsObject(expectedSchema.fields.map {
      case StructField(name, dataType, nullable, metadata) => jsObject.fields
    })
  }

  // Dates will be left as strings, and converted by jsObjectToCaseClass
  def parsePartitionToFieldValueMaps(partition: Iterator[String], format: Format): Iterator[JsObject] = {
    format match {
      case JSON(_, _) => partition.map(_.parseJson.asJsObject)
      case CSV(hasHeader, quotedStrings, _, _) =>
        // Should use apache commons CSV parser

        ???
    }
  }
}
