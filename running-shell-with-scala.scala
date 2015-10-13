
// Doesn't work in scala repl for some reason cos of weird implicit not working

def executeAndDiscardStdErr(s: String): String = s !! ProcessLogger(_ => ())

def executeString(s: String): (String, String, Int) = {
    var stderr = ""
    var stdout = ""
    val exitCode = s ! ProcessLogger(stdout += _, stderr += _)
    (stdout, stderr, exitCode)
  }

case class BashArgsParser(input: ParserInput) extends Parser {
    def commandLine: Rule1[Seq[String]] = rule((word ~ zeroOrMore(" " ~ arg)) ~> ((_: String) +: (_: Seq[String])))
    def arg: Rule1[String] = rule(quotedWords | word)
    def word: Rule1[String] = rule(capture(oneOrMore(notSpace)))
    def quotedWords: Rule1[String] = rule("'" ~ capture(oneOrMore(noneOf("'"))) ~ "'")
    def notSpace: Rule0 = rule(noneOf(" "))
  }

  def bashStringToSeq(s: String): Seq[String] = BashArgsParser(s).commandLine.run().get

  def executeString(s: String): (String, String, Int) = executeSeq(bashStringToSeq(s))

  def executeSeq(s: Seq[String]): (String, String, Int) = {
    var stderr = ""
    var stdout = ""
    val exitCode = s ! ProcessLogger(stdout += "\n" + _, stderr += "\n" + _)
    (stdout, stderr, exitCode)
  }
