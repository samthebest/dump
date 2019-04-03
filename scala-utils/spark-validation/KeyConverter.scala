
import org.apache.spark.sql.types.{ArrayType, DataType, StructField, StructType}

case class KeyConverter(inToOut: String => String,
                        outToIn: String => String)

object KeyConverter {
  val identity = KeyConverter(Predef.identity, Predef.identity)

  def convertKeys(schema: StructType, convert: String => String): StructType =
    new StructType(schema.fields.map(convertKeys(_, convert)))

  def convertKeys(arrayType: ArrayType, convert: String => String): ArrayType =
    arrayType.copy(
      elementType = convertKeys(arrayType.elementType, convert)
    )

  def convertKeys(dataType: DataType, convert: String => String): DataType =
    dataType match {
      case f: StructType => convertKeys(f, convert)
      case f: ArrayType => convertKeys(f, convert)
      case f => f
    }

  def convertKeys(field: StructField, convert: String => String): StructField =
    field.copy(
      name = convert(field.name),
      dataType = convertKeys(field.dataType, convert)
    )
}
