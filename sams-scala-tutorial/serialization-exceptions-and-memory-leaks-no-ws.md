Please take a look at the Manifesto for Agile Data Science: 

http://www.datasciencemanifesto.org/

## Introduction

Talk:

1. What causes serialization problems (and "memory leaks")
2. Quiz
3. Discussion of ways to prevent/stop these problems
4. The best way to prevent these problems

## What causes serialization problems

Unserializable data transferred across executors, there are 2 kinds of problems:

1. Spark jobs that *may* require transfer of *object* data between JVMs
    - Trying to broadcast non-serializable data
    - Trying to collect non-serializable data
    - Trying to *shuffle* non-serializable data
2. Spark jobs that try to transfer non-serializable "*meta*" data between JVMs via (intentional or unintentional) **transative closure capture**.

## What causes "Memory Leaks" and performance issues

Spark jobs that try to transfer serializable "*meta*" data between JVMs via (intentional or unintentional) **transative closure capture**, and that data is large, or slow to serialize/deserialize.

Here be dragons! Making data serializable can make exceptions go away, but increase memory footprint and slow down jobs.

## Definition - Closure Capture

The *captured closure* of a function *F* is the set of values *C* where *x* is in *C* if and only if:

1. *x* is referenced in the scope of *F*, or
2. *x* is referenced in the scope of a function *G* where there exists a sequence of function calls *F_1, ..., F_N* such that *F* calls *F_1*, *F_1* calls *F_2*, ... , *F_N* calls *G*, or
3. *x* is a value of or referenced in the scope of an instance *h* of a class *H* where *h* is a value of the form 1. or 2.

## QUIZ!

### Q1. Will this cause a serialization exception?

```
scala> class NotSerializable(i: Int)
defined class NotSerializable

scala> sc.makeRDD(1 to 1000, 1).map(i => (i, new NotSerializable(i))).count()
```

### A1. No. 

Because method count only process objects in current node and return number for shuffle, in this scenario, objects of `NotSerializable` won't be shipped between worker nodes or between worker and driver.

```
scala> sc.makeRDD(1 to 1000, 1).map(i => (i, new NotSerializable(i))).count()

...

res0: Long = 1000
```

### Q2. Will any of these cause serialization exception?

```
scala> class NotSerializable(i: Int)
defined class NotSerializable

scala> sc.makeRDD(1 to 1000, 1).map(i => (i, new NotSerializable(i))).groupByKey().count()

scala> sc.makeRDD(1 to 1000, 1).map(i => (i, new NotSerializable(i))).collect()

scala> sc.broadcast(new NotSerializable(10))
```

### A2: All of them! 

Because method `groupByKey` and `collect` will send elements in RDD to driver node, but instantiations from class `NotSerializable` cannot be serialized.

```
...

java.io.NotSerializableException: $line15.$read$$iwC$$iwC$NotSerializable
Serialization stack:
	- object not serializable (class: $line15.$read$$iwC$$iwC$NotSerializable, value: $line15.$read$$iwC$$iwC$NotSerializable@356ee6e2)
	at org.apache.spark.serializer.SerializationDebugger$.improveException(SerializationDebugger.scala:40)
	... a billion more lines ...
```

Lesson: Only performing map operations on non-serializable data in `RDD`s is OK.
# Q3: Will this cause a serialization exception?

```
scala> class NotSerializableFunction extends (Int => Int) {
     |   def apply(i: Int): Int = i + 1
     | }
     
scala> sc.makeRDD(1 to 100).map(new NotSerializableFunction()).reduce(_ + _)
```

### A3 Yes. B

Because objects of `NotSerializable` in RDD cannot be shipped to phase shuffle that is required by method reduce.

```
scala> sc.makeRDD(1 to 100).map(new NotSerializableFunction()).reduce(_ + _)
16/02/26 05:38:59 WARN util.ClosureCleaner: Expected a closure; got $line37.$read$$iwC$$iwC$NotSerializableFunction
...
org.apache.spark.SparkException: Job aborted due to stage failure: Task not serializable: java.io.NotSerializableException: $iwC$$iwC$NotSerializableFunction
Serialization stack:
        - object not serializable (class: $iwC$$iwC$NotSerializableFunction, value: <function1>)
        - field (class: org.apache.spark.rdd.RDD$$anonfun$map$1$$anonfun$apply$3, name: cleanF$1, type: interface scala.Function1)
        ...
Caused by: java.io.NotSerializableException: $iwC$$iwC$NotSerializableFunction
Serialization stack:
        - object not serializable (class: $iwC$$iwC$NotSerializableFunction, value: <function1>)
        - field (class: org.apache.spark.rdd.RDD$$anonfun$map$1$$anonfun$apply$3, name: cleanF$1, type: interface scala.Function1)
```

