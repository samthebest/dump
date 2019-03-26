
import java.sql.Timestamp

import org.specs2.mutable.Specification
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.Json.{JNumber, JString}
import CirceJsonUtils.decodeTimestamp
import scala.util.{Failure, Success, Try}

case class AddressDouble(street: String, zip: Double)
case class Address(street: String, zip: Int)
case class Person(name: String, address: Address)
case class WithList(names: List[String])
case class PersonOptionalAddress(name: String, address: Option[Address])

case class WithTimestamp(timestamp: Timestamp, foo: String)
case class WithTimestampOptional(timestamp: Option[Timestamp], foo: String)

object CirceJsonUtilsTest extends Specification {
  val defaultSellingTag = SellingTagStringTimes(1234, "foo", "foo", None)

  val withTimestampMap = Map(
    "timestamp" -> 1234L,
    "foo" -> "value"
  )

  val withTimestampOptMap = Map(
    "timestamp" -> Some(1234L),
    "foo" -> "value"
  )

  val withTimestampOptMapMissing = Map(
    "timestamp" -> None,
    "foo" -> "value"
  )

  val withTimestamp = WithTimestamp(timestamp = new Timestamp(1234L), foo = "value")
  val withTimestampOpt = WithTimestampOptional(timestamp = Some(new Timestamp(1234L)), foo = "value")
  val withTimestampOptMissing = WithTimestampOptional(timestamp = None, foo = "value")

  val personJsObject: Json = Map(
    "name" -> "Tom".asJson,
    "address" -> Map(
      "street" -> "Jefferson st".asJson,
      "zip" -> 10000.asJson
    ).asJson).asJson

  val personMap = Map(
    "name" -> "Tom",
    "address" -> Map("street" -> "Jefferson st", "zip" -> 10000)
  )
  val person = Person("Tom", Address("Jefferson st", 10000))
  val withList = WithList(List("one", "two"))

  val addressDouble = AddressDouble("Tom", 1234.1234)
  val addressDoubleMap = Map("street" -> "Tom", "zip" -> 1234.1234)

  val withListMap = Map(
    "names" -> List("one", "two")
  )

  val personOptionalAddressMap = Map(
    "name" -> "Tom",
    "address" -> Some(Map("street" -> "Jefferson st", "zip" -> 10000))
  )

  val personOptionalAddressMapNone = Map(
    "name" -> "Tom",
    "address" -> None
  )

  val personOptionalAddress = PersonOptionalAddress("Tom", Some(Address("Jefferson st", 10000)))
  val personOptionalAddressNone = PersonOptionalAddress("Tom", None)

  "syntax.asJson" should {
    "Handle simple case class" in {
      decode[SellingTagStringTimes](defaultSellingTag.asJson.noSpaces).right.get must_=== defaultSellingTag
    }

    "Handle ???, which has over 22 fields" in {
// TODO
    }
  }

  "CirceJsonUtils.mapToJson" should {
    "Convert Person example map to Person JSON" in {
      CirceJsonUtils.mapToJson(personMap) must_=== person.asJson
    }

    "Convert withListMap to WithList JSON" in {
      CirceJsonUtils.mapToJson(withListMap) must_=== withList.asJson
    }

    "Can convert EnrichedProductStringTimes" in {
      CirceJsonUtils.mapToJson(CirceJsonUtils.jsonToMap(defaultEnrichedProductStringTimes.asJson)) must_===
        defaultEnrichedProductStringTimes.asJson
    }
  }

  "CirceJsonUtils.jsonToMap" should {
    "Convert person to map" in {
      CirceJsonUtils.jsonToMap(person.asJson) must_=== personMap
    }

    "Convert addressDouble to map" in {
      CirceJsonUtils.jsonToMap(addressDouble.asJson) must_=== addressDoubleMap
    }

    "Convert withList map to WithList" in {
      CirceJsonUtils.jsonToMap(withList.asJson) must_=== withListMap
    }
  }

