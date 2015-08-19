import scalaz.Scalaz._

case object CappedSet {
  // This value should be slightly less than the value in FlatHashTable.defaultLoadFactor divided by 1000
  val defaultLoadFactor = 0.45 - Double.MinPositiveValue

  // See http://bits.stephan-brumme.com/roundUpToNextPowerOfTwo.html
  def roundUpToNextPowerOfTwo(target: Int): Int = {
    var c = target - 1
    c |= c >>> 1
    c |= c >>> 2
    c |= c >>> 4
    c |= c >>> 8
    c |= c >>> 16
    c + 1
  }

  def capacity(expectedSize: Int) = expectedSize match {
    case x if x < 0 => throw new IllegalArgumentException("negative value of expected size: " + x)
    case 0 => 1
    case _ => roundUpToNextPowerOfTwo(expectedSize)
  }

  def optimalCap(cap: Int) =
    (capacity((cap.toDouble / defaultLoadFactor).ceil.toInt).toDouble * defaultLoadFactor).floor.toInt

  def isOptimal(cap: Int): Boolean = cap === optimalCap(cap)
}

// TODO Unit test the memory usage like hell (you will probably have to call `capacity`)
// or it would pretty clever if you actually serailized it and the unit tests make sure that it grows as expected
// (so within some range)
case class CappedSet(cap: Int, strictlyOptimal: Boolean = false) extends Serializable {
  if (strictlyOptimal) require(CappedSet.isOptimal(cap), "Value of cap has not been optimized: " + cap)
  private val set = new collection.mutable.HashSet[Int]()

  def +(e: Int): CappedSet =
    if (exceeded) this
    else {
      set += e
      this
    }

  def exceeded = set.size > cap
  def uniques: Option[Int] = (!exceeded).option(set.size)
  def toSet: Set[Int] = set.toSet
  def uniquesOrCap(): Int = uniques.getOrElse(cap)

  case class CapppedSetPrintable(set: Set[Int], size: Int, cap: Int, exceeded: Boolean)
  override def toString: String = CapppedSetPrintable(toSet, set.size, cap, exceeded).toString
}
