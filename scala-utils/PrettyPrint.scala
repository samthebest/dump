import cats.data.Chain
import hypervolt.clock.InstantUtils.InstantWithSomeActuallyEasySyntax
import hypervolt.lang.Syntax.DoubleSyntax

import java.lang.reflect.{Array => _, _}
import java.time.{DateTimeException, Instant}

object PrettyPrint {

  def apply(a: Any): String = prettyfy(a)

  def withPrefix(s: String, a: Any): String = prettyfy(a).prefixLines(s)

  implicit class PrettyStringSyntax(s: String) {
    def prefixLines(prefix: String): String = s.split("\n").map(prefix + _).mkString("\n")
  }

  def isBasicType(x: Any): Boolean = x match {
    case _: Double | _: Float | _: Long | _: Int | _: Short | _: Byte | _: Unit | _: Boolean |
        _: Char | _: String | _: None.type =>
      true
    case _ => false
  }

  def prettyfy(a: Any, indentSize: Int = 2, depth: Int = 0): String = {
    val indent: String = " " * depth * indentSize
    val fieldIndent: String = indent + (" " * indentSize)
    val nextDepth: Any => String = prettyfy(_: Any, indentSize, depth + 1)

    def nest(x: Any): String = s"\n$fieldIndent${nextDepth(x)}"

    def prettyfySeq(prefix: String, seq: Seq[Any]): String =
      if (seq.forall(isBasicType))
        prefix + s"(${seq.map(prettyfy(_)).mkString(", ")})"
      else
        prefix + s"(${seq.map(nest).mkString(",")}\n$indent)"

    a match {
      case null => "null"

      case None => "None"

      case i: Instant =>
        try {
//          Instant.ofEpochMilli(i.forceEpochMillis).pretty
          i.pretty
        } catch {
          case e: DateTimeException =>
            throw new IllegalArgumentException(s"Dodgy instant: ${i.forceEpochMillis}", e)
        }

      case s: String if s.contains("\n") =>
        s"\n${fieldIndent}" + "\"\"\"" + s.replace(
          "\n",
          s"\n${fieldIndent}  |",
        ) + "\"\"\".stripMargin"

      case s: String =>
        val replaceMap = Seq(
          "\n" -> "\\n",
          "\r" -> "\\r",
          "\t" -> "\\t",
          "\"" -> "\\\"",
        )

        val replaced: String =
          replaceMap.foldLeft(s) { case (cumulative, (target, replacement)) =>
            cumulative.replace(target, replacement)
          }

        s""""$replaced""""

      case Some(x) =>
        if (isBasicType(x))
          s"Some(${prettyfy(x)})"
        else
          s"Some(${nest(x)})"

      case seq: Seq[_] if seq.isEmpty =>
        seq.toString()

      case map: Map[_, _] if map.isEmpty =>
        map.toString()

      case map: Map[_, _] =>
        s"Map(${map.toList
            .sortBy(_._1.toString)
            .map { case (key, value) => nest(key) + s" -> ${nextDepth(value)}" }
            .mkString(",")}\n$indent)"

      case list: List[_] =>
        prettyfySeq("List", list)

      case seq: Seq[_] =>
        prettyfySeq("Seq", seq)

      case array: Array[_] =>
        prettyfySeq("Array", array.toSeq)

      case chain: Chain[_] =>
        throw new IllegalArgumentException(
          "Cannot pretty print Chain or NonEmptyChain due to weird bug in cats",
        )

        prettyfySeq("Chain", chain.toList)
      // Doesn't work bug in https://github.com/alexknvl/newtypes
      //      case nonEmptyChain: NonEmptyChain[_] @unchecked => prettyfySeq("NonEmptyChain", nonEmptyChain.toChain.toList)

      case tuple: Product
          if tuple.productPrefix
            .startsWith("Tuple") && tuple.productPrefix.stripPrefix("Tuple").forall(_.isDigit) =>
        prettyfySeq("", tuple.productIterator.toSeq)

      case product: Product =>
        val fields = product.getClass.fields
        if (fields.isEmpty) product.productPrefix
        else
          s"${product.productPrefix}(\n" + fields
            .map { f =>
              f.setAccessible(true)
              s"$fieldIndent${f.getName} = ${nextDepth(f.get(product))}"
            }
            .mkString(",\n") + s"\n$indent)"
      case long: Long => long.toString + "L"
      case d: Double => d.unscientific
      case _          => a.toString
    }
  }

  implicit class ClassSyntax[A](private val self: Class[A]) extends AnyVal {
    def fields: List[Field] =
      Option(self.getSuperclass).map(_.fields).getOrElse(Nil) ++
        self.getDeclaredFields.toList.filterNot(f =>
          f.isSynthetic || java.lang.reflect.Modifier
            .isStatic(f.getModifiers) || f.getName == "bitmap$init$0",
        )

  }

 implicit class DoubleSyntax(d: Double) {
    def pretty: String = f"$d%.2f"
    def pretty4: String = f"$d%.4f"
    // Can't believe java has no easy way to do this
    // TODO Add a flag for whether to include commas or not, or to truncate small digits
    // strictly speaking the above shouldn't have commas or we won't be able to copy and paste the output back into Scala
    def unscientific: String = {
      val ugly = d.toString
      if (!ugly.contains("E")) ugly
      else {
        val numberStr :: magStr :: Nil = ugly.filterNot(_ == '.').split("E").toList
        val mag = magStr.toInt
        if (mag > 0) numberStr.take(mag + 1).reverse.grouped(3).mkString(",").reverse + "." + numberStr.drop(mag + 1)
        else "0." + List.fill(-mag - 1)("0").mkString + numberStr
      }
    }
  }
}