### Q4: (Ninjas only) How to modify this line with the insertion of 3 characters so that it doesn't throw exception?

```
scala> sc.makeRDD(1 to 100).map(new NotSerializableFunction()).reduce(_ + _)
```

### A4:

```
scala> sc.makeRDD(1 to 100).map(new NotSerializableFunction()(_)).reduce(_ + _)
...
res10: Int = 5150
```

Is sugar for

```
scala> sc.makeRDD(1 to 100).map(i => new NotSerializableFunction()(i)).reduce(_ + _)
...
res10: Int = 5150
```
In second one, data saved in RDD is not objects of NotSerializable but integer, so reduce can be executed successfully.
## IMPORTANT LESSON

In Scala `map(something)` is NOT (always) the same as `map(x => something(x))`!

Will be important later when we talk about memory leaks.

Proof:

```
scala> val sillyFunction: Int => Int = (i: Int) => ???
sillyFunction: Int => Int = <function1>

scala> 

scala> List(1, 2, 3).map(sillyFunction)
scala.NotImplementedError: an implementation is missing
        at scala.Predef$.$qmark$qmark$qmark(Predef.scala:252)
        ...

scala> List(1, 2, 3).map(i => sillyFunction(i))
scala.NotImplementedError: an implementation is missing
	at scala.Predef$.$qmark$qmark$qmark(Predef.scala:252)
	at scala.Function1$class.apply$mcII$sp(Function1.scala:39)
	at scala.runtime.AbstractFunction1.apply$mcII$sp(AbstractFunction1.scala:12)
	at $anonfun$1.apply$mcII$sp(<console>:10)
```

Observe extra stack before we see `TraversableLike`

### Q5: 

```
scala> class NotSerializableA {
         def addOne(i: Int): Int = i + 1
       }
     
scala> val nsa = new NotSerializableA()

scala> sc.makeRDD(1 to 100).map(nsa.addOne).reduce(_ + _)
```

### A5: Yes

For **methods** `map(nsa.addOne)` is actually the same as `map(i => nsa.addOne(i))`

```
scala> List(1, 2, 3).map(new Silly().addOne)
scala.NotImplementedError: an implementation is missing
	at scala.Predef$.$qmark$qmark$qmark(Predef.scala:252)
	at Silly.addOne(<console>:9)
	at $anonfun$1.apply$mcII$sp(<console>:10)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)

scala> List(1, 2, 3).map(i => new Silly().addOne(i))
scala.NotImplementedError: an implementation is missing
	at scala.Predef$.$qmark$qmark$qmark(Predef.scala:252)
	at Silly.addOne(<console>:9)
	at $anonfun$1.apply$mcII$sp(<console>:10)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)
```

### Q6: 

```
scala> class NotSerializableB {
         val addOne = (i: Int) => i + 1
       }
     
scala> val nsb = new NotSerializableB()

scala> sc.makeRDD(1 to 100).map(nsb.addOne).reduce(_ + _)
```
### A7: No exception, it works.

Here, the function `(i: Int) => i + 1` is serializable.

### Q8: Now, can add 3 chars so that it WILL cause a serialization exception?

```
scala> class NotSerializableB {
         val addOne = (i: Int) => i + 1
       }
     
scala> val nsb = new NotSerializableB()

scala> sc.makeRDD(1 to 100).map(nsb.addOne).reduce(_ + _)
```

### A8: Yes!

```
scala> class NotSerializableB {
         val addOne = (i: Int) => i + 1
       }
     
scala> val nsb = new NotSerializableB()

scala> sc.makeRDD(1 to 100).map(nsb.addOne(_)).reduce(_ + _)
```

This causes the exception because **the closure** requires that we serialize `nsb`, not just `nsb.addOne`.

### Q9: Will this one throw exception?

```
scala> class NotSerializableB {
         val addOne = (i: Int) => i + 1
       }
   
scala> class IShouldBeSerializable extends Serializable {
         val nsb = new NotSerializableB()
       
         def addTwo(i: Int): Int = i + 2
       }
     
scala> val ibs = new IShouldBeSerializable()

scala> sc.makeRDD(1 to 100).map(ibs.addTwo).reduce(_ + _)

```
### A9: Yup!

```
scala> sc.makeRDD(1 to 100).map(ibs.addTwo).reduce(_ + _)
org.apache.spark.SparkException: Task not serializable
...
Caused by: java.io.NotSerializableException: $iwC$$iwC$NotSerializableB
Serialization stack:
        - object not serializable (class: $iwC$$iwC$NotSerializableB, value: $iwC$$iwC$NotSerializableB@7dcbb74)
        - field (class: $iwC$$iwC$IShouldBeSerializable, name: nsb, type: class $iwC$$iwC$NotSerializableB)
        - object (class $iwC$$iwC$IShouldBeSerializable, $iwC$$iwC$IShouldBeSerializable@15d92772)
        - field (class: $iwC$$iwC$$iwC$$iwC$$iwC$$iwC$$iwC$$iwC, name: ibs, type: class $iwC$$iwC$IShouldBeSerializable)
```

