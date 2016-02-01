# Intro

We will try to answer five questions:

 - What is functional programming?
 - Why would we use it?
 - Why Scala in particular?
 - Why is it appropriate in terms of Big Data?
 - Why is it appropriate for server-side web development?

The hope is to mix between bullet points and code snippets that you could copy and paste into a shell then experiement further.  This talk will **not** cover stuff like Monoids, Monads, Functors, Categories, etc.

# What is Functional Programming?

## Things are Functions

You can *apply* most things in Scala as if they were a function, e.g.

```
scala> val mySet = Set(1, 3, 5, 6)
mySet: scala.collection.immutable.Set[Int] = Set(1, 3, 5, 6)

scala> mySet(4)
res0: Boolean = false

scala> mySet(5)
res2: Boolean = true
```

So `mySet` is a function from `Int` to `Boolean`, can you guess what it is?

## Functions are Things

We can declare and pass functions around like things. E.g.

```
scala> val addOne = (i: Int) => i + 1
addOne: Int => Int = <function1>

scala> val list = List(1, 2, 2, 5, 5, 6)
list: List[Int] = List(1, 2, 2, 5, 5, 6)

scala> list.map(addOne)
res6: List[Int] = List(2, 3, 3, 6, 6, 7)
```
