object NamingParser extends QuickParsers {
  // May behave undesirably for acronyms
  def lowerCamelToUnderscore(s: String): String = parse(
    (((lowerOrDigitsOrUnderscore | (upperChar ^^ ("_" + _.toLowerCase))).+ ^^ (_.mkString)) ~ end) ^^ (_._1), s) match {
    case Success(result, _) => result
    case Failure(msg, _) => throw new IllegalArgumentException(msg)
    case _ => ???
  }
}
