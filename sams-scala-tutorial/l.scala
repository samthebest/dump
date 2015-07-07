

def e(s: String): List[Int] = 
  s.grouped(2).toList.take(5).map(Integer.parseInt(_, 16) % 50) ++ s.grouped(2).toList.take(2).map(Integer.parseInt(_, 16) % 9)
