object ProfilingUtils {
  def time(f: => Unit): Double = {
    val now = System.nanoTime
    f
    val took = System.nanoTime - now
    println("Took " + took / (1000.0 * 1000.0) + " milli seconds")
    took
  }
  
  def time(f: => Unit, s: String): Double = {
    val now = System.currentTimeMillis()
    f
    val took = System.currentTimeMillis() - now
    println(s + " took " + took + " milli seconds")
    took
  }

  def doMany(n: Int, f: => Unit): Unit =
    (1 to n).foreach(i => f)

  def timeMany(n: Int, f: => Unit): Double = time(doMany(n, f))

  def example: Double =
    timeMany(100, {
      println("Hello world, I'm being profiled")
    })
}
