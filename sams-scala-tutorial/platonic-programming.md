Quick Links

 * [Class Oriented Programming Sucks](#class-oriented-programming)

# 1. Introduction

Platonic Programming (PP) is the future.  An alternative name for PP could be "Complexity Minimising Programming".

Practically PP is quite simple to apply, but the motivating theory assumes ~some~ a lot of acedemic background particularly within computation, complexity and information theory.

Scala programmers can feel free to skip to the Applications in Scala section where we give some simple to follow rules that are derived from the theoretical acedemic sections.

I have placed the "Princples of PP" section above the motivating, theoretical and defining sections.  Therefore it is highly recommended you read this section last (I put this section early for easier reference for when you have read the other sections).

## Hell Is Other Peoples Code

PP is in stark contrast to SOLID, OOP and Design Patterns, which we will collectively refer to as Anthropocentric Principles or Anthropocentric Programming (or just AP). AP currently makes up the vast majority of code, and the vast majority of programmers follow these principles by faith and fashion.  We argue AP is fundementally flawed and is the root of all of the following problems:

1. High bug density
2. Code that is difficult to understand, refactor and test
3. Code that is difficult to add new features to
4. Verbose code
5. Unhappy programmers, late delivery and the **end of the world**.  

No seriously, due to compounding super-exponential technology advancements human programming incompetency could be the main reason we don't yet live in a utopia.  This is what happens when you optimise according to AP https://github.com/EnterpriseQualityCoding/FizzBuzzEnterpriseEdition, or this https://www.youtube.com/watch?v=7RJmoCWx4cE.  If this is how insane Fizz Buzz (or Hello World) can get imagine how terrifying it can get for a non-trivial program.  [Soylent Green](https://en.wikipedia.org/wiki/Soylent_Green) is just round the corner unless we stop this!

The justification is as follows:

 - SOLID & Design Patterns are not formally defined, nor is it even possible in principle to formally define them (with the exception of Liskov Substitution).  They are inherently subjective, often incompatible and usually impertinent within the context of Functional Programming.
 - SOLID & Design Patterns are at best Social Sciences, yet try to apply to what is really Mathematics.  No other branch of mathematics has ever had some set of anthropocentric and social principles imposed upon it, to do so would be absurd.
 - Even if we consider AP as a Social Science, for a theory of Social Science to be a success it will require vast amounts of experiments and statistical work.  AP has no such evidence base; very few studies exist.

## Quick History of Languages

The irony is that some languages make AP difficult, to nearly impossible.  For example Lisp, Erlang, Haskell and OCaml are founded on Functional Programming, which manages to eliminate the vast majority of the problems AP faces.  Ironically these languages are not that new, in fact Lisp is one of the oldest languages there is.

Some modern attempts have been made to mitigate the hell we find ourselves in.  For example Rust and Go have made attempts to standardise approaches to programming that removes the subjectivity, verbosity and complexity.

The worst languages ever invented and the main source of hell, unfortunately are probably the most popular, namely Java and C#.  The worst decisions these two languages both made are:

 - Every function has to be arbitrarily tied to a containing scope
 - Class Oriented Programming, by design, forces a complex coupling of many functions
 - Access level modifiers on functions within types, or scope-namespace mangling.  

Observe how these catastrophically bad language design decisions only occur in basically two languages out of dozens of others, yet these two languages are the most popular.  The popularity is probably due to the necessity of vast numbers of developers in order to get anything done, just like we need hundreds of people with a spade to dig a hole when compared to one person with a digger. Anyway, let's move on.

# Principles of PP

We give the principles only as Comparative Principles since in practice refactoring and improving code should happen in a sequence of small iterations (pull requests). 

Given functionally equivalent programs we ought to use the following principles to choose one over the other, where the principles given earlier take higher priority over those given later

## 1. Triangulation Principle

The program with the strongest Depth Triangulation Table should be preferred.

This principle has the highest priority since correctness should always take preference over anything else.

## 2. Basic Complexity Principle

The program (not including its tests) with the lowest Basic Complexity (AST length) should be preferred.

This principle is second, since simplicity with respect to the Programming Language is second to correctness.

## 3. Kolmogorov Complexity Principle

The program (not including its tests) with the lowest Kolmogorov Complexity should be preferred.

This comes after Basic Complexity since simplicity with respect to other Programming Languages is second to the simplicity with respect to the Language being used.

## 4. Call Complexity Principle

The program (not including its tests) with the lowest Call Complexity should be preferred.

The justification why this sits above state complexity has its own section in "Long Justifications"

## 5. State Complexity Principle

The program (not including its tests) with the lowest State Complexity should be preferred.

## 6. Scope Complexity Principle

The program (not including its tests) with the lowest Scope Complexity should be preferred.

## 7. External Inherited Complexity Principle

The program (not including its tests) with the lowest External Inherited Complexity should be preferred.

## 8. Internal Inherited Complexity Principle

The program (not including its tests) with the lowest Internal Inherited Complexity should be preferred.

It's more important to avoid using external libraries than native libraries.

## 9. Runtime (or Algorthimic) Complexity Principle

Finally, faster programs, or programs that use less memory, should be preferred. Please see Long Justifications.

## 10. Test Complexity Principle

Principles 1 to 8 but applied to the tests.

Note that test complexity comes way way after everything else.  Please see Long Justifications.

## 11. Non Obstructive Programming Principle

If both programs are completely equal in all respects, we should favour the program that is "closer" (in terms of diffing the ASTs) to a better program.

This principle allows us to actively discourage changes that prevent further improvements/changes.

# Theory, Definitions and Theorems

## Central Tenet of PP

There does not exist an objectively perfect (non-trivial) program, but for any pair of programs we can compare them objectively.

Therefore an objective of subjects is to refactor programs so that we make objective improvements.

## Definition - Equivalent

We say program `P_1` and `P_2` are equivalent if for any input `I`, `P_1(I) = P_2(I)` when the two programs are run in identical universes (i.e. the computer and external states are identical).

## Examples - Equivalent

Below, `f` and `g` are equivalent.

```
def f: Int = 2
def g: Int = 1 + 1
```

Furthermore

```
def f: Int = 2 + readFile("myfile").length
def g: Int = 1 + 1 + readFile("myfile").length
```

are also equivalent even though they depend on an external state so may not always produce identical results in practice.

## Definition - Call Graph

For any program `P` the **call graph** `G_P` is the graph `(V, E)` where for any `v`, `v` is in the vertex set `V` if and only if it is a function in `P`, and for any `e`, `e` is in the edge set `E` if and only if `e = (f, g)` where `f` calls `g` in the program `P`.

So a **leaf function** is a function that calls no other function.  The **height** of a function `f`, written `H(f)`, is defined recursively as follows:

 - For any **leaf functions** `f`, `H(f) = 0`
 - For any function `f` that calls `g_1, ..., g_n`, then `H(f) = max_i(H(g_i)) + 1`

**Note:** We do not include functions native to a language in the call graph.  How we consider functions from an external library will be addressed later.

## Definition - Abstract Syntax Tree and Basic Complexity

The AST of a function `f` is the function written in reverse polish notation (or equivalently Lisp form).  The **Basic Complexity** of a function is the length of the AST, where each symbol has length 1 (equivalently the length of the Lisp form not counting the parens).

E.g. the lisp form of the expression `l.map(f)` is `(map l f)`, so it's Basic Complexity is 3.

## Definition - Kolmogorov Complexity

The Kolmogorov Complexity (KC) of a function `f` is the length of the shortest program (in some chosen language L) that outputs the AST of the function.  In practice Kolmogorov Complexity must be intuited, not computed, since (a) the choice of language L is arbitrary, (b) KC is not itself a computable function.

E.g. We intuitively can imagine that the shortest program that outputs the expression `1 + 2 + 3 + 4 + 5 + 6` is shorter than the shortest program that outputs the expression `1 + 2 - 3 + 4 - 5 - 6`.

## Definition - Triangulation

For any function with tests `t_1, ..., t_n` and a type signature `(p_1, ...., p_n) -> r` we call the pair `((t_1, ..., t_n), (p_1, ...., p_n) -> r)` the **functions build constraints**.  The collection of all constraints for a program is called the **programs build constraints**.

### Finitary Triangulation

For any function `f` with build constrains `B_f`, let `F` be the set of functions that satisfy `B_f`.  If we partition `F` into equivalence classes (i.e. subsets of `F` where for each subset every function is equivalent) we say `f` is **finitarily triangulated** if this parition is finite. Or we say it is **`N`-Triangulated** where `N` is the cardinality of the partition.

### Ideal Triangulation

This is when a function `f` is `1`-Triangulated.  In other words, every function that satisfies the build constraints is equivalent to `f`.

Note that for any function that is `N`-Triangulated, it is possible to add only `N - 1` tests to make it ideally triangulated.

Usually, in the real world, the tests plus the type signature will not be sufficient to even achieve finitary triangulation. So we define the following

### Mutually Exclusive

Observe that if a function is N-Triangulated then it is not M-Triangulated when N != M.

### Complexity Augmented Triangulation

A function `f` is **AST Traingulated** or **A-Triangulated** if and only if, for any function `g` if `g` is not equivalent to `f` but does satisfy the build constraints then `g` has strictly greater Basic Complexity than `f`.

A function `f` is **Kolmogorov Traingulated** or **K-Triangulated** if and only if, for any function `g` if `g` is not equivalent to `f` but does satisfy the build constraints then `g` has strictly greater Kolmogorov Complexity than `f`.

In other words, a function is A or K Triangulated if all non-equivalent functions that build are more complicated.

In practice one will need to intuit whether or not a function is A or K Triangulated.  Usually a function written with perfect TDD will be.  This does not mean the concept is subjective, it just means it is not computable.  In the vast majority of cases one ought to be able to provide a short argument that proves or disproves the triangulation of a function.

### Non Deterministic Triangulation

Some testing frameworks will randomly generate test cases in order to pseudo-rigorously test generic properties on a function (that may be difficult to constrain with the type system alone).

In PP these kinds of tests are forbidden unless the seed of the random generator is fixed.  This essentially collapses a single non-deterministic test into a large collection of ordinary tests.  Strictly speaking then, these kinds of "Property Based Tests" add little, but in practice such tests can be useful to retrospectively add to a codebase with few tests.

## Definition - Triangulation Strength

We say a triangulation type, say X-Triangulation is stronger than say Y-Triangulation, where X, Y can be 1, N, A, K, if and only if, for any function `f`, for any set of build constraints `X_f` that form an X-Triangulation, for any a set of build constraints `Y_f`, if `Y_f` forms a Y-Triangulation then `Y_f` is strictly weaker than `X_f`.

## Ordering A & K Triangulations

Note that since Kolmogorov Complexity assumes a reference language, it's not possible to say if A-Triangulation is stronger or weaker than K-Triangulation.  So we define:

A&K-Triangulation is the conjunction of A-Triangulation and K-Triangulation.  Similarly A|K-Triangulation is the disjunction.

## Theorem - Triangulation Well Ordering Theorem

Triangulation types (1, N, A&K, A|K) under the strength relation form a well ordering.

## Proof / Remark

Follows quite naturally from the observation that all triangulation types are mutually exclusive and by understanding Strength like the (non) existence of a path.

## Theorem - Triangulation Transitivity

If a function `f: A -> B` is X-Triangulated, and `f` calls `g: X -> Y` such that the parameters of `f` are passed through a bijection `p: A -> X` then passed to `g`, then `g` is at least X-Triangulated.  Note that the converse need not be true.

### Proof

Exercise (observe how lower level functions tend to have better test coverage than high level functions).

## Definition - Depth Triangulation Table

A programs **depth triangulation table** is the maximum level of triangulation (i.e. `(1, N, A&K, A|K, None)`) for functions of each Height.

For example suppose we have a simple program with only 2 functions `f` and `g`, and `f` calls `g`.  Suppose `g` is 1-Triangulated and `f` is A|K-Triangulated, then we could write the table as follows: `((H = 0, 1), (H = 1, A|K))`.

Furthermore we have an implicit ordering of the strength on tables of the same length, e.g. `((H = 0, 1), (H = 1, A|K))` is stronger than `((H = 0, N), (H = 1, A|K))`, and `((H = 0, N), (H = 1, A|K))` is stronger than `((H = 0, N), (H = 1, None))`.  `((H = 0, N), (H = 1, A|K))` is stronger than `((H = 0, 1), (H = 1, None))`

Note that the 0-height functions are more important for triangulation than the 1-height functions and so on.  This is to resolve the contention when a refactoring will result in an improvement for lower height functions, but a regression for higher height functions.  This justifications are:

 - once lower height function triangulations have been improved, it's easier to triangulate higher height functions by re-using the tests and types through composition (see last section on Applications with Scala for examples)
 - by the transitivity theorem, if we can triangulate higher height functions without first triangulating the lower height functions, then it must mean that the higher functions are only partially calling the lower functions.  Put another way, we are likely to have redundant complexity in our lower functions, and thus should refactor accordingly.

## Call Complexity

If we where to write down every (potentially infinite) call path a program can take by only looking at it's call graph, the **call complexity** is the Kolmogorov Complexity of this sequence.

For example a program with 2 functions `f` and `g` where `f` calls `g` has only 1 path, so the sequence is `((f, g))`, so this will have very lower call complexity (the simple program `println("((f, g))")` would output this sequence.  A program with 1 recursive function `f` has the following paths: `(f), (f, f), (f, f, f), ...`, this will have higher call complexity (exercise, write a program to output this sequence).

## Scope Tree

A Scope Set is a set of collection of parameters and variables in the same scope.  The Scope Tree is the tree of Scope Sets, where an edge `(s_1, s_2)` means `s_2` is a subscope of `s_1`.

### Example

This function

```
def foo(x: Int, y: Int): Int = {
  val xy = x * y
  val bar = {
    val one = 1
    x + one
  }
  xy ^ bar
}

val ten = 10
```

has this scope tree:

```
(((ten), (x, y)), ((x, y), (xy, bar)), ((xy, bar), (one)))
```

while this function

```
def foo(x: Int, y: Int): Int = x * y ^ (x + 1)

val ten = 10
```

has this scope tree:

```
(((ten), (x, y)))
```

## Scope Complexity

Is the Kolmogorov Complexity of the scope tree.

## Inlining Expressions Theorem

When we use a variable to name an expression that is only used once, if we inline this expression we reduce the Basic, Kolmogorov and Scope complexity of the program.

## Natively Inherited Complexity

Given a program P, we can consider all code transitively used by native library usages in P. This set of *inherited* code can be measured with either Basic or Kolomogorov Complexity, giving a notion of **Inherited Complexity**.

## External Inherited Complexity

Similarly external library usages can be considered in the same way.

## State

### Definition - Referential Transparency

An expression E is referentially transparent if and only if for any program P replacing E with it's value gives an equivalent program.

Note that for this definition to make practical sense, we must ignore the situations when evaluating the expression would run out of memory.

### Definition - State Complexity

We define the **State complexity** of a program as the number of non-referetially transparent expressions.

### Theorem - State Monism

Given an infinitely fast processor and an infinite amount of memory, every program can be refactored to have at most 1 variable (i.e. `var` in Scala) while remaining functionally equivalent.  Furthermore this `var` need only occur in the entry point of the application within the lambda of an infinite stream of inputs.  This means only one function, the entry point, mutates anything, while all other functions are pure.

Similarly we could use a single mutable variable instead (something like `val x: Mutable` in Scala).

**Note:** Most modern functional languages provide many native functions that hide away `var`s and most modern computers are very powerful, therefore most modern programs should respect State Monism.

### Examples

```
def main(args: Array[String]): Unit = {
  var state: State = State.init
  
  readInputContinually().foreach(input =>
    state = updateState(input, state)
  )
}

// A pure function
def updateState(input: Input, state: State): State = ...
```

**Note:** Context objects encapsulating external state, for example a database context, should also be considered mutable variables.  So they could be included in this single state variable, although practically this could mean dumping the entire database to memory when we call the lower level pure functions or passing a concurrency locked read only database context.

```
def main(args: Array[String]): Unit = {
  val db: Database = Database.init
  
  readInputContinually().foreach(input =>
    db.lock()
    db.apply(databaseUpdates(input, db.inMemoryReadOnlyCopy()))
    db.unlock()
  )
}

// A pure function
def databaseUpdates(input: Input, db: ReadOnlyDatabase): DatebaseUpdates = ...
```

Now this can work for the majority of small scale applications.  But we will face performance issues for larger scale applications/data. In particular because we (a) dump the entire database to memory, (b) lock the entire database.

### Theorem - Interpreted State Monism

Given an reasonably fast processor and amount of memory, every program can be refactored to have at most 1 variable (i.e. `var` in Scala) while remaining functionally equivalent.  Furthermore this `var` need only occur in a "read-write chain" using interpreters, in the entry point of the application within the lambda of an infinite stream of inputs.  

### Definition/Example - Read Write Chain or Idealised Monolith

We will define a read-write chain by example

```
case class Instructions(lockables: Lockables, 
                        databaseUpdates: DatabaseUpdates, 
                        nextThingsToRead: Reads, 
                        adt: AbstractDataType)

def main(args: Array[String]): Unit = {
  val db: Database = Database.init
  
  readInputContinually().foreach(input =>
    foldLeftUntil[Instructions](Instructions.empty)(instructions => instructions.isComplete, instructions => {
      db.lockAndUnlock(instructions.lockables)
      db.apply(instructions.databaseUpdates)
      nextInstructions(input, db.read(instructions.nextThingsToRead(input)), instructions)
    })
    
    db.apply(databaseUpdates(input, db.inMemoryReadOnlyCopy()))
    db.unlock()
  )
}

// A pure function
def nextInstructions(input: Input, relevantDbState: DbSubState, instructions: Instructions): Instructions = ...
```

Note there will exist many equivalent formulations where the order of reading, writing, producing new instructions and locking is slightly different.  Now since we only lock parts of the database, and we only read parts of it, this model can be scaled to any application.

### Aside on Actor Systems, Message Queues and Microservices

The above *Idealised Monolith* may become difficult to manage in a large organisation with many teams.  In particular since the function `nextInstructions` is extremely generic and weakly typed, understanding the sequence of locking, reading and writing may become unclear.   Put simply, the application lacks obvious modularity.

The recommended solution to this is refactor the application into N applications that use non-interpreted state monism.

In practice this usually means employing two powerful technologies; low latency databases (e.g. RocksDB, Redis), and messages queues (e.g. Kafka RabbitMQ).  Other technologies that can be useful here are columnar databases where it is assumed we have a near infinite amount of storage space, and so we can essentially just keep appending data and never overwrite/delete it.  If we are only appending data we need not worry so much about locking.

This said, adding technologies will add some Inherited Complexity and will increase the complexity of the continuous deployment code, which ought to be included as part of the program.

# Constructing an Objective Set of Programming Principles

We now have a large toolset of definitions and formalisms to work with.  Nevertheless, what do we really mean when we say one program is "better" than another?  Well we can start with this informal definition:

> Programs ought to be correct and simple to change, while being as simple as possible.

To make this formal we can use our formal definition of Triangulation to serve as a proxy for "correct" and our formal definitions of complexity to serve as a proxy for "simple".  The latter may seem controversial - how do we know that humans understand things that are simpler according to these mathematical definitions?  For this I think we can use the following axiom on "understanding":

> If you can't explain it simply, you don't understand it well enough. - Einstein

Now to explain an object is to output a program that would output the object in question.  To do that simply, is to output the shortest program.  This provides a very neat argument for using Kolmogorov Complexity as a definition of simple, and using AST length as a heuristic for Kolmogorov Complexity.

The main catch with Kolmogorov Complexity is that we need a base language (or context) to start with, i.e what language should we write this program in?  For example French and English people will obviously disagree on which texts are easier to understand.  Nevertheless if for any French or English text, we prefixed a translation dictionary, French and English people could start to agree.  This means people will in general only disagree by some constant amount due to their choice of language.  When the problem at hand gets sufficiently large, or as languages overlap/converge, this constant vanishes.

## Context Convergence Hypothesis

Two or more programmers working together and teaching each other their language, colloquialism, etc will converge torwards programs that are mutally "perfect" as time goes to infinity provided they use Kolmogorov Complexity as the foundation of their optimisation.

This may seem like a weak statement, since surely any team working for an infinite amount of time could produce a perfect program?  Wrong!  Suppose a team uses, say OOP & SOLID, and after a million years of refactoring they produce a program `P`.  If this program `P` maximally satisfies OOP & SOLID the team will stop refactoring according to their principles.  Even if there exists a program `P'` that is shorter and simpler than `P` the team will not choose that program if their only set of principles is OOP & SOLID.  Furthermore if `P'` violates OOP & SOLID, which I believe is completely inevitable, then the team would have to reject it.

This is exactly what we see in industry.  Programmers only exposed to OOP, like Java and C#, take only a year or two to converge upon the same style and happily nod their heads in agreement for the next 20 years.  What is really horrifying is that when these kinds of developers are left to refactor code for long periods of time, they tend to make the programs *longer* and *more complicated* in order to maximise according to their insane principles.

This being the jovial example https://github.com/EnterpriseQualityCoding/FizzBuzzEnterpriseEdition

# Principle of PP Continued

## Long Justifications

### Call vs State Complexity

The call complexity principle is considered more important that the state complexity principle.  This is because objectively speaking calling a function is actually implicitly mutating a hidden variable; the stack.  Reasoning about a stack is itself a difficult thing to do, especially in the case of recursion since this makes our stack unbounded.

In practice this could mean refactoring a recursive function to be an iterative function that mutates a variable (provided principles 1 to 3 are unchanged).  To many functional programmers this seems counter intuitive, but in Platonic Programming we are interested in the mathematical structure of the entire application within all contexts.  Nevertheless principles 1 - 3 still sit higher than Call Complexity, which means when a recursive function is syntactically obviously simpler than it's *only* iterative counterpart, then we should still pick the recursive function.

Please see Applications in Scala section.

### Test Complexity Comes Last

This means we should not make our program more complicated to make it easier to test, the classical example being adding a lot of dependency and function injection.  This means we need to be more intelligent about how to organise and generate tests.  In particular we ought to consider test generators (i.e. code that generates test cases, rather than listing all the cases out manually).

Also note that the way we have defined triangulation starts from the bottom up.  Combine this with low call complexity and we can hope that an application is defined in terms of a few layers, where the bottom layer is much wider than the others.  The higher layers ought to be simple compositions of lower level functions, and so they don't need much testing in order to become A-triangulated. This is because a higher level function just composing a bunch of lower level functions is already syntactically very simple.

### Runtime Complexity is low rank

Note that we are comparing functionally equivalent programs.  So if one program takes the length of the universe to run, while another runs in 1 second, these are NOT functionally equivalent.

In large enterprise organisations, they have this archiac notion of "non-functional requirements".  This is a massive abuse of language.  If a requirement, say to run in less than 10 minutes, is not a functional requirement then this implies if we dropped this requirement the program would still be "functional".

# Exceptions and Serious Application of PP

Programming is in the real world, and every rule that tries to apply to the real world will have exceptions.  Therefore we still don't condone PP as an absalute system.  We do consider PP as a huge improvement since it is at least formally defined.  This means when programmers argue about an approach they at least can start from somewhere where they have to agree.

Currently, no two programmers that have an impressive set of diverse experiences will agree on anything, even definitions.  This is the major success of PP, is that it at least adds some formal definitions we can start with.

## Large Jumps in Lower Ranked Principles - Do we need weights?

Sometimes we can make a very small gain in, say principle 1, say by adding a call to an external library and removing our own implementation, but this could have catestrophic consequences to principle 6.  I.e. the implementation provided by the external library could itself be terrible.

So in actual fact what we really want is not a rank on the principles but a weight.  Then we can assign a number to each program by summing up all the various forms of complexity.  That said, I've refrained from discussing weights as this will bring PP too far away from practical application.  Perhaps in 1000 years, or some alien race, (or my garage project?!) has developed a compiler that evaluate all of these principles in such a way.  Until then we will still have to use some heuristics.

## Triangulating Library Calls

How much testing should we do when we call libraries? Well the answer is to consider adding the libraries tests to ones own suite.  So you must read the tests for the library and decide if they are good enough, or if they need more.

From my own experience I often do this and have been shocked to find very poor coverage in quite popular libraries.

# Applications in Scala (and similar languages)

## Recursion

In general recursively defined types and structures, like graphs and trees, ought to be handled with recursive functions since their stateful counterparts will be too verbose.  Everything else can use mutation.  Moreover, most functional langauges provide many helpful higher order functions to do iteration without recursion and without mutation, e.g. `foldLeft`.

## Access Level Modifiers

The motivation for access level modifiers is either to protect/encapsulate state (i.e. a mutable field/variable) or to protect/encapsulate an interface (i.e. a package or module), so allowing programmers to protect/encapsulate functions of a type only gets in the way and adds absolutely nothing.

In terms of our principles, Access Level Modifiers violate princples 2, 3 and 11.  Principle 11 is especially violated since access level modifiers force even more complicated tests, and prevent refactorings.

## Class Oriented Programming

Classes are scopes that contain functions and data.  These are strongly discouraged.  They violate the principles so badly the feature ought to be removed from all languages.  Some languages (mainly quite advanced, theoretical and functional languages) already do not have such features, like Haskall, Lisp, OCaml, Closure, Agda, Idris, Erlang, Rust, Go.

Note that Java and C# are the main Class Oriented Languages. Note that they are NOT Object Oriented Languages, this is a very common confusion.  Object Oriented Languages include Smalltalk and Erlang, though some Functional Languages, and even Procedural Languages can be used in a Object Oriented way (e.g. using Akka in Scala, Channels in Rust & Go).

Languages like Python, Ruby, C++, Scala and JavaScript, being the weird monstrosities they are, have features to do just about anything, and so you can do Class Oriented Programming in these languages.

Java and C# allow you to write functions into "static" scopes, but do not allow you to write functions into pure namespaces.

### Violation of Simplicity

Programs written using classes tend to be more verbose than their functional counterparts.  The syntax for a class is immediately more versbose than the functional counterpart, so we immediately violate 2 and 3.  Furthermore every time we wish to call a function, we have to instantiate a class with the necessary data. This violates principle 2 and 3.  In order to avoid this repetative practice, programmers go to great lengths to reuse class instances, which only makes the situation worse by violating principle 6.

Principle 6 is violated in it's own right since all the functions in the class now unnecessarily sit inside another scope.  Principle 11 is violated greatly since adding tests is harder, now we have to unnecessarily instantiate the class in order to call the functions.

One very frustrating thing about class oriented programming is that we cannot easily move functions, nor their invocations without producing a compile error.

### Alternatives

The main two problems Class Oriented programming aims (and fails) to solve are

 - Mutable state encapsulation
 - Dynamic dispatch
 
#### Functional Dynamic Dispatch Example

Suppose we wish to log events to a log server and we want to wrap this in an abstraction so that we can test locally without the log server.  The typical Class Oriented way would be as follows:

```
trait Logger {
  def log(message: String): Unit
}

class ServerLogger(logServer: LogServer) extends Logger {
  def log(message: String): Unit = logServer.log(message)
}

object Main {
  def someBusinessLogic(config: Config): Unit = ???
  
  def apply(config: Config): Unit = {
    val logger = new ServerLogger(new LogServer(address = "blar"))
    
    logger.log("Application started")
    
    // Writes to a database or filesystem or something
    someBusinessLogic(config)
    
    logger.log("Some business logic executed")
    
    // ...
  }
```

Below we present 4 options, options 1 - 3 have some issues, which help us learn what the source of the confusion/complexity is.  Option 1 is preferable but not always applicable.  When we cannot use Option 1 we should opt for Option 4.

**Option 1 - Pure Functional - Deferred Side Effects**

We could refactor this to be pure functional and defer all side effects to a big `doUnsafe` like so:

```
trait SideEffect
case class Log(message: String) extends SideEffect
case class DBWrite(key: String, value: String) extends SideEffect

object Main {
  def someBusinessLogic(config: Config): List[DBWrite] = ???
  
  def doUnsafe(sideEffects: List[SideEffect]): Unit = {
    val logger = new LogServer(address = "blar")
    sideEffects.foreach {
      case Log(message) => logger.log(message)
      case DBWrite(key: String, value: String) => database.write(key, value)
    }
  }

  def apply(config: Config): Unit = {
    doUnsafe(Log("Application started") +: someBusinessLogic() :+ Log("Some business logic executed"))
  }
```

**Caveats**

1. This only guarantees our log messages correctly straddle side effecting events and proceed errors if `someBusinessLogic` is completely deterministic and pure.  But in the real world nothing is perfectly pure since we have finite memory.  Furthermore some rather flaky library/framework can unpredictably throw errors (e.g. Spark).
2. If we need to do a read before write, this will get more complicated, we'll need to call `doUnsafe` multiple times or design a AOP system of chaining functions together.

**Option 2 - Casting Contexts**

```
trait Logger {
  def log(message: String)(implicit logContext: LogContext): Unit
}

trait LogContext
case class ServerLoggerContext(logServer: LogServer) extends LogContext

object ServerLogger extends Logger {
  def log(message: String)(implicit logContext: LogContext): Unit = toLogServer.log(message)
  
  def toLogServer(implicit logContext: LogContext): LogServer = logContext match {
    case ServerLoggerContext(logServer: LogServer) => logServer
    case other => throw new RuntimeException("Calling ServerLogger.log with incorrect context: " + other.toString)
  }
}

object Main {
  def someBusinessLogic(config: Config)(implicit logger: Logger, logContext: LogContext): Unit = ???
  
  def apply(config: Config): Unit = {
    implicit val logContext: LogContext = ServerLoggerContext(new LogServer(address = "blar"))

    ServerLogger.log("Application started")
    
    // Writes to a database or filesystem or something
    someBusinessLogic(config)
    
    ServerLogger.log("Some business logic executed")
    
    // ...
  }
```

**Misconception**

One may be concerned here that we have less compile time checking since the method `log` can now throw an exception if we get the `LogContext` wrong, whereas in the Class Oriented version if we accidentally passed something different to the `ServerLogger` constructor we would get a compile error.

Note that non of our principles deal exclusively with static typing, rather they require "the build" to triangulate the code, which can include both type systems and unit tests.  The fact that the compiler of Scala is not smart enough to tell us that an exception will occur when we pass in the wrong `LogContext` is an issue with the compiler, not the code or the principles.  If the language in question is not powerful enough to give compile errors then we can always resort to integration tests to ensure this code works.

**Option 3 - Pattern Matching Dispatch**

```
object Logger {
  def log(message: String)(implicit logContext: LogContext): Unit = logContext match {
    case ServerLoggerContext(logServer: LogServer) => logServer.log(message, logServer)
    case other => throw new RuntimeException("Calling Logger.log with incorrect context: " + other.toString)
}

trait LogContext
case class ServerLoggerContext(logServer: LogServer) extends LogContext

object Main {
  def someBusinessLogic(config: Config)(implicit logger: Logger, logContext: LogContext): Unit = ???
  
  def apply(config: Config): Unit = {
    implicit val logContext: LogContext = ServerLoggerContext(new LogServer(address = "blar"))

    Logger.log("Application started")
    
    // Writes to a database or filesystem or something
    someBusinessLogic(config)
    
    Logger.log("Some business logic executed")
    
    // ...
  }
```

**Caveat**

This is the old school way to do dynamic dispatch, i.e. a simple "switch" statement.  The problem is that we have to modify main code, namely the `Logger.log` method, if we wish to add test implementations of logging.

One way to avoid this is to add another context, the `dispatchMap: Map[LogContext, (String, LogContext) => Unit]`

**Option 4 - BEST Pattern Matching Dispatch With Explicit Dispatch Injection**

Observing the problems with the above, we see that the issue with trying to do dependency injection is that we have to use complex language features. The root cause of this problem is that we are avoiding making the dynamic dispatch an explicit part of the application, as if for some reason being clear and explicit is too simple for most developers.

So here we make the dynamic dispatch an explicit part of the application

```
object Logger {
  type Dispatcher = (String, LogContext) => Unit

  def log(message: String)
         (implicit logContext: LogContext,
          dispatcher: Dispatcher): Unit = dispatcher(message, logContext)
}

trait LogContext
case class ServerLoggerContext(logServer: LogServer) extends LogContext

object Main {
  def someBusinessLogic(config: Config)(implicit dispatcher: Dispatcher, logContext: LogContext): Unit = ???
  
  def apply(config: Config): Unit = {
    implicit val logContext: LogContext = ServerLoggerContext(new LogServer(address = "blar"))

    implicit val dispatcher = (message: String, logContext: LogContext) => logContext match {
      case ServerLoggerContext(logServer: LogServer) => logServer.log(message, logServer)
      case other => throw new RuntimeException("Calling dispatcher with incorrect context: " + other.toString)
    }

    Logger.log("Application started")
    
    // Writes to a database or filesystem or something
    someBusinessLogic(config)
    
    Logger.log("Some business logic executed")
    
    // ...
  }
```

Notes:

 - When we wish to test, we can inject different dispatchers that can call out to test implementations of log
 - We are welcome to wrap the body of each `case` statement in another method in another object if it gets too verbose, but note there is no need to enforce a common supertype on this collection of objects since we won't be passing them around.

## Variables Outside Functions

TODO examples - languages that allow variables to exist in an object or namespace are bad.

## Concrete Examples

### Classes vs Functions

**Class version**

```
class Printer(mode: String = "Colour") {
  def print(text: String): Unit
}
```

Written in a lisp like way

```
(class Printer (fields (mode String = "Colour")) (methods (def print (args (text String)) Unit)))
```

We can now simply count the symbols to get an AST length of 13, and an AST depth of 4.

To call it

```
new Printer("Black And White").print("hello world")
```

Lisp form

```
(print (new Printer ("Black And White")) "hello world")
```

which has AST length 5, and an AST depth of 3

**Functional version**

```
def print(text: String, mode: String = "Colour"): Unit
```

Lisp form:

```
(def print (args (text String) (mode String = "Colour")) Unit)
```

AST Length = 10, AST depth = 3

To call it

```
(print "hello world" "Black And White")
```

AST length = 3, AST depth = 1

#### Comparison

So the ASTs of the class version are longer and deeper, therefore they are more complicated.

#### Obstructive

If we want to move the function `print` into a different namespace, perhaps because we wish to decrease the number of import or paths we have to specify (thus decreasing the AST length of the program), we cannot do that with a simple cut and paste operation in the class oriented version.

Furthermore if we want to move the invocation, we also need to move (or often copy) the instantiation of the class.

#### Scope Complexity

TODO examples

## Single Responsability Principle

This is what happens when you take SRP to the extreme

https://www.youtube.com/watch?v=7RJmoCWx4cE

# Applications in Advanced Languages

TODO dependent-types, or 

# Other Links

https://stackoverflow.com/a/22148186/1586965
