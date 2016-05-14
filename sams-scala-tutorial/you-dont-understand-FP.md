# You Don't Understand Functional Programming nor even OOP

## Abstract:

Powerfully Statically Typed Functional Programming is going to become more and more necessary for the future of automation, but it's still misunderstood and underused. Partial exceptions appear to be in concurrency and bug averse applications like aerospace, defense and finance.

An old programmer, like PDP-7 old, once said to me "you won't truly learn language theory until you build your own compilers".  Only recently I followed him up on that, and indeed he couldn't be more right. 

I still include myself in the confrontational generic "you" of the title since I am yet to finish building a compiler, but I still wish to share the myth busting learnings so far. Which will include

 - "Multi-Paradigm" languages like Scala, F#, Python, C# & Java 8 are really OOP plus syntactic sugar.
 - Using lambdas and 2nd-order functions doesn't mean you're doing FP
 - OOP is still useful, but just not for what your using it for
 - You really don't have enough traffic / data to justify mutation (unless you are Google)
 - Databases are evil, they are the children of premature optimisation

## Introduction
 
## In Haskell
 
You don't access a field in data using `myData.myField`, you actually call a function `myField` on `myData` like this: `myField myData`.

In Haskell you don't have inheritence, you have typeclasses. In Scala you kind of have the same thing, but since Scala provides no way to introduce infix notation, it's not as useful.

## Clearly define the entry point to your application

Defer all mutations till the end of your application.  E.g.

```
def main(args: Array[String]): Unit = {
  

  applicationLogic(args = args, db = readEntireDatabase(args)).foreach(mutation => do(mutation))
}
```

This works even for any application, 

## Databases create side effects

You don't need a database, really you don't.

## Why you don't even understand OOP

OOP is all about encapsulating and protecting state. Sometimes, it really is difficult to write your entire application as a bunch of entry points that return a sequence of mutations. The usual reasons are:

 - FP just isn't fast enough
 - FP creates GC pressure
 - You really do have so much data that you cannot just log it all. Use Vector or HashMap for everything else.


Log each event
process event
log processed event

If node dies, if count(log) != count(log processed) then your data is inconsistent and you will need to replay.

Have a thread that dumps your hashmaps to disk every so often with a key that points to the event. Then if your node dies, you don't need to replay your entire event history to restore your DB, you can start from the cache.



