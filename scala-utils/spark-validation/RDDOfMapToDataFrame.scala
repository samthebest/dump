import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object RDDOfMapToDataFrame {
  def apply(rdd: RDD[Map[String, Any]], schema: StructType)
           (implicit sparkSession: SparkSession): DataFrame =
    sparkSession.createDataFrame(rdd.map(mapToRow), schema)

  def mapToRow(m: Map[String, Any]): Row = Row.fromSeq(
    m.values.map {
      case struct: Map[String, Any]@unchecked => mapToRow(struct)
        // Intellij is confused by this line, please leave as is
      case mapList@( (_: Map[String, Any] @unchecked) :: _) =>
        mapList.map(_.asInstanceOf[Map[String, Any]]).map(mapToRow)
      case other => other
    }.toSeq
  )
}
