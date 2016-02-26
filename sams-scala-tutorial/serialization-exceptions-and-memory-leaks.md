## Introduction

Talk:

1. What causes serialization problems (and "memory leaks")
2. Quiz
3. Discussion of ways to prevent/stop these problems
4. The best way to prevent these problems
5. The ideal future way to prevent these problems

.

.

.

.

.

.

.

.

.

.

.

.

## What causes serialization problems

Two things:

1. Spark jobs that *may* require transfer of *object* data between JVMs
    - Trying to broadcast non-serializable data
    - Trying to collect non-serializable data
    - Trying to *shuffle* non-serializable data
2. Spark jobs that try to transfer non-serializable "*meta*" data between JVMs via (intentional or unintentional) **transative closure capture**.

.

.

.

.

.

.

.

.

.

## What causes "Memory Leaks" and performance issues

Spark jobs that try to transfer serializable "*meta*" data between JVMs via (intentional or unintentional) **transative closure capture**, and that data is large, or slow to serialize/deserialize.

Here be dragons! Making data serializable can make exceptions go away, but increase memory footprint and slow down jobs.
.

.

.

.

.

.

.

.

.

.

.

## QUIZ!

### Q1. Will this cause a serialization exception?

```
scala> class NotSerializable(i: Int)
defined class NotSerializable

scala> sc.makeRDD(1 to 1000, 1).map(i => (i, new NotSerializable(i))).count()
```

.

.

.

.

.

.

.

.

.

.

.

### A1. No

```
scala> sc.makeRDD(1 to 1000, 1).map(i => (i, new NotSerializable(i))).count()

...

res0: Long = 1000
```

.

.

.

.

.

.

.

.

.

.

### Q2. Will any of these cause serialization exception?

```
scala> class NotSerializable(i: Int)
defined class NotSerializable

scala> sc.makeRDD(1 to 1000, 1).map(i => (i, new NotSerializable(i))).groupByKey().count()

scala> sc.makeRDD(1 to 1000, 1).map(i => (i, new NotSerializable(i))).collect()

scala> sc.broadcast(new NotSerializable(10))
```

.

.

.

.

.

.

.

.

.

.

.

.

.

.

### A2: All of them!

```
...

java.io.NotSerializableException: $line15.$read$$iwC$$iwC$NotSerializable
Serialization stack:
	- object not serializable (class: $line15.$read$$iwC$$iwC$NotSerializable, value: $line15.$read$$iwC$$iwC$NotSerializable@356ee6e2)
	at org.apache.spark.serializer.SerializationDebugger$.improveException(SerializationDebugger.scala:40)
	... a billion more lines ...
```

Lesson: Only performing map operations on non-serializable data in `RDD`s is OK.

.

.

.

.

.

.

.

.

.

.

.

# Q3: Will this cause a serialization exception?

```
scala> class NotSerializableFunction extends (Int => Int) {
     |   def apply(i: Int): Int = i + 1
     | }
     
scala> sc.makeRDD(1 to 100).map(new NotSerializableFunction()).reduce(_ + _)
```

.

.

.

.

.

.

.

.

.

.

.

### A3 Yes

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

.

.

.

.

.

.

.

.

.

.

.

### Q4: (Ninjas only) How to modify this line with the insertion of 3 characters so that it doesn't throw exception?

```
scala> sc.makeRDD(1 to 100).map(new NotSerializableFunction()).reduce(_ + _)
```

.

.

.

.

.

.

.

.

.

.

.

.

.

.

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
        at $anonfun$1.apply(<console>:8)
        at $anonfun$1.apply(<console>:8)
        at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)
        at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)
        ...

scala> List(1, 2, 3).map(i => sillyFunction(i))
scala.NotImplementedError: an implementation is missing
	at scala.Predef$.$qmark$qmark$qmark(Predef.scala:252)
	at $anonfun$1.apply(<console>:8)
	at $anonfun$1.apply(<console>:8)
	at scala.Function1$class.apply$mcII$sp(Function1.scala:39)
	at scala.runtime.AbstractFunction1.apply$mcII$sp(AbstractFunction1.scala:12)
	at $anonfun$1.apply$mcII$sp(<console>:10)
	at $anonfun$1.apply(<console>:10)
	at $anonfun$1.apply(<console>:10)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)
```

Observe extra stack before we see `TraversableLike`

.

.

.

.

.

.

.

.

.

.

.

.

### Q5: 

```
scala> class NotSerializableA {
         def addOne(i: Int): Int = i + 1
       }
     
scala> val nsa = new NotSerializableA()

scala> sc.makeRDD(1 to 100).map(nsa.addOne).reduce(_ + _)
```

.

.

.

.

.

.

.

.

.

.

.

### A5: Yes

For **methods** `map(nsa.addOne)` is actually the same as `map(i => nsa.addOne(i))`

```
scala> List(1, 2, 3).map(new Silly().addOne)
scala.NotImplementedError: an implementation is missing
	at scala.Predef$.$qmark$qmark$qmark(Predef.scala:252)
	at Silly.addOne(<console>:9)
	at $anonfun$1.apply$mcII$sp(<console>:10)
	at $anonfun$1.apply(<console>:10)
	at $anonfun$1.apply(<console>:10)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)

scala> List(1, 2, 3).map(i => new Silly().addOne(i))
scala.NotImplementedError: an implementation is missing
	at scala.Predef$.$qmark$qmark$qmark(Predef.scala:252)
	at Silly.addOne(<console>:9)
	at $anonfun$1.apply$mcII$sp(<console>:10)
	at $anonfun$1.apply(<console>:10)
	at $anonfun$1.apply(<console>:10)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)
```

.

.

.

.

.

.

.

.

.

.

.

.

.

.

### Q6: 

```
scala> class NotSerializableB {
         val addOne = (i: Int) => i + 1
       }
     
scala> val nsb = new NotSerializableB()

scala> sc.makeRDD(1 to 100).map(nsb.addOne).reduce(_ + _)
```

.

.

.

.

.

.

.

.

.

.

.

.

.

### A7: No exception, it works.

Here, the function `(i: Int) => i + 1` is serializable.

.

.

.

.

.

.

.

.

.

.

.

.

### Q8: Now, can add 3 chars so that it WILL cause a serialization exception?

```
scala> class NotSerializableB {
         val addOne = (i: Int) => i + 1
       }
     
scala> val nsb = new NotSerializableB()

scala> sc.makeRDD(1 to 100).map(nsb.addOne).reduce(_ + _)
```

.

.

.

.

.

.

.

.

.

.

.

.

### A8: Yes!

```
scala> class NotSerializableB {
         val addOne = (i: Int) => i + 1
       }
     
scala> val nsb = new NotSerializableB()

scala> sc.makeRDD(1 to 100).map(nsb.addOne(_)).reduce(_ + _)
```

This causes the exception because **the closure** requires that we serialize `nsb`, not just `nsb.addOne`.

.

.

.

.

.

.

.

.

.

.

.

.

.

.

.

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

.

.

.

.

.

.

.

.

.

.

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

.

.

.

.

.

.

.

.

.

.

.

.

## Memory Leaks

```
scala> class IAmSerializable extends Serializable {
         val hugeMap = (1 to 10).map(i => (i, i.toString)).toMap
         
         def addTwo(i: Int): Int = i + 2
       }
       
scala> val ias = new IAmSerializable()

scala> sc.makeRDD(1 to 100, 10).map(ias.addTwo).reduce(_ + _)
```

 - 