Because closures are transative! Nor can they be "lazy".

If `nsb` was serializable and a large data set, say a big lookup, then we have a **Memory leak**.
## Memory Leaks / Performance Issues

### With hugeMap

```
scala> class IAmSerializable extends Serializable {
         val hugeMap = (1 to 100000).map(i => (i, i.toString)).toMap
         
         def addTwo(i: Int): Int = i + 2
       }
       
scala> val ias = new IAmSerializable()

scala> sc.makeRDD(1 to 1000, 100).map(ias.addTwo).reduce(_ + _)
...
...
16/02/26 13:08:35 INFO DAGScheduler: Job 0 finished: reduce at <console>:19, took 4.973575 s
res0: Int = 502500
```

### Without

```
scala> class IAmSerializable extends Serializable {
         def addTwo(i: Int): Int = i + 2
       }
       
scala> val ias = new IAmSerializable()

scala> sc.makeRDD(1 to 1000, 100).map(ias.addTwo).reduce(_ + _)
...
...
16/02/26 13:12:29 INFO DAGScheduler: Job 1 finished: reduce at <console>:19, took 0.555915 s
res1: Int = 502500
```

### Lessons

 - Observe difference in memory cost
 - Observe time difference (caused by serializing and broadcasting)
 - Much better since later versions of spark (1.5.0?) since broadcasting is automatic
 - Make sure you only have one executor per node
 
### Common Solutions to Serialization Exceptions

 - Add `extends Serializable` to everything - BAD: ugly, could cause aforementioned issues
 - Prefix `val`s with `@transient` - OK: ugly
 - Turn methods into functions - BAD: very ugly, could create more problems
 - Use `com.twitter.chill.MeatLocker` - BAD: very ugly boilerplate everywhere
 - Use Spores - OK: Not really finished yet, again adds boilerplate

So what should we do? :(

## General Solution: Ban OOP! Go Functional

Object Oriented Programming together with it's horrible design principles causes these problems.

Use FP in it's purest sense, that is **Don't mix your data with your functions**.
Practically this means

 - Almost never put vals in `objects`
 - Almost never put methods in `class`es
 - Use `case class`es for your data
 - Only put methods in `object`s or `implicit class`es
 - Use type-classes and `implicit class`es to create sugar (infix & dot notation over postfix)
 - Use implicit parameters to ease context passing - Do NOT use `class` scope
 - `val`s only really exist in the scope of a method

The benifits include

 - Closure problems vanish
 - Inheritance becomes purely for scope control
 - Inheritance does not effect initialization
 - Clear separation between data and functions acting on data
 - Single responsibility principle is automatic (each function is a responsibility)
 - Multiple inheritence is easy
 - Discourages mutable state
 - Discourage pointless encapsulation (avoid `private`!)
 - All application context, initialization and injection is visible in the `main` method (avoid DI frameworks)
 - GC is faster
 - Serializing tasks is faster
 - Reference dependencies always point into data (case classes), except data may be structured (so point to other data)
 
## Recap

1. Serialization problems are very subtle, make sure you understand the details
2. Making exceptions go away could be just hiding your problem
3. As always, functional programming is the solution!

-------


## Additional Example
```
object NonSerializableObject {
  def f1(input: Int): Int = input + 1
}

object TestSerializable {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf
    val sc = new SparkContext(sparkConf)

    val data = List(1, 2, 3, 4, 5)
    val rdd = sc.makeRDD(data)

    // What is the unserializable task?
    
    // When there exists a reference in the graph of reference dependencies with root nodes being all references in
    // the closure of a function being passed to a higher kinded method of Spark that points to non-serializable data
    // and the resulting Spark DAG requires serializing that data.
    
    val nso = NonSerilizableObject
    rdd.map(x => nso.f1(x)).count()

    // Above example does not work:
    // Exception in thread "main" org.apache.spark.SparkException: Task not serializable
    // 	at org.apache.spark.util.ClosureCleaner$.ensureSerializable

    // How to solve this kind of exception
	
    // Access the object directly without the unnecessary indirection. Since it's static it will already be on every executor.
    rdd.map(NonSerilizableObject.f1).count()
    
    // do NOT just make the object extend Serializable, that would be misunderstanding the problem and could potentially
    // introduce more problems later.
  }
}
```

