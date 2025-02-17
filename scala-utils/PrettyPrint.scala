package hypervolt.lang

import cats.data.Chain
import foo.clock.InstantUtils.{DurationSyntax, InstantWithSomeActuallyEasySyntax}
import foo.lang.Syntax.DoubleSyntax
import squants.energy.Energy
import squants.market.Money

import java.lang.reflect.{Array as _, *}
import java.time.{DateTimeException, Instant}
import scala.concurrent.duration.Duration

case class PrettyMapper(isType: Any => Boolean, prettify: Any => String)

object PrettyMapper {
  def apply[T](f: T => String): PrettyMapper =
    PrettyMapper(_.isInstanceOf[T], x => f(x.asInstanceOf[T]))
}

@annotation.nowarn("cat=w-flag-dead-code")
object PrettyPrint {

  def apply(
      a: Any,
      shortenNumbers: Boolean = false,
      domainMappers: List[PrettyMapper] = Nil,
  ): String =
    prettyfy(a, shortenNumbers = shortenNumbers, domainMappers = domainMappers)

  def withPrefix(s: String, a: Any): String = prettyfy(a).prefixLines(s)

  implicit class PrettyStringSyntax(s: String) {
    def prefixLines(prefix: String): String = s.split("\n").map(prefix + _).mkString("\n")
  }

  def isBasicType(x: Any): Boolean = x match {
    case _: Double | _: Float | _: Long | _: Int | _: Short | _: Byte | _: Unit | _: Boolean |
        _: Char | _: String | _: None.type | _: Energy | _: Money =>
      true
    case Nil => true
    case _   => false
  }

  def prettyfy(
      a: Any,
      indentSize: Int = 2,
      depth: Int = 0,
      shortenNumbers: Boolean = false,
      // Helpful to inject custom serialisations for a specific domain
      domainMappers: List[PrettyMapper] = Nil,
  ): String = {
    val indent: String = " " * depth * indentSize
    val fieldIndent: String = indent + (" " * indentSize)
    val nextDepth: Any => String =
      prettyfy(
        _: Any,
        indentSize = indentSize,
        depth = depth + 1,
        shortenNumbers = shortenNumbers,
        domainMappers = domainMappers,
      )

    def nest(x: Any): String = s"\n$fieldIndent${nextDepth(x)}"

    def prettyfySeq(prefix: String, seq: Seq[Any]): String =
      if (seq.forall(isBasicType))
        prefix + s"(${seq.map(prettyfy(_, shortenNumbers = shortenNumbers, domainMappers = domainMappers)).mkString(", ")})"
      else
        prefix + s"(${seq.map(nest).mkString(",")}\n$indent)"

    a match {
      case x if domainMappers.exists(_.isType(x)) => domainMappers.find(_.isType(x)).get.prettify(x)

      case null => "null"

      case None => "None"

      case i: Instant =>
        try {
          "\"" + (if (shortenNumbers) i.pretty else i.prettyMillis) + "\""
        } catch {
          case e: DateTimeException =>
            throw new IllegalArgumentException(s"Dodgy instant: ${i.forceEpochMillis}", e)
        }

      case s: String if s.contains("\n") =>
        s"\n$fieldIndent" + "\"\"\"" + s.replace(
          "\n",
          s"\n$fieldIndent  |",
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
          s"Some(${prettyfy(x, shortenNumbers = shortenNumbers, domainMappers = domainMappers)})"
        else
          s"Some(${nest(x)}\n$indent)"

      case Nil => "Nil"

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
      case long: Long         => long.toString + "L"
      case d: Double          => d.unscientific
      case duration: Duration => if (shortenNumbers) duration.pretty else duration.toString
      case energy: Energy     => s"KilowattHours(${energy.toKilowattHours.unscientific})"
      case money: Money       => s"Money(${money.value.unscientific})"
      case _                  => a.toString
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
}
