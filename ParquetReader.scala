
import BasicLogger._
import NamingParser
import org.apache.spark.SparkContext.{logInfo => _}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.expressions.Row
import org.apache.spark.sql.{SQLContext, SchemaRDD}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scalaz.Scalaz._

case object ParquetReader {
  def getCaseMethods[T: TypeTag]: List[String] = typeOf[T].members.sorted.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }.toList.map(_.toString)

  def caseClassToSQLCols[T: TypeTag]: List[String] =
    getCaseMethods[T].map(_.split(" ")(1)).map(NamingParser.lowerCamelToUnderscore)

  def schemaRDDToRDD[T: TypeTag : ClassTag](sqlContext: SQLContext)(schemaRDD: SchemaRDD, fac: Row => T): RDD[T] = {
    val tmpName = "tmpTableName"
    schemaRDD.registerAsTable(tmpName)

    logInfo("First record of scheme RDD:")
    println(schemaRDD.take(1))

    val sql = "SELECT " + caseClassToSQLCols[T].mkString(", ") + " FROM " + tmpName
    logInfo("Running SQL: " + sql)
    sqlContext.sql(sql).map(fac)
  }

  def readCSVDirs[T: TypeTag : ClassTag](sqlContext: SQLContext,
                                         p: String,
                                         fac: Row => T,
                                         forcePar: Option[Int] = None): RDD[T] =
    p.split(",").map(p => {
      logInfo("Will read parquet file: " + p)
      sqlContext.parquetFile(p)
    })
    .map(schemaRDDToRDD[T](sqlContext)(_, fac))
    .reduce(_ ++ _) |> (rdd => forcePar.map(i => {
      logInfo("Forcing repartition of " + p + " into " + i + " partitions")
      rdd.repartition(i)
    }).getOrElse(rdd))
}
