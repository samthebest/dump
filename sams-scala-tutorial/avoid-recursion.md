Avoid recursion for anything that is not recursive :)

Sometimes recursive code is much easier to read for recursively defined structures (like Graphs, Trees, etc). This is the main exception.

For everything else, use Scala Collections, `foldLeft`, or:

```
object Utils {
  implicit class PimpedIterator[T](it: Iterator[T]) {
    def whileLeft[C](zero: C)(f: (C, T) => C)(breakCondition: (C, T) => Boolean): C =
      if (it.isEmpty) zero
      else {
        var cum: C = zero
        var cur = it.next()
        while (it.hasNext && !breakCondition(cum, cur)) {
          cum = f(cum, cur)
          cur = it.next()
        }
        cum
      }
  }

  def whileLeft[C](zero: C)(f: C => C)(breakCondition: C => Boolean): C =
    Iterator.continually(()).whileLeft(zero)((c: C, _) => f(c))((c: C, _) => breakCondition(c))
}
```

Almost all tail-recursive functions can be refactored to use the above, and has the advantage of not needing to fiddle around with the logic of the code so that it's properly tailrec.
