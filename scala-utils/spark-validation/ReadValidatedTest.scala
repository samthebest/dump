
import org.specs2.mutable.Specification
import java.sql.Timestamp

import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types._
import ReadValidated._

class ReadValidatedTest extends Specification {
  "ReadValidated.schemaFor" should {
    "Produce StructType with correct nullability" in {
      ReadValidated.structFor[Example] must_=== StructType(Seq(
        StructField("nullable1", StringType, nullable = true),
        StructField("notNullable1", StringType, nullable = false),
        StructField("notNullable2", IntegerType, nullable = false),
        StructField("nullable2", IntegerType, nullable = true),
        StructField("nullable3", TimestampType, nullable = true),
        StructField("notNullable3", TimestampType, nullable = false),
        StructField("notNullable4", StructType(Seq(StructField("foo", IntegerType, nullable = false))), nullable = false),
        StructField("nullable4", StructType(Seq(StructField("foo", IntegerType, nullable = false))), nullable = true)
      ))
    }

    "Spark should produce StructType with incorrect nullability" in {
      ScalaReflection.schemaFor[Example].dataType.asInstanceOf[StructType] must_=== StructType(Seq(
        StructField("nullable1", StringType, nullable = true),
        StructField("notNullable1", StringType, nullable = true),
        StructField("notNullable2", IntegerType, nullable = false),
        StructField("nullable2", IntegerType, nullable = true),
        StructField("nullable3", TimestampType, nullable = true),
        StructField("notNullable3", TimestampType, nullable = true),
        StructField("notNullable4", StructType(Seq(StructField("foo", IntegerType, nullable = false))), nullable = true),
        StructField("nullable4", StructType(Seq(StructField("foo", IntegerType, nullable = false))), nullable = true)
      ))
    }
  }

  implicit val line = ""

  // Epic copy and pasting in this file, but drying it could make the tests harder to follow