  "CirceJsonUtils.jsonToAny" should {
    "Convert Int" in {
      val converted = CirceJsonUtils.jsonToAny(1234.asJson)
      converted.isInstanceOf[Int] must beTrue
      converted must_=== 1234
    }

    "Convert Long" in {
      val converted = CirceJsonUtils.jsonToAny(2147483647000L.asJson)
      converted.isInstanceOf[Long] must beTrue
      converted must_=== 2147483647000L
    }

    "Convert Double" in {
      CirceJsonUtils.jsonToAny(1234.1234.asJson) must_=== 1234.1234
    }

    "Convert String" in {
      CirceJsonUtils.jsonToAny("foo".asJson) must_=== "foo"
    }

    "Convert Array" in {
      CirceJsonUtils.jsonToAny(List(1234, 1234).asJson) must_=== List(1234, 1234)
    }

    "Convert object" in {
      CirceJsonUtils.jsonToAny(person.asJson) must_=== personMap
    }

    "Convert Boolean" in {
      CirceJsonUtils.jsonToAny(true.asJson) must_=== true
    }

    "Convert null" in {
      CirceJsonUtils.jsonToAny((null: String).asJson) must_=== null
    }
  }

  "CirceJsonUtils.anyToJson" should {
    "Convert Int to Json" in {
      CirceJsonUtils.anyToJson(1234) must_=== 1234.asJson
    }

    "Convert Double to Json" in {
      CirceJsonUtils.anyToJson(1234.1234) must_=== 1234.1234.asJson
    }

    "Convert Double to Json" in {
      CirceJsonUtils.anyToJson(2147483647000L) must_=== 2147483647000L.asJson
    }

    "Convert String to Json" in {
      CirceJsonUtils.anyToJson("hello") must_=== "hello".asJson
    }

    "Convert Some(String) to Json" in {
      CirceJsonUtils.anyToJson(Some("hello")) must_=== "hello".asJson
    }

    "Convert true to Json" in {
      CirceJsonUtils.anyToJson(true) must_=== true.asJson
    }

    "Convert false to Json" in {
      CirceJsonUtils.anyToJson(false) must_=== false.asJson
    }

    "Convert null to Json" in {
      CirceJsonUtils.anyToJson(null) must_=== None.asJson
    }

    "Convert None to Json" in {
      CirceJsonUtils.anyToJson(None) must_=== None.asJson
    }

    "Convert List to Json" in {
      CirceJsonUtils.anyToJson(List(1, 2, 3)) must_=== List(1, 2, 3).asJson
    }

    "Convert Vector to Json" in {
      CirceJsonUtils.anyToJson(Vector(1, 2, 3)) must_=== Vector(1, 2, 3).asJson
    }
  }


  "CirceJsonUtils.mapToCaseClass" should {
    "Convert Person example map to Person case class" in {
      CirceJsonUtils.mapToCaseClass[Person](personMap) must_=== person
    }

    "Convert PersonOptionalAddress example map to Person case class" in {
      CirceJsonUtils.mapToCaseClass[PersonOptionalAddress](personOptionalAddressMap) must_=== personOptionalAddress
    }

    "Convert PersonOptionalAddress with None example map to Person case class" in {
      CirceJsonUtils.mapToCaseClass[PersonOptionalAddress](personOptionalAddressMapNone) must_=== personOptionalAddressNone
    }

    "Convert enrichedProduct" in {
      CirceJsonUtils.mapToCaseClass[EnrichedProductStringTimes](
        CirceJsonUtils.jsonToMap(defaultEnrichedProductStringTimes.asJson)) must_=== defaultEnrichedProductStringTimes
    }

    "Convert withTimestamp" in {
      CirceJsonUtils.mapToCaseClass[WithTimestamp](withTimestampMap) must_=== withTimestamp
    }

    "Convert withOptionalTimestamp" in {
      CirceJsonUtils.mapToCaseClass[WithTimestampOptional](withTimestampOptMap) must_=== withTimestampOpt
    }

    "Convert withOptionalTimestamp missing" in {
      CirceJsonUtils.mapToCaseClass[WithTimestampOptional](withTimestampOptMapMissing) must_=== withTimestampOptMissing
    }
  }
}
