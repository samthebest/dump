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

## How to update state and multithreading

Many argue FP isn't about eliminating all mutations, and this is true.  FP is often confused with "immutability", rather FP encourages you, especially if built into the language by design (like Haskell) to completely decouple business logic from mutations.  The functional programmers delays mutation. When a function in an OOP world would ordinarily "mutate", in a functional world it returns a "mutation" of somesort then later some "mutator" thing has the responability of "commiting" it or actually doing the mutation.

The objective in FP is to make mutation a single responability of some uber-mutator thing that barely has any of it's own business logic - it just handles wiring the business logic together from the POV of multithreading.

What could greatly improve this, and leave even less to do for the uber-mutator would be if the uber-mutator worked in a similar way to a blockchain, or the git version control system.  That is threads or users, or views have their own copy of the database, and some other process handles "merging" these.


## Approach 1 - Locking DB with merges

Need to express the below more simply for sake of talk

```
case class AtomStore[K, V](root: String, keyStr: Str[K], valueStr: Str[V], config: AtomStoreConfig = AtomStoreConfig())

case class AtomView[K](keyToEventKey: HashMap[K, String])

case class DBView(rootToAtomViews: Map[String, AtomView[_]], eventKey: String, prevEventKey: String)

case class Mutation[K, V](atomStore: AtomStore[K, V], key: K, value: V, allowKeyCollisions: Boolean = true)

case class Event(dBView: DBView, eventType: EventType, mutations: List[Mutation[_, _]], ts: Long)

def apply[T](event: Event, validator: Event => \/[DBValidationFail, T], newEventKey: String)
            (implicit fs: FS, logDir: String, serializer: Event => String): \/[DBFail, (DBView, T)]
            
// plus some way to specify how to merge DBViews
```

In fact the idea of periodic saves of in memory hashmaps with event logging is a nice idea. It would probably massively simplify the above.  May want to ditch the above design after all!


## Approach 2 - Blockchain

Instead of specifying a single validation function and some kind of merge function that could merge divergent sequences of events, we specify a function for processing a block of events.

A thread checks to see which thread has the "longest block chain", i.e. has already processed the most events.  It then assumes that to be the true state of affairs and copies any blocks it doesn have.  It then grabs a block of events from the event queue, and executs the "process" function, which may reject some events as inconsistent with earlier events (using some nano-timestamp).

It then writes this block to it's own copy of the block chain.

Now in parallel another thread processes another block of events (which need not be disjoint).

To ensure we do actually get parallel behaviour, we cannot just select the "longest block chain", rather we must merge the longest block chains to form a new block chain.

I guess in essence this is similar to the other model except the merging isn't something happening idly in the background, but the merging happens before the processing of a (block of) event(s).  The elegant aspect is that as the chain grows, when it rejects certain threads chains as not mergable, the orphaning is implicit.

The entry point to the application needs to take the event, put it on the queue, then wait for it to enter any block, then respond with "accepted-1", then after some reasonable length of time, it ought to send a second response saying "confirmed", meaning the probability of it being orphaned is very low.

Now unlike in a real block chain, where there is the potential that threads are evil, and do not reject a chain because they wish to preserve those events over the currently longest chain, we need not worry about such an situation. Therefore only some small fudge factor of a couple of blocks is required to ensure that threads don't get confused, or use out of date information to decide which block is longest.




