// TODO Use logback here
object BasicLogger {
  val project: String = "XXX"
  val degugEnvVarName: String = project + "_DEBUG"
  val debugMode = scala.util.Properties.envOrNone(degugEnvVarName).isDefined

  final val Info: Int = 0
  final val Warn: Int = 1
  final val Error: Int = 2
  var level: Int = Info
  val format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

  def time: String = format.format(new Date())

  def infoPrefix: String = time + " " + project + " INFO: "
  def warnPrefix: String = time + " " + project + " WARNING: "
  def debugPrefix: String = time + " " + project + " DEBUG: "
  def errorPrefix: String = time + " " + project + " ERROR: "

  def logDebug(s: => String): Unit = if (debugMode) System.err.println(debugPrefix + s)
  def logInfo(s: String): Unit = if (level <= Info) System.err.println(infoPrefix + s)
  def logWarning(s: String): Unit = if (level <= Warn) System.err.println(warnPrefix + s)
  def logError(s: String): Unit = if (level <= Error) System.err.println(errorPrefix + s)

  if (debugMode) logDebug("DEBUG MODE ON")
}
