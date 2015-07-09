



// What does this line of code do?
def e(s: String): List[Int] = 
  s.grouped(2).toList.take(5).map(Integer.parseInt(_, 16) % 50) ++ s.grouped(2).toList.take(2).map(Integer.parseInt(_, 16) % 9)

// How to solve the RAID problem in 11 chars:

(0/:l)(_^_)

val l: List[List[Int]] = ???
// What does this line of code do?
(List(Nil:List[Int])/:l)(_|@|_|>(_(_:+_)))
