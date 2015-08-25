
import org.apache.hadoop.fs._
import org.apache.hadoop.conf.Configuration
import scala.util.Try

import ControlUtils.using
import scalaz.syntax.id._

// Remember you need to use hadoop jar to run jars to use this
// TODO I'm convinced we don't need to create a new file system for every path, just a new filesystem for each URI
object HdfsUtils {

  lazy val configuration: Configuration = new Configuration

  def getSize(files: Iterator[Path]): Long =
    files.map(file => file.getFileSystem(configuration).getFileStatus(file).getLen).sum

  def bigDataOutputSize(dir: String): Long = getSize(bigDataFilesIterator(dir))

  def nonEmptyBigDataOutput(dir: String): Boolean = bigDataOutputSize(dir) > 0

  def readBigDataOutput(dir: String): Iterator[String] = linesIterator(bigDataFilesIterator(dir))

  def bigDataFilesIterator(dir: String): Iterator[Path] = filesIterator(dir).filter(!_.getName.startsWith("_"))

  def linesIterator(files: Iterator[Path]): Iterator[String] = new Iterator[String] {
    val fileLines: Iterator[Iterator[String]] = files.map(file => linesIterator(file))
    def firstNextFile: Iterator[String] = fileLines.find(_.hasNext).getOrElse(Iterator[String]())
    var nextFile: Iterator[String] = firstNextFile

    def hasNext = nextFile.hasNext

    def next() = {
      val elem: String = nextFile.next()
      if (!nextFile.hasNext) nextFile = firstNextFile
      elem
    }
  }

  def linesIterator(p: String): Iterator[String] = linesIterator(new Path(p))

  def linesIterator(file: Path): Iterator[String] = new Iterator[String] {
    //According to the net, we can't just return the basicIterator or we will cause a handle leak, but in future scala this may change
    val stream: FSDataInputStream = file.getFileSystem(configuration).open(file)
    val basicIterator = scala.io.Source.fromInputStream(stream).getLines()
    if (!basicIterator.hasNext) Try(stream.close()).recover {
      case e: java.io.IOException => ()
    }

    def hasNext = Try(basicIterator.hasNext).recover {
      case e: java.io.IOException => false
    }.get

    def next() = {
      val elem = basicIterator.next()
      Try(if (!basicIterator.hasNext) stream.close()).recover {
        case e: java.io.IOException => ()
      }
      elem
    }
  }

  def filesIterator(dirPath: String, recursive: Boolean = false): Iterator[Path] = new Iterator[Path] {
    val dir = new Path(dirPath)
    val fileListIterator = dir.getFileSystem(configuration).listFiles(dir, recursive)
    def hasNext = fileListIterator.hasNext
    def next() = fileListIterator.next.getPath
  }

  def statsIterator(dirPath: String): Iterator[LocatedFileStatus] = new Iterator[LocatedFileStatus] {
    val dir = new Path(dirPath)
    val statsListIterator: RemoteIterator[LocatedFileStatus] = dir.getFileSystem(configuration).listLocatedStatus(dir)
    def hasNext = statsListIterator.hasNext
    def next() = statsListIterator.next
  }

  def dirsIterator(dirPath: String): Iterator[Path] = statsIterator(dirPath).filter(!_.isFile).map(_.getPath)

  def write(p: String, s: String, overwrite: Boolean = true) = writeLines(p, List[String](s), overwrite)

  def writeLines(p: String, contents: List[String], overwrite: Boolean = true): Unit = {
    val file = new Path(p)
    using(file.getFileSystem(configuration).create(file, overwrite))(file => contents.map(_ + "\n").foreach(file.writeBytes))
  }

  def writeUTF(p: String, s: String, overwrite: Boolean = true): Unit = {
    val file = new Path(p)
    using(file.getFileSystem(configuration).create(file, overwrite))(_.writeUTF(s))
  }
}
