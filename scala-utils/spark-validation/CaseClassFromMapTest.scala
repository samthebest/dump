import java.sql.Timestamp

import org.specs2.mutable.Specification
import CaseClassFromMap._
import shapeless._
import labelled.{FieldType, field}
import FromMap._

case class WithTimestamp(name: String, timestamp: Timestamp)
case class AddressDouble(street: String, zip: Double)
case class Address(street: String, zip: Int)
case class Person(name: String, address: Address)
case class WithList(names: List[String])
case class PersonOptionalAddress(name: String, address: Option[Address])

case class NestedFoo(foos: Option[List[Foo]])
case class Foo(foo: String)


object CaseClassFromMapTest extends Specification {
  "CaseClassFromMap.apply" should {
    "Convert Map to Person" in {
      CaseClassFromMap[Person](Map(
        "name" -> "Tom",
        "address" -> Map("street" -> "Jefferson st", "zip" -> 10000)
      )) must_=== Person("Tom", Address("Jefferson st", 10000))
    }

    "Convert Map to WithTimestamp" in {
      CaseClassFromMap[WithTimestamp](Map(
        "name" -> "Tom",
        "timestamp" -> new Timestamp(1234)
      )) must_=== WithTimestamp("Tom", new Timestamp(1234))
    }

    "Convert Map to WithList" in {
      CaseClassFromMap[WithList](Map(
        "names" -> List("Tom", "Dick", "Harry")
      )) must_=== WithList(List("Tom", "Dick", "Harry"))
    }

    "Convert Map to PersonOptionalAddress Some" in {
      CaseClassFromMap[PersonOptionalAddress](Map(
        "name" -> "Tom",
        "address" -> Some(Map("street" -> "Jefferson st", "zip" -> 10000))
      )) must_=== PersonOptionalAddress("Tom", Some(Address("Jefferson st", 10000)))
    }

    "Convert Map to PersonOptionalAddress None" in {
      CaseClassFromMap[PersonOptionalAddress](Map(
        "name" -> "Tom",
        "address" -> None
      )) must_=== PersonOptionalAddress("Tom", None)
    }

    "Convert Map to Person inside spark closure" in {
      TestCommon.spark.sparkContext.makeRDD(Seq(Map(
        "name" -> "Tom",
        "address" -> Map("street" -> "Jefferson st", "zip" -> 10000)
      ))).map(CaseClassFromMap[Person](_)).collect().toList must_=== List(
        Person("Tom", Address("Jefferson st", 10000))
      )
    }

    // FAILS!
    "NestedPA" in {
      val map = Map[String, Any]("foos" -> Some(List(Map("foo" -> "value"))))

      CaseClassFromMap[NestedFoo](map) must_=== NestedFoo(foos = Some(List(Foo(
        foo = "value"
      ))))
    }
  }
}

