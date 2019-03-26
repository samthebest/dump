
import java.sql.Timestamp

import io.circe.{Decoder, Json}
import io.circe.Json.{JNumber, JString}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object CirceJsonUtils {
  import cats.syntax.either._

  import io.circe.Decoder
  import io.circe.Encoder

  implicit val decodeTimestamp: Decoder[Timestamp] = Decoder.decodeLong.emap(long =>
    Either.catchNonFatal(new Timestamp(long)).leftMap(_ => "Timestamp")
  )

  implicit val encodeTimestamp: Encoder[Timestamp] = Encoder.encodeLong.contramap[Timestamp](_.getTime)

  def mapToJson(map: Map[String, Any]): Json =
    map.mapValues(anyToJson).asJson

  def anyToJson(any: Any): Json = any match {
    case n: Int => n.asJson
    case n: Long => n.asJson
    case n: Timestamp => n.asJson
    case n: Double => n.asJson
    case s: String => s.asJson
    case true => true.asJson
    case false => false.asJson
    case null | None => None.asJson
    case list: List[_] => list.map(anyToJson).asJson
    case list: Vector[_] => list.map(anyToJson).asJson
    case Some(any) => anyToJson(any)
    case map: Map[String, Any] => mapToJson(map)
  }

  def jsonToAny(json: Json): Any = json match {
    case _ if json.isNull => null
    case _ if json.isNumber =>
      json.asNumber.get.toBigDecimal.get match {
        case n: BigDecimal if n.isValidInt  => json.as[Int].right.get
        case n: BigDecimal if n.isValidLong  => json.as[Long].right.get
        case n: BigDecimal if n.isDecimalDouble => json.as[Double].right.get
      }
    case _ if json.isString => json.as[String].right.get
    case _ if json.isBoolean => json.as[Boolean].right.get
    case _ if json.isArray => json.asArray.get.map(jsonToAny)
    case _ if json.isObject => jsonToMap(json)
  }

  def jsonToMap(json: Json): Map[String, Any] =
    json.asObject.get.toMap.mapValues(jsonToAny)

  def mapToCaseClass[T : Decoder](map: Map[String, Any]): T = mapToJson(map).as[T].right.get
}
