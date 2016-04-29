def sumDoubleVectors: UserDefinedAggregateFunction = new UserDefinedAggregateFunction {
    def inputSchema: StructType = StructType(StructField("doubleArray", ArrayType(DoubleType, false)) :: Nil)
    def bufferSchema: StructType = StructType(StructField("doubleArray", ArrayType(DoubleType, false)) :: Nil)
    def dataType: DataType = ArrayType(DoubleType, false)
    def deterministic: Boolean = true
    def initialize(buffer: MutableAggregationBuffer): Unit = buffer(0) = Array.empty[Double]
    def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = update(buffer1, buffer2)
    def evaluate(buffer: Row): Any = buffer.getAs[mutable.WrappedArray[Double]](0)

    // TODO Might want to mutate rather than copy (i.e. map)
    def update(buffer: MutableAggregationBuffer, input: Row): Unit =
      buffer(0) = {
        val inputArray = input.getAs[mutable.WrappedArray[Double]](0)

        buffer.getAs[mutable.WrappedArray[Double]](0) match {
          case bufferOld if bufferOld.isEmpty => inputArray
          case bufferOld if inputArray.isEmpty => bufferOld
          case bufferOld => bufferOld.zip(inputArray).map {
            case (l, r) => l + r
          }
        }
      }
  }

  def sumNestedDoubleVectors: UserDefinedAggregateFunction = new UserDefinedAggregateFunction {
    def inputSchema: StructType = StructType(StructField("doubleArrays", ArrayType(ArrayType(DoubleType, false), false)) :: Nil)
    def bufferSchema: StructType = StructType(StructField("doubleArrays", ArrayType(ArrayType(DoubleType, false), false)) :: Nil)
    def dataType: DataType = ArrayType(ArrayType(DoubleType, false), false)
    def deterministic: Boolean = true
    def initialize(buffer: MutableAggregationBuffer): Unit = buffer(0) = Array.empty[Array[Double]]
    def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = update(buffer1, buffer2)
    def evaluate(buffer: Row): Any = buffer.getAs[mutable.WrappedArray[mutable.WrappedArray[Double]]](0)

    // TODO Might want to mutate rather than copy (i.e. map)
    def update(buffer: MutableAggregationBuffer, input: Row): Unit =
      buffer(0) = {
        val inputArray = input.getAs[mutable.WrappedArray[mutable.WrappedArray[Double]]](0)
        buffer.getAs[mutable.WrappedArray[mutable.WrappedArray[Double]]](0) match {
          case bufferOld if bufferOld.isEmpty => inputArray
          case bufferOld if inputArray.isEmpty => bufferOld
          case bufferOld => bufferOld.zip(inputArray).map {
            case (l, r) => l.zip(r).map {
              case (l, r) => l + r
            }
          }
        }
      }
  }
  
  
  
  .....
  
  import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{DoubleType, ArrayType, StructField, StructType}
import org.scalatest.FunSuite

import scala.collection.mutable

case class DoubleArray(doubleArray: Array[Double])
case class DoubleNestedArray(doubleArrays: Array[Array[Double]])

class IMAUDFsTest extends FunSuite {

  import sql.implicits._

  test("IMAUDFs.apply creates a sumDoubleVectors UDAF") {
    val data = Seq(
      DoubleArray(Array(1.0, 1.0, 1.0)),
      DoubleArray(Array(1.0, 5.0, 2.0))
    )

    sql.createDataset(data).toDF().registerTempTable("double_array")

    IMAUDFs(sql)

    assert(
      sql.sql("select sumDoubleVectors(doubleArray) from double_array")
      .collect().toList.map(_ (0).asInstanceOf[mutable.WrappedArray[Double]]) ==
        List(Seq(2.0, 6.0, 3.0))
    )
  }

  test("IMAUDFs.apply creates a sumNestedDoubleVectors UDAF") {
    val data = Seq(
      DoubleNestedArray(Array(Array(1.0, 1.0, 1.0), Array(1.0, 1.0, 1.0))),
      DoubleNestedArray(Array(Array(1.0, 5.0, 2.0), Array(3.0, 3.0, 3.0)))
    )

    sql.createDataset(data).toDF().registerTempTable("double_arrays")

    IMAUDFs(sql)

    assert(
      sql.sql("select sumNestedDoubleVectors(doubleArrays) from double_arrays")
      .collect().toList
      .map(_ (0).asInstanceOf[mutable.WrappedArray[mutable.WrappedArray[Double]]]) ==
        List(Seq(Seq(2.0, 6.0, 3.0), Seq(4.0, 4.0, 4.0)))
    )
  }
}




..........



// Uses while loop so we can early exit (so has better average time than foldLeft). We could use tailrec here
  // but the stackframe copy overhead
  // (Warning: has very little meaning, cannot be understood, invented by statisticians).
  def percentileLinInterpFirstVariant(values: Seq[Double], p: Double): Double = {
    val count = values.size
    require(count > 1, "Need at least 2 values")

    var ((value, index) :: tail, prevPercentRank, prevValue, result) =
      (values.sorted.zipWithIndex, Option.empty[Double], 0.0, Option.empty[Double])

    while (result.isEmpty) {
      (prevPercentRank, (100.0 / count) * (index + 1 - 0.5), tail) match {
        case (None, percentRank, _) if p < percentRank =>
          result = Some(value)
        case (_, percentRank, _) if p == percentRank =>
          result = Some(value)
        case (Some(prevPercentRank), percentRank, _) if prevPercentRank < p && p < percentRank =>
          result = Some(count * (p - prevPercentRank) * (value - prevValue) / 100 + prevValue)
        case (_, percentRank, Nil) =>
          result = Some(value)
        case (_, percentRank, (nextValue, nextIndex) :: nextTail) =>
          prevValue = value
          value = nextValue
          index = nextIndex
          tail = nextTail
          prevPercentRank = Some(percentRank)
      }
    }
    result.get
  }
  
  
  test("IMA.percentileLinInterpFirstVariant handles small sequences as per worked example on wikipedia: " +
    "https://en.wikipedia.org/wiki/Percentile#Worked_Example_of_the_First_Variant") {
    assert(IMA.percentileLinInterpFirstVariant(List(15, 20, 35, 40, 50), 5) == 15.0)
    assert(IMA.percentileLinInterpFirstVariant(List(15, 20, 35, 40, 50), 30) == 20.0)
    assert(IMA.percentileLinInterpFirstVariant(List(15, 20, 35, 40, 50), 40) == 27.5)
    assert(IMA.percentileLinInterpFirstVariant(List(15, 20, 35, 40, 50), 95) == 50)
  }

  test("IMA.percentileLinInterpFirstVariant handles weird cases") {
    assert(IMA.percentileLinInterpFirstVariant(List(0, 100), 50) == 50.0)
    assert(IMA.percentileLinInterpFirstVariant(List(0, 200), 50) == 100.0)
    assert(IMA.percentileLinInterpFirstVariant(List(0, 100), 50 + 12.5) == 75.0)
    assert(IMA.percentileLinInterpFirstVariant(List(0, 100, 200), 50) == 100.0)
    assert(IMA.percentileLinInterpFirstVariant(List(0, 100, 200, 300), 75) == 250.0)
  }
  
  test("IMA.percentileLinInterpFirstVariant handles ordinary cases") {
    assert(IMA.percentileLinInterpFirstVariant((1 to 99).map(_.toDouble).toList, 50) == 50.0)
    assert(IMA.percentileLinInterpFirstVariant((101 to 199).map(_.toDouble).toList, 50) == 150.0)
  }
  
  
  
