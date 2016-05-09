
import java.io.File

import BasicLogger._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext.{logInfo => _, _}
import org.apache.spark.rdd.RDD
import org.joda.time.{DateTime, Interval}

import scala.reflect.ClassTag
import scalaz.Scalaz._

object Pimps {
  implicit class PimpedRDD[T: ClassTag](rdd: RDD[T]) {
    // TODO Slightly misleading name
    def distinctBy[K: ClassTag](f: T => K): RDD[(K, T)] = rdd.map(t => (f(t), t)).reduceByKey((l, _) => l)
  }

  implicit class PimpedSparkContext(sc: SparkContext) {
    def printCacheMemoryStatus(): Unit = {
      logInfo("Cache memory usage:")
      if (BasicLogger.level <= BasicLogger.Info)
        sc.getExecutorMemoryStatus.foreach {
          case (executor, (max, remaining)) => System.err.println("SBP: " + executor + ", remaining (MB): " + (remaining / (1000 * 1000))
            + ", max (MB): " + (max / (1000 * 1000)) + ", ratio usage: " + ((max - remaining).toDouble / max))
        }
    }
  }

  implicit class PimpedIterable[T](iterable: Iterable[T]) {
    def writeLines(p: String): Iterable[T] = {
      val pw = new java.io.PrintWriter(new File(p))
      try iterable.map(_.toString + "\n").foreach(pw.write) finally pw.close()
      iterable
    }

    def keyBy[K](f: T => K): Map[K, T] = iterable.map(el => f(el) -> el).toMap
  }

  import scala.collection.IterableLike
  import scala.collection.generic.CanBuildFrom

  // TODO Should unit test since this is copy pasted from SO
  implicit class DistinctableIterableLike[A, Repr](xs: IterableLike[A, Repr]) {
    def distinctBy[B, That](f: A => B)(implicit cbf: CanBuildFrom[Repr, A, That]) = {
      val builder = cbf(xs.repr)
      val i = xs.iterator
      var set = Set[B]()
      while (i.hasNext) {
        val o = i.next()
        val b = f(o)
        if (!set(b)) {
          set += b
          builder += o
        }
      }
      builder.result()
    }
  }

  implicit class PimpedSeq[T](l: Seq[T]) {
    def indexOption(e: T): Option[Int] = {
      val i = l.indexOf(e)
      (i != -1).option(i)
    }

    def indexOrElse(e: T, f: => Int): Int = {
      val i = l.indexOf(e)
      if (i == -1) f else i
    }

    def existsNot(f: T => Boolean): Boolean = l.exists(!f(_))
    def findNot(f: T => Boolean): Option[T] = l.find(!f(_))
    def mapAdd[T2](f: T => T2): Seq[(T, T2)] = l.map(x => (x, f(x)))
    def add[T2](f: => T2): Seq[(T, T2)] = l.map(x => (x, f))

    def hasDuplicated[T2](f: T => T2): Boolean = l.distinctBy(f).size != l.size
    def hasNoDuplicated[T2](f: T => T2): Boolean = l.distinctBy(f).size == l.size
  }

  implicit class PimpedIterator[T](l: Iterator[T]) {
    def indexOption(e: T): Option[Int] = {
      val i = l.indexOf(e)
      (i != -1).option(i)
    }

    def indexOrElse(e: T, f: => Int): Int = {
      val i = l.indexOf(e)
      if (i == -1) f else i
    }

    def existsNot(f: T => Boolean): Boolean = l.exists(!f(_))
    def findNot(f: T => Boolean): Option[T] = l.find(!f(_))
    def mapAdd[T2](f: T => T2): Iterator[(T, T2)] = l.map(x => (x, f(x)))
  }

