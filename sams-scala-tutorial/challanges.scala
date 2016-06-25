
Given a list `l` of `Int`s, that is `val l: List[Int]`, where it is known that exactly one number in the list occurs an odd number of times, write an expression that's value will be the number that occurs an odd number of times.























// What does this line of code do?
def e(s: String): List[Int] = 
  s.grouped(2).toList.take(5).map(Integer.parseInt(_, 16) % 50) ++ s.grouped(2).toList.take(2).map(Integer.parseInt(_, 16) % 9)

def l(s: String): List[Int] = 
  s.grouped(2).toList.take(6).map(Integer.parseInt(_, 16) % 60)

// How to solve the RAID problem in 11 chars:

(0/:l)(_^_)

val l: List[List[Int]] = ???
// What does this line of code do?
(List(Nil:List[Int])/:l)(_|@|_|>(_(_:+_)))
