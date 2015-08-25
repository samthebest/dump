import org.scalacheck.{Arbitrary, Gen}
import org.specs2.matcher.Parameters

import scala.util.Random

class CappedSetSpecs extends ScalaCheckUtils {
  sequential

//  implicit val params = Parameters(minTestsOk = if (quickTestMode) 1000 else 1000000)
  implicit val params = Parameters(minTestsOk = if (quickTestMode) 100000 else 1000000)

  "CappedCompetitorSet" should {
    val cap = 10
    "Be correct count when under cap" in {
      (1 to cap - 5).foldLeft(CappedSet.apply(cap))(_ + _).uniques must_== Some(cap - 5)
    }

    "Be correct count when under cap, with non-unique elements" in {
      ((1 to cap - 5) ++ (1 to cap - 5)).foldLeft(CappedSet.apply(cap))(_ + _).uniques must_==
        Some(cap - 5)
    }

    "Be None when over cap" in {
      (1 to cap + 10).foldLeft(CappedSet.apply(cap))(_ + _).uniques must_== None
    }

    "toSet works without duplicates" in {
      val l = List(1, 2, 3, 7, 9, 21, 65, 73)
      l.foldLeft(CappedSet.apply(cap))(_ + _).toSet must_== l.toSet
    }

    "toSet works with duplicates" in {
      val l = List(1, 2, 2, 7, 21, 21, 65, 21)
      l.foldLeft(CappedSet.apply(cap))(_ + _).toSet must_== l.toSet
    }

    "uniques returns 3 when given 3, 3, 7, 8 when cap set to 3" in {
      (CappedSet(3) + 3 + 3 + 7 + 8).uniques must_== Some(3)
    }

    "uniques returns None when cap exceeded 3, 3, 7, 7, 9, 8 when cap set to 3" in {
      (CappedSet(3) + 3 + 3 + 7 + 7 + 9 + 8).uniques must_== None
    }

    "uniques returns None when cap exceeded 3, 3, 7, 10, 23, 7, 9, 8 when cap set to 3" in {
      (CappedSet(3) + 3 + 3 + 7 + 10 + 23 + 7 + 9 + 8).uniques must_== None
    }

    val max = 100
    implicit val arbitraryInt: Arbitrary[Int] = Arbitrary(Gen.choose(1, max))

    "Passes simple property based test" ! check(prop(
      (cap: Int, toAdd: List[Int]) => {
        val result = toAdd.foldLeft(CappedSet(cap))(_ + _)
        val (uniques, asSet) = (result.uniques, result.toSet)
        val expectedSet = toAdd.toSet
        val expectedSize = expectedSet.size

        (expectedSet, result.exceeded, uniques) must_==
          (if (expectedSize > cap) (expectedSet, true, None) else (asSet, false, Some(expectedSize)))
      }
    ))

    {
      implicit val arbitraryListInt1: Arbitrary[List[Int]] = Arbitrary(for {
        listSize <- Gen.choose(max + 5, max * 10)
        copies <- Gen.choose(1, 5)
      } yield new Random().shuffle((1 to copies).flatMap(_ => 1 to listSize).toList))

      "Exceeds cap with big list" ! check(prop(
        (cap: Int, toAdd: List[Int]) => {
          val result = toAdd.foldLeft(CappedSet(cap))(_ + _)
          result.exceeded must_== true
          result.uniquesOrCap() must_== cap
        }
      ))
    }

    {
      implicit val arbitraryListInt: Arbitrary[List[Int]] =
        Arbitrary(Gen.choose(max + 1, max * 10).flatMap(listSize => Gen.listOfN(listSize, Gen.choose(max + 1, max * 10))))

      "Passes property based test with custom list generator" ! check(prop(
        (cap: Int, toAdd: List[Int]) => {
          val result = toAdd.foldLeft(CappedSet(cap))(_ + _)
          val (uniques, asSet) = (result.uniques, result.toSet)
          val expectedSet = toAdd.toSet
          val expectedSize = expectedSet.size

          (expectedSet, result.exceeded, uniques) must_==
            (if (expectedSize > cap) (expectedSet, true, None) else (asSet, false, Some(expectedSize)))
        }
      ))
    }

    "Passes regression test" in {
      val cap = 46
      val toAdd = List(1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
        28, 29, 30, 31, 32, 9, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 457, 45, 457, 1)
      val result = toAdd.foldLeft(CappedSet(cap))(_ + _)

      val (uniques, asSet) = (result.uniques, result.toSet)
      val expectedSet = toAdd.toSet
      val expectedSize = expectedSet.size

      (expectedSet, result.exceeded, uniques) must_==
        (if (expectedSize > cap) (expectedSet, true, None) else (asSet, false, Some(expectedSize)))
    }

    "Computes the optimal cap value" in {
      CappedSet.optimalCap(46) must_=== 57
      CappedSet.optimalCap(10) must_=== 14
      CappedSet.optimalCap(100) must_=== 115
      CappedSet.optimalCap(1000) must_=== 1843
      CappedSet.optimalCap(10000) must_=== 14745
    }
  }
}
