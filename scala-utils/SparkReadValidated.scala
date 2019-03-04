package com.asos.datalake.staging

import com.asos.datalake.staging.IO.READING_OPTION_MULTILINE
import org.apache.spark.sql._
import org.apache.spark.sql.types.StructType
import spray.json._

import scala.reflect._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

sealed trait Format
case object JSON extends Format
case object CSV extends Format

case class ReadConfig(path: String, format: Format, hasHeader: Boolean, dateFormat: Option[String])

case class NotProcessableRecord(recordLine: String, notProcessableReason: String, stackTrace: Option[String])

object ReadValidated {
  def apply[T <: Product : TypeTag](session: SparkSession,
                                    readConfig: ReadConfig,
                                    expectedSchema: StructType): (Dataset[T], Dataset[NotProcessableRecord]) = {
    session.sparkContext.textFile(readConfig.path)
    .mapPartitions(parsePartitionToFieldValueMaps(_, readConfig))


    session.read
    .option(READING_OPTION_MULTILINE, value = true)
    .schema(expectedSchema)
    .json(landingPath)
    .as[T](Encoders.product[T])
  }

  def fromMap[T: TypeTag : ClassTag](m: Map[String, _]) = {
    val rm: universe.Mirror = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
    val classTest: universe.ClassSymbol = typeOf[T].typeSymbol.asClass
    val classMirror = rm.reflectClass(classTest)
    val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val constructorMirror: universe.MethodMirror = classMirror.reflectConstructor(constructor)

    typeOf[T]
      .members
      .collect {
        case m: MethodSymbol if m.isCaseAccessor => m
      }
      .toList.map(ms => ms.name.toString -> ms.typeSignature.typeSymbol).toMap

    val constructorArgs = constructor.paramLists.flatten.map((param: Symbol) => {
      val paramName = param.name.toString
      if (param.typeSignature <:< typeOf[Option[Any]])
        m.get(paramName)
      else
        m.get(paramName).getOrElse(throw new IllegalArgumentException("Map is missing required parameter named " + paramName))
    })

    constructorMirror(constructorArgs: _*).asInstanceOf[T]
  }

  def constructorMirrorFromTypeSymbol(symbol: Symbol): MethodMirror = {
    val classTest: universe.ClassSymbol = symbol.asClass
    val classMirror = rm.reflectClass(classTest)
    val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val constructorMirror: universe.MethodMirror = classMirror.reflectConstructor(constructor)
  }

  def fieldValueMapToCaseClass[T](fieldValueMap: Map[String, Any],
                                  expectedSchema: StructType): Either[T, NotProcessableRecord] = {

  }

  def parsePartitionToFieldValueMaps(partition: Iterator[String], readConfig: ReadConfig): Iterator[Map[String, Any]] = {
    readConfig.format match {
      case JSON => partition.map(_.parseJson.asJsObject).map(jsObjectToMap)
    }
  }

  def fieldToTypeMap[T: TypeTag]: Map[String, String] =
    typeOf[T]
      .members
      .collect {
        case m: MethodSymbol if m.isCaseAccessor => m
      }
      .toList.map(ms => ms.name.toString -> ms.typeSignature.typeSymbol.name.toString).toMap


  def jsObjectToMap(jsObject: JsObject): Map[String, Any] = {
    jsObject.fields.mapValues {
      case JsNumber(n) => n
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case jsObject: JsObject => jsObjectToMap(jsObject)
    }
  }

}