  "ReadValidated.validateAndConvertTypes" should {
    def testMissingField(dataType: DataType) = {
      validateAndConvertTypes(
        fieldsToValues = Map("fieldWrong" -> "foo"),
        expectedSchema = StructType(Seq(StructField("field", dataType, nullable = false))),
        format = JSON()
      ) must_=== Left(NotProcessableRecordTyped(
        recordLine = line,
        notProcessableReasonType = MissingField,
        notProcessableReasonMessage = "Missing non nullable field: field",
        stackTrace = None
      ))
    }

    "String conversions" should {
      "Convert a map with Strings in it" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> "foo"),
          expectedSchema = StructType(Seq(StructField("field", StringType, nullable = false))),
          format = JSON()
        ) must_=== Right(Map("field" -> "foo"))
      }

      "Return a map with Some when the field exists and is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> "foo"),
          expectedSchema = StructType(Seq(StructField("field", StringType, nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> Some("foo")))
      }

      "Return a map with None when the field does not exist but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map(),
          expectedSchema = StructType(Seq(StructField("field", StringType, nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> None))
      }

      "Return a map with None when the field is null but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> null),
          expectedSchema = StructType(Seq(StructField("field", StringType, nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> None))
      }

      "Return NotProcessableRecord when given a map with integer field instead of string" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234),
          expectedSchema = StructType(Seq(StructField("field", StringType, nullable = false))),
          format = JSON()
        ) must_=== Left(NotProcessableRecordTyped(
          recordLine = line,
          notProcessableReasonType = IncorrectType,
          notProcessableReasonMessage = "Expected String but found field: 1234",
          stackTrace = None
        ))
      }

      "Return NotProcessableRecord when given a map with missing field" in {
        testMissingField(StringType)
      }
    }

    "Boolean conversions" should {
      "Convert a map with Booleans in it" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> true),
          expectedSchema = StructType(Seq(StructField("field", BooleanType, nullable = false))),
          format = JSON()
        ) must_=== Right(Map("field" -> true))
      }

      "Return a map with Some when the field exists and is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> true),
          expectedSchema = StructType(Seq(StructField("field", BooleanType, nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> Some(true)))
      }

      "Return a map with None when the field does not exist but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map(),
          expectedSchema = StructType(Seq(StructField("field", BooleanType, nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> None))
      }

      "Return a map with None when the field is null but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> null),
          expectedSchema = StructType(Seq(StructField("field", BooleanType, nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> None))
      }

      "Return NotProcessableRecord when given a map with integer field instead of boolean" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234),
          expectedSchema = StructType(Seq(StructField("field", BooleanType, nullable = false))),
          format = JSON()
        ) must_=== Left(NotProcessableRecordTyped(
          recordLine = line,
          notProcessableReasonType = IncorrectType,
          notProcessableReasonMessage = "Expected Boolean but found field: 1234",
          stackTrace = None
        ))
      }

      "Return NotProcessableRecord when given a map with missing field" in {
        testMissingField(BooleanType)
      }
    }

    "Timestamp conversions" should {
      "Convert a map with Timestamps in it" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> "2018-09-25T00:00:00Z"),
          expectedSchema = StructType(Seq(StructField("field", TimestampType, nullable = false))),
          format = JSON(Some("yyyy-MM-dd'T'HH:mm:ssX"))
        ) match {
          case Right(map) if map == Map("field" -> new Timestamp(1537833600000L)) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with Some when the field exists and is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> "2018-09-25T00:00:00Z"),
          expectedSchema = StructType(Seq(StructField("field", TimestampType, nullable = true))),
          format = JSON(Some("yyyy-MM-dd'T'HH:mm:ssX"))
        ) match {
          case Right(map) if map == Map("field" -> Some(new Timestamp(1537833600000L))) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with None when the field does not exist but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map(),
          expectedSchema = StructType(Seq(StructField("field", TimestampType, nullable = true))),
          format = JSON(Some("yyyy-MM-dd'T'HH:mm:ssX"))
        ) match {
          case Right(map) if map == Map("field" -> None) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return NotProcessableRecord when given a map with integer field instead of timestamp string" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234),
          expectedSchema = StructType(Seq(StructField("field", TimestampType, nullable = false))),
          format = JSON()
        ) must_=== Left(NotProcessableRecordTyped(
          recordLine = line,
          notProcessableReasonType = IncorrectType,
          notProcessableReasonMessage = "Expected Timestamp String but found field: 1234",
          stackTrace = None
        ))
      }

      "Return NotProcessableRecord when given a map with missing field" in {
        testMissingField(TimestampType)
      }
    }

    "Long conversions" should {
      "Convert a map with Longs in it" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234L),
          expectedSchema = StructType(Seq(StructField("field", LongType, nullable = false))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> 1234L) && map.head._2.isInstanceOf[Long] => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with Some when the field exists and is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234L),
          expectedSchema = StructType(Seq(StructField("field", LongType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> Some(1234L)) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with None when the field does not exist but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map(),
          expectedSchema = StructType(Seq(StructField("field", LongType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> None) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with None when the field is null but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> null),
          expectedSchema = StructType(Seq(StructField("field", LongType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> None) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return NotProcessableRecord when given a map with decimal field instead of long" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234.1234),
          expectedSchema = StructType(Seq(StructField("field", LongType, nullable = false))),
          format = JSON()
        ) must_=== Left(NotProcessableRecordTyped(
          recordLine = line,
          notProcessableReasonType = IncorrectType,
          notProcessableReasonMessage = "Expected Long but found field: 1234.1234",
          stackTrace = None
        ))
      }

      "Return NotProcessableRecord when given a map with missing field" in {
        testMissingField(LongType)
      }
    }

    "Integer conversions" should {
      "Convert a map with Ints in it" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234),
          expectedSchema = StructType(Seq(StructField("field", IntegerType, nullable = false))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> 1234) && map.head._2.isInstanceOf[Int] => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with Some when the field exists and is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234),
          expectedSchema = StructType(Seq(StructField("field", IntegerType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> Some(1234)) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with None when the field does not exist but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map(),
          expectedSchema = StructType(Seq(StructField("field", IntegerType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> None) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with None when the field is null but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> null),
          expectedSchema = StructType(Seq(StructField("field", IntegerType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> None) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return NotProcessableRecord when given a map with decimal field instead of int" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234.1234),
          expectedSchema = StructType(Seq(StructField("field", IntegerType, nullable = false))),
          format = JSON()
        ) must_=== Left(NotProcessableRecordTyped(
          recordLine = line,
          notProcessableReasonType = IncorrectType,
          notProcessableReasonMessage = "Expected Int but found field: 1234.1234",
          stackTrace = None
        ))
      }

      "Return NotProcessableRecord when given a map with missing field" in {
        testMissingField(IntegerType)
      }
    }

    "Double conversions" should {
      "Convert a map with Doubles in it" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234.1234),
          expectedSchema = StructType(Seq(StructField("field", DoubleType, nullable = false))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> 1234.1234) && map.head._2.isInstanceOf[Double] => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with Some when the field exists and is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> 1234.1234),
          expectedSchema = StructType(Seq(StructField("field", DoubleType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> Some(1234.1234)) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with None when the field does not exist but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map(),
          expectedSchema = StructType(Seq(StructField("field", DoubleType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> None) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return a map with None when the field is null but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> null),
          expectedSchema = StructType(Seq(StructField("field", DoubleType, nullable = true))),
          format = JSON()
        ) match {
          case Right(map) if map == Map("field" -> None) => success
          case left@Left(_) => failure("Conversion error: " + left)
          case Right(map) => failure("Wrong result: " + map)
        }
      }

      "Return NotProcessableRecord when given a map with string field instead of double" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> "1234 1234"),
          expectedSchema = StructType(Seq(StructField("field", DoubleType, nullable = false))),
          format = JSON()
        ) must_=== Left(NotProcessableRecordTyped(
          recordLine = line,
          notProcessableReasonType = IncorrectType,
          notProcessableReasonMessage = "Expected Double but found field: 1234 1234",
          stackTrace = None
        ))
      }

      "Return NotProcessableRecord when given a map with missing field" in {
        testMissingField(DoubleType)
      }
    }

    "Array conversions" should {
      "Convert a map with array double in it" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> List(1.2, 1.3)),
          expectedSchema = StructType(Seq(StructField("field",
            ArrayType(DoubleType), nullable = false))),
          format = JSON()
        ) must_=== Right(Map("field" -> List(1.2, 1.3)))
      }

      "Return a map with Some when the field exists and is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> List(1.2, 1.3)),
          expectedSchema = StructType(Seq(StructField("field",
            ArrayType(DoubleType), nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> Some(List(1.2, 1.3))))
      }

      "Return a map with None when the field does not exist but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map(),
          expectedSchema = StructType(Seq(StructField("field",
            ArrayType(DoubleType), nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> None))
      }

      "Return a map with None when the field is null but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> null),
          expectedSchema = StructType(Seq(StructField("field",
            ArrayType(DoubleType), nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> None))
      }

      "Convert a map with arrays of structs (of strings)" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> List(Map("fieldInner" -> "foo"), Map("fieldInner" -> "bar"))),
          expectedSchema = StructType(Seq(StructField("field",
            ArrayType(StructType(Seq(StructField("fieldInner", StringType, nullable = false)))), nullable = false))),
          format = JSON()
        ) must_=== Right(Map("field" -> List(Map("fieldInner" -> "foo"), Map("fieldInner" -> "bar"))))
      }

      "Convert a map with arrays of arrays (of ints)" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> List(List(5, 3, 1, 7), List())),
          expectedSchema = StructType(Seq(StructField("field",
            ArrayType(ArrayType(IntegerType)), nullable = false))),
          format = JSON()
        ) must_=== Right(Map("field" -> List(List(5, 3, 1, 7), List())))
      }

      "Return NotProcessableRecord when given a map with string field instead of an array" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> "1234 1234"),
          expectedSchema = StructType(Seq(StructField("field", ArrayType(StringType), nullable = false))),
          format = JSON()
        ) must_=== Left(NotProcessableRecordTyped(
          recordLine = line,
          notProcessableReasonType = IncorrectType,
          notProcessableReasonMessage = "Expected Array but found field: 1234 1234",
          stackTrace = None
        ))
      }

      "Return NotProcessableRecord when given a map with missing field" in {
        testMissingField(ArrayType(StringType))
      }
    }

    "Struct conversions" should {
      "Convert a map with Structs in it" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> Map("fieldInner" -> "foo")),
          expectedSchema = StructType(Seq(StructField("field",
            StructType(Seq(StructField("fieldInner", StringType, nullable = false))), nullable = false))),
          format = JSON()
        ) must_=== Right(Map("field" -> Map("fieldInner" -> "foo")))
      }

      "Return a map with Some when the field exists and is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> Map("fieldInner" -> "foo")),
          expectedSchema = StructType(Seq(StructField("field",
            StructType(Seq(StructField("fieldInner", StringType, nullable = false))), nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> Some(Map("fieldInner" -> "foo"))))
      }

      "Return a map with None when the field does not exist but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map(),
          expectedSchema = StructType(Seq(StructField("field",
            StructType(Seq(StructField("fieldInner", StringType, nullable = false))), nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> None))
      }

      "Return a map with None when the field is null but it is nullable" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> null),
          expectedSchema = StructType(Seq(StructField("field",
            StructType(Seq(StructField("fieldInner", StringType, nullable = false))), nullable = true))),
          format = JSON()
        ) must_=== Right(Map("field" -> None))
      }

      "Return NotProcessableRecord when given a map with string field instead of structure" in {
        validateAndConvertTypes(
          fieldsToValues = Map("field" -> "foo"),
          expectedSchema = StructType(Seq(StructField("field",
            StructType(Seq(StructField("fieldInner", StringType, nullable = false))), nullable = false))),
          format = JSON()
        ) must_=== Left(NotProcessableRecordTyped(
          recordLine = line,
          notProcessableReasonType = IncorrectType,
          notProcessableReasonMessage = "Expected Struct but found field: foo",
          stackTrace = None
        ))
      }

      "Return NotProcessableRecord when given a map with missing field" in {
        testMissingField(StructType(Seq()))
      }
    }
  }

  "ReadValidated.getField" should {

    // Unit tests to handle:
    //
    // nullable and exists
    // nullable and not exist
    // nullable and exists as null
    // not nullable and exists
    // not nullable and not exist
    // not nullable and exists as null

    "Return "

    "tests" in {
      failure("write me")
    }
  }

  "ReadValidated.parsePartitionToFieldValueMaps" should {
    "Correctly parse JSON Ints, Longs and Doubles" in {
      parsePartitionToFieldValueMaps(Iterator(
        """{"field":1234}""",
        """{"field":2147483647000}""",
        """{"field":1234.1234}"""
      ), JSON()).toList must_===
        List(
          """{"field":1234}""" -> Map("field" -> 1234),
          """{"field":2147483647000}""" -> Map("field" -> 2147483647000L),
          """{"field":1234.1234}""" -> Map("field" -> 1234.1234)
        )
        .map(Right[NotProcessableRecordTyped, (String, Map[String, Any])])
    }

    "Correctly parse arrays, nested and nulls" in {
      parsePartitionToFieldValueMaps(Iterator(
        """{"structure":{"array":[1,2,3],"nullField":null}}"""
      ), JSON()).toList must_===
        List(
          """{"structure":{"array":[1,2,3],"nullField":null}}""" -> Map("structure" -> Map(
            "array" -> List(1, 2, 3),
            "nullField" -> null
          ))
        )
        .map(Right[NotProcessableRecordTyped, (String, Map[String, Any])])
    }

    "Correctly produces NotProcessableRecords" in {
      val result =
        parsePartitionToFieldValueMaps(Iterator(
          """{"field":1234}""",
          """{"field":1234.1234 BAD JSON!!!}"""
        ), JSON()).toList

      result.head must_=== Right[NotProcessableRecordTyped, (String, Map[String, Any])](
        """{"field":1234}""" -> Map("field" -> 1234))

      result.last match {
        case Left(fail) if
        fail.recordLine == """{"field":1234.1234 BAD JSON!!!}""" &&
          fail.notProcessableReasonType == InvalidJSON &&
          fail.notProcessableReasonMessage == "Could not parse json see stack trace" &&
          fail.stackTrace.nonEmpty => success
        case other => failure("Incorrect result: " + other)
      }
    }
  }
}

case class ExampleStruct(foo: Int)

case class Example(nullable1: Option[String],
                   notNullable1: String,
                   notNullable2: Int,
                   nullable2: Option[Int],
                   nullable3: Option[Timestamp],
                   notNullable3: Timestamp,
                   notNullable4: ExampleStruct,
                   nullable4: Option[ExampleStruct])
