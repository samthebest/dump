# Intro

We will try to answer five questions:

 - What is functional programming?
 - Why would we use it?
 - Why Scala in particular?
 - Why is it appropriate in terms of Big Data?
 - Why is it appropriate for server-side web development?

This talk will **not** cover stuff like Monoids, Monads, Functors, Categories, etc.

.

.

.

.

.

.

.

.

# What is Functional Programming?

1. Things are functions
2. Functions are things
3. Functions are functions (huh?)

.

.

.

.

.

.

.

.

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

.

.

.

.

.

.

.

.


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

.

.

.

.

.

.

.

.

## Functions are *really Functions*! AKA Pure Functions

 - That is they are functions in the formal *mathematical* sense
 - They **only** take some parameters and return a result

I.e.

1. They do NOT *change anything*
2. They can NOT depend on *change*

(NOTE: Style and practice - not forced by the language.)

.

.

.

.

.

.

.

.

### Breaks 1

```
scala> var iCanChange = 0

scala> def notReallyAFunction(bar: Int): Int = {
         iCanChange = iCanChange + 10
         bar
       }
     
scala> notReallyAFunction(5)
res16: Int = 5

scala> iCanChange
res17: Int = 10
```

This is called a "side effect"

.

.

.

.

.

.

.

.

### Breaks 2

```
scala> var iCanChange = 0
iCanChange: Int = 0

scala> def notReallyAFunction(bar: Int): Int = iCanChange + bar
notReallyAFunction: (bar: Int)Int

scala> notReallyAFunction(5)
res9: Int = 5

scala> iCanChange = iCanChange + 3
iCanChange: Int = 3

scala> notReallyAFunction(5)
res10: Int = 8
```

.

.

.

.

.

.

.

.

### Pure Function Summary

Basically if you never use variables (in Scala `var`), and never use any code that uses a `var` (e.g. mutable data structures) then your functions will be "Pure" (ignorning weird stuff like IO).

.

.

.

.

.

.

.

.

### Pure Functions Are Important

The most important part of Functional Programming is purity, not syntax.  So points 1 & 2 are less important than purity.

This principle is so important because it eliminates the vast majority of bugs.

A system's complexity is determined by the number of moving parts, the more complex a system the harder it is to understand and consequently the more mistakes will be made.  Therefore having a system with no moving parts eliminates almost all complexity and thus almost all mistakes.

.

.

.

.

.

.

.

.

.

### Key Design Differences to Procedural

 - Passing functions around as things
 - Pure Functions

.

.

.

.

.

.

.

.

### Key Design Difference to OOP

#### Deduplication, Scope control and Abstraction

 - OOP: Use functions, complex class heiarchies, injection, imports and interfaces
 - FP: Use static functions and imports (with some syntactic sugar to allow for infix notation)

#### Functions, State and Data

 - OOP: classes mix functions, state and data
 - FP: functions are separate to data (`objects` for functions and `case class`es for data)
 - FP: state is delayed, or kept entirely outside the application code (like a DB, filesystem, client state)

#### Bug Elimination

 - OOP: `private`, hiding functionality, and/or forcing people to copy and paste code instead of just calling it
 - FP: immutability. (no code hiding is necessary, because there is no state to break)
 - FP + Static Typing: The compiler checks your work for you!

So it's much harder to leap from OOP to FP, than Procedural to FP. The switch from OOP requires unlearning, which is harder than learning.

.

.

.

.

.

.

.

.

# Why would we use it?

## VS both procedural & OOP

 - Higher Ordered Functions (like map) remove endless repitition of idioms & control structures (like `for`, `while` loops)
 - Very easy to reason about as there are no hidden dependencies, nor effects
 - Very easy to change FP code. E.g. no matter where you put a line of code, it will always do the same thing!
 - Very easy to get code to run in parallel

.

.

.

.

.

.

.

.

## VS OOP

 - OOP has 100s of principles, rules, practices and design patterns, without them code becomes unmaintainable
 - FP has only one rule: DRY (Don't repeat yourself), all other issues are addressed just by being Functional
 - OOP ends up resulting in long strange names for things because all functions have to be wrapped into classes, e.g. `SimpleBeanFactoryAwareAspectInstanceFactory`, `AbstractSingletonProxyFactoryBean`, `BeanContextServiceProviderBeanInfo`, `AbstractAnnotationValueVisitor6`, `AbstractAnnotationConfigDispatcherServletInitializer`
 - FP doesn't require functions to be in classes, nor even have a name (c.f. anonymous functions). 
 - Furthermore abstract concepts are handled by the right tool - mathematics, so you will see things like `Monad`, `BifunctorOps`, `SemiGroup`, `Monoid`, which have established meanings you can Google and understand.

.

.

.

.

.

.

.

.

# Why Scala in particular?

We could use *OCaml, ML, Haskell, Clojure, F#, Scala, Lisp, Erlang, Rust, ...*

If we want to avoid esoteric / acedemic languages and we want a lot of libraries that hook into modern technologies, then that (arguably) limits us to

*F#, Clojure, Scala*

If we want to avoid .NET, or we want to stick to the JVM, then we are limited to

*Clojure, Scala*

Finally, if we want a language that's *either* native to Big Data tools like Kafka and Spark, or we want *static typing* all that remains is:

**Scala**

.

.

.

.

.

.

.

.

## Dynamic vs Static - It's a tradeoff, like most things

In a nutshell:

 - Dynamic: reduces the time spent initially writting code, but increases the time spent debugging code. Great for high-level logic, or string based worlds, like DevOps.
 - Static: increases the time spent initially writting code, but nearly eliminates time spent debugging code.  Great for detailed logic, or data driven worlds, like Big Data or low latency programming.

.

.

.

.

.

.

.

.

# Why is it appropriate in terms of Big Data?

Little left to say, to reiterate:

 - Native to Kafka and Spark
 - Hooks into other JVM tech
 - Makes reasoning about parallel processing easy

.

.

.

.

.

.

.

.

# Why is it appropriate for server-side web development?

Little left to say, to reiterate:

 - Detailed logic is robust, terse and easy to maintain
 - Hooks into other JVM tech
 - Makes reasoning about multi-threaded applications easy
 - Moore's law has run out, if our applications need to be fast, we need to use multiple CPUs


