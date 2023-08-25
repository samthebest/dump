package samthebest.logging

import java.io.{PrintWriter, StringWriter}
import java.net.InetAddress

import java.time.{Instant, ZoneOffset}
import java.time.format.DateTimeFormatter
import LogLevel._


object LogLevel {
  case object Off extends LogLevel(0, "off")
  case object Error extends LogLevel(1, "error")
  case object Warn extends LogLevel(2, "warn")
  case object Info extends LogLevel(3, "info")
  case object Debug extends LogLevel(4, "debug")
  // Use find usages on this, because it can alter behaviour of some stuff
  case object Trace extends LogLevel(5, "trace")

  val all: List[LogLevel] = List(Off, Error, Warn, Info, Debug, Trace)
}

sealed abstract class LogLevel(val rank: Int, val name: String) extends Product with Serializable {
  val msgType: String = name.toUpperCase
}



// TODO
// This doesn't perform quite as well as logback (logback probably has some trick to memoize stack trace generation)
// This performs about 5 - 10 times slower than logback with a naive test in LoggingPerformanceTest
// If performance is a concern, we could modify this to wrap logback with some boilerplate
//
// TODO Need to check exactly what happens to logs.
//  I see <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender"> in the logback.xml, so presumably
//  something is piping the logs to a file (which is ideal, rather than using logback.xml rotation stuff)
//  but it's possible that something weird happens at deployment and the logback.xml in the codebase is overridden
object Logger {
  def trace(msg: String)(implicit logLevel: LogLevel): Unit = printMsg(Trace, msg)
  def debug(msg: String)(implicit logLevel: LogLevel): Unit = printMsg(Debug, msg)
  def info(msg: String)(implicit logLevel: LogLevel): Unit = printMsg(Info, msg)
  def warn(msg: String)(implicit logLevel: LogLevel): Unit = printMsg(Warn, msg)
  def error(msg: String)(implicit logLevel: LogLevel): Unit = printMsg(Error, msg)

  def trace(msg: String, t: Throwable)(implicit logLevel: LogLevel): Unit = printMsg(Trace, msg, t)
  def debug(msg: String, t: Throwable)(implicit logLevel: LogLevel): Unit = printMsg(Debug, msg, t)
  def info(msg: String, t: Throwable)(implicit logLevel: LogLevel): Unit = printMsg(Info, msg, t)
  def warn(msg: String, t: Throwable)(implicit logLevel: LogLevel): Unit = printMsg(Warn, msg, t)
  def error(msg: String, t: Throwable)(implicit logLevel: LogLevel): Unit = printMsg(Error, msg, t)

  def noPrefix(msg: String, threshold: LogLevel = Info)(implicit logLevel: LogLevel): Unit =
    if (logLevel.rank >= threshold.rank) System.err.println(msg)

  def printMsg(threshold: LogLevel, msg: String)(implicit logLevel: LogLevel): Unit =
    if (logLevel.rank >= threshold.rank) printMsg(msg, threshold.msgType)

  def printMsg(threshold: LogLevel, msg: String, t: Throwable)(implicit logLevel: LogLevel): Unit = {
    if (logLevel.rank >= threshold.rank) {
      printMsg(threshold, msg)
      t.printStackTrace(System.err)
    }
  }

  def printMsg(msg: String, msgType: String): Unit = {
    val st = new Exception().getStackTrace
    val ste = st(2)
    val ste2 = if (st.isDefinedAt(3)) Some(st(3)) else None

    val thread = java.lang.Thread.currentThread()
    val prefixedMessage = constructPrefixedMessage(
      now = nowString(),
      msgType = msgType,
      host = InetAddress.getLocalHost.getHostName,
      thread = thread.getName,
      threadId = thread.getId,
      fileName = ste.getFileName,
      lineNumber = ste.getLineNumber,
      ste2FileName = ste2.map(_.getFileName),
      ste2LineNumber = ste2.map(_.getLineNumber),
      msg = msg
    )

    System.err.println(prefixedMessage)
//    System.err.println(prefixedMessage.takeLines(256, includeTrucationWarning = true))
  }

  def constructPrefixedMessage(
    now: String,
    msgType: String,
    host: String,
    thread: String,
    threadId: Long,
    fileName: String,
    lineNumber: Int,
    ste2FileName: Option[String],
    ste2LineNumber: Option[Int],
    msg: String
  ): String =
    now + " " + msgType + ": host: " + host + ": thread: " + thread + ":" + threadId + ": " + fileName + ":" +
      lineNumber + ": " + ste2FileName.map(_ + ":").getOrElse("") +
      ste2LineNumber.map(_.toString + ": ").getOrElse("") + msg

  val defaultZoneOffset: ZoneOffset = ZoneOffset.UTC

  val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS")

  def millisToDateTimeString(millis: Long): String =
    formatter.format(Instant.ofEpochMilli(millis).atZone(defaultZoneOffset))

  def nowString(): String =
    millisToDateTimeString(System.currentTimeMillis())

  def timeMillis[V](f: () => V): (V, Long) = {
    val start = System.currentTimeMillis()
    val r = f()
    (r, System.currentTimeMillis() - start)
  }

  def time[A](message: String, threshold: LogLevel = Info)(f: => A)(implicit logLevel: LogLevel): A = {
    printMsg(threshold, s"$message ...")

    val (result, durationMs) = timeMillis(() => f)

    printMsg(threshold, s"DONE $message: ${durationMs}ms")

    result
  }

  def printStackTrace(e: Throwable, threshold: LogLevel = Error)(implicit logLevel: LogLevel): Unit =
    printMsg(threshold, stackTraceToString(e))

  def stackTraceToString(e: Throwable): String = {
    val sw = new StringWriter
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}