  implicit class PimpedTupleSeq[T1, T2](l: Seq[(T1, T2)]) {
    def map1[U](f: T1 => U): Seq[(U, T2)] = l.map(t => (f(t._1), t._2))
    def map2[U](f: T2 => U): Seq[(T1, U)] = l.map(t => (t._1, f(t._2)))
    def mapTupled[U](f: (T1, T2) => U): Seq[U] = l.map(f.tupled)
    def flatMapTupled[U](f: (T1, T2) => TraversableOnce[U]): Seq[U] = l.flatMap(f.tupled)
  }

  implicit class PimpedTupleIterator[T1, T2](l: Iterator[(T1, T2)]) {
    def map1[U](f: T1 => U): Iterator[(U, T2)] = l.map(t => (f(t._1), t._2))
    def map2[U](f: T2 => U): Iterator[(T1, U)] = l.map(t => (t._1, f(t._2)))
    def mapTupled[U](f: (T1, T2) => U): Iterator[U] = l.map(f.tupled)
    def flatMapTupled[U](f: (T1, T2) => TraversableOnce[U]): Iterator[U] = l.flatMap(f.tupled)
  }

  implicit class PimpedString(s: String) {
    def indexOption(c: Char): Option[Int] = {
      val i = s.indexOf(c)
      (i != -1).option(i)
    }

    def indexOrElse(c: Char, f: => Int): Int = {
      val i = s.indexOf(c)
      if (i == -1) f else i
    }

    def parenthesized = "(" + s + ")"
  }

  implicit class PimpedBooleanFunction[T](f: T => Boolean) extends java.io.Serializable {
    def &&(f2: T => Boolean): T => Boolean = (x: T) => f(x) && f2(x)
    def ||(f2: T => Boolean): T => Boolean = (x: T) => f(x) || f2(x)
  }

  implicit class PimpedBooleanFunction2[T1, T2](f: (T1, T2) => Boolean) extends java.io.Serializable {
    def &&(f2: (T1, T2) => Boolean): (T1, T2) => Boolean = (x: T1, y: T2) => f(x, y) && f2(x, y)
    def ||(f2: (T1, T2) => Boolean): (T1, T2) => Boolean = (x: T1, y: T2) => f(x, y) || f2(x, y)
  }

  case class SizedSeq[T](l: Seq[T], quickSize: Int)

  object SizedSeq {
    def apply[T](l: Seq[T]): SizedSeq[T] = SizedSeq(l, l.size)
  }

  implicit class ToSized[T](l: Seq[T]) {
    def toSizedSeq: SizedSeq[T] = SizedSeq(l)
  }

  implicit class PimpedSet[T](s: Set[T]) {
    def delta(that: Set[T]): Set[T] = (s &~ that) ++ (that &~ s)
  }

  implicit class PimpedDouble(d: Double) {
    def \(numerator: Double): Double = if (d == 0.0 && numerator == 0.0) 1.0 else d / numerator
  }

  implicit class PimpedRecord[T](record: T) {
    def isNotIn(seq: Seq[T]): Boolean = !seq.contains(record)
    def isNotIn(set: Set[T]): Boolean = !set.contains(record)
  }

  implicit class PimpedLong(long: Long) {
    def dateTime: DateTime = new DateTime(long * 1000)
  }

  implicit class PimpedDateTime(timestamp: DateTime) {
    def isNotIn(interval: Interval): Boolean = !interval.contains(timestamp)
  }

  implicit class PimpedTuple2[T1, T2](tuple: (T1, T2)) {
    def applyTupled[U](f: (T1, T2) => U): U = f.tupled(tuple)
  }

  implicit class PimpedMap[K, V](map: Map[K, V]) {
    def join[V2](map2: Map[K, V2]): Map[K, (V, V2)] =
      map.keys.filter(map2.contains).map(key => (key, (map(key), map2(key)))).toMap

    def outerJoin[V2](map2: Map[K, V2]): Map[K, (Option[V], Option[V2])] =
      (map.keys ++ map2.keys).map(key => (key, (map.get(key), map2.get(key)))).toMap
  }
}
