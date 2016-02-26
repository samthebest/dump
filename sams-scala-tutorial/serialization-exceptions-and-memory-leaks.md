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

### Q3: 
