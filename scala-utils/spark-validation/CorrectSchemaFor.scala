
import org.apache.spark.sql.catalyst.ScalaReflection._
import org.apache.spark.sql.types._
import scala.reflect.runtime.universe._

object CorrectSchemaFor {

  val bigIntDecimal = DecimalType(38, 0)


  def structFor[T: TypeTag]: StructType = schemaFor(localTypeOf[T]).dataType.asInstanceOf[StructType]

  // Copy and pasted from Spark, with all nullable = true change to false for everything except Option
  // We could write our own that doesn't depend on any Spark and stop using StructType
  def schemaFor(tpe: `Type`): Schema = cleanUpReflectionObjects {
    tpe.dealias match {
      // this must be the first case, since all objects in scala are instances of Null, therefore
      // Null type would wrongly match the first of them, which is Option as of now
      case t if t <:< definitions.NullTpe => Schema(NullType, nullable = true)
      //      case t if t.typeSymbol.annotations.exists(_.tree.tpe =:= typeOf[SQLUserDefinedType]) =>
      //        val udt = getClassFromType(t).getAnnotation(classOf[SQLUserDefinedType]).udt().newInstance()
      //        Schema(udt, nullable = true)
      //      case t if UDTRegistration.exists(getClassNameFromType(t)) =>
      //        val udt = UDTRegistration.getUDTFor(getClassNameFromType(t)).get.newInstance()
      //                  .asInstanceOf[UserDefinedType[_]]
      //        Schema(udt, nullable = true)
      case t if t <:< localTypeOf[Option[_]] =>
        val TypeRef(_, _, Seq(optType)) = t
        Schema(schemaFor(optType).dataType, nullable = true)
      case t if t <:< localTypeOf[Array[Byte]] => Schema(BinaryType, nullable = false)
      case t if t <:< localTypeOf[Array[_]] =>
        val TypeRef(_, _, Seq(elementType)) = t
        val Schema(dataType, nullable) = schemaFor(elementType)
        Schema(ArrayType(dataType, containsNull = nullable), nullable = false)
      case t if t <:< localTypeOf[Seq[_]] =>
        val TypeRef(_, _, Seq(elementType)) = t
        val Schema(dataType, nullable) = schemaFor(elementType)
        Schema(ArrayType(dataType, containsNull = nullable), nullable = false)
      case t if t <:< localTypeOf[Map[_, _]] =>
        val TypeRef(_, _, Seq(keyType, valueType)) = t
        val Schema(valueDataType, valueNullable) = schemaFor(valueType)
        Schema(MapType(schemaFor(keyType).dataType,
          valueDataType, valueContainsNull = valueNullable), nullable = false)
      case t if t <:< localTypeOf[Set[_]] =>
        val TypeRef(_, _, Seq(elementType)) = t
        val Schema(dataType, nullable) = schemaFor(elementType)
        Schema(ArrayType(dataType, containsNull = nullable), nullable = false)
      case t if t <:< localTypeOf[String] => Schema(StringType, nullable = false)
      case t if t <:< localTypeOf[java.sql.Timestamp] => Schema(TimestampType, nullable = false)
      case t if t <:< localTypeOf[java.sql.Date] => Schema(DateType, nullable = false)
      case t if t <:< localTypeOf[BigDecimal] => Schema(DecimalType.SYSTEM_DEFAULT, nullable = false)
      case t if t <:< localTypeOf[java.math.BigDecimal] =>
        Schema(DecimalType.SYSTEM_DEFAULT, nullable = false)
      case t if t <:< localTypeOf[java.math.BigInteger] =>
        Schema(bigIntDecimal, nullable = false)
      case t if t <:< localTypeOf[scala.math.BigInt] =>
        Schema(bigIntDecimal, nullable = false)
      case t if t <:< localTypeOf[Decimal] => Schema(DecimalType.SYSTEM_DEFAULT, nullable = false)
      case t if t <:< localTypeOf[java.lang.Integer] => Schema(IntegerType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Long] => Schema(LongType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Double] => Schema(DoubleType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Float] => Schema(FloatType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Short] => Schema(ShortType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Byte] => Schema(ByteType, nullable = false)
      case t if t <:< localTypeOf[java.lang.Boolean] => Schema(BooleanType, nullable = false)
      case t if t <:< definitions.IntTpe => Schema(IntegerType, nullable = false)
      case t if t <:< definitions.LongTpe => Schema(LongType, nullable = false)
      case t if t <:< definitions.DoubleTpe => Schema(DoubleType, nullable = false)
      case t if t <:< definitions.FloatTpe => Schema(FloatType, nullable = false)
      case t if t <:< definitions.ShortTpe => Schema(ShortType, nullable = false)
      case t if t <:< definitions.ByteTpe => Schema(ByteType, nullable = false)
      case t if t <:< definitions.BooleanTpe => Schema(BooleanType, nullable = false)
      case t if definedByConstructorParams(t) =>
        val params = getConstructorParameters(t)
        Schema(StructType(
          params.map {
            case (fieldName, fieldType) =>
              val Schema(dataType, nullable) = schemaFor(fieldType)
              StructField(fieldName, dataType, nullable)
          }), nullable = false)
      case other =>
        throw new UnsupportedOperationException(s"Schema for type $other is not supported")
    }
  }
}
