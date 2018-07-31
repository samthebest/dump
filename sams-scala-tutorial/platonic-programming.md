# Introduction

Platonic Programming (PP) is the future.  An alternative name for PP could be "Complexity Minimising Programming".

Practically PP is quite simple to apply, but the motivating theory assumes ~some~ a lot of acedemic background particularly within computation, complexity and information theory.

Scala programmers can feel free to skip to the final section where we give some simple to follow rules that are derived from the theoretical acedemic sections.

I have placed the "Princples of PP" section above the motivating, theoretical and defining sections.  Therefore it is highly recommended you read this section last (but I put it first for easier reference, once you have read the other sections).

## Hell Is Other Peoples Code

PP is in stark contrast to SOLID, OOP and Design Patterns, which we will collectively refer to as Anthropocentric Principles or Anthropocentric Programming (or just AP). AP currently makes up the vast majority of code, and the vast majority of programmers follow these principles by faith and fashion.  We argue AP is fundementally flawed and is the root of all of the following problems:

1. High bug density
2. Code that is difficult understand, refactor and test
3. Code that is difficult to add new features to
4. Verbose code
5. Unhappy programmers, late delivery and the **end of the world**.  

No seriously, due to compounding super-exponential technology advancments human programming incomptentency could be the main reason we don't yet live in a utopia.  This is what happens when you optimise according to AP https://github.com/EnterpriseQualityCoding/FizzBuzzEnterpriseEdition, or this https://www.youtube.com/watch?v=7RJmoCWx4cE.  If this is how insane Fizz Buzz (or Hello World) can get imagine how terrifying it can get for a non-trivial program.  [Soylent Green](https://en.wikipedia.org/wiki/Soylent_Green) is just round the corner unless we stop this!

The justification is as follows:

 - SOLID & Design Patterns are not formally defined, nor is it even possible in principle to formally define them (with the exception of Liskov Substitution).  They are inherently subjective.
 - SOLID & Design Patterns are at best Social Sciences, yet try to apply to what is really Mathematics.  No other branch of mathematics has ever had some set of anthropocentric and social principles imposed upon it, to do so would be absurd.
 - Even if we consider AP as a Social Science, for a theory of Social Science to be a success it will require vast amounts of experiments and statistical work.  AP has no such evidence base; very few studies exist.

## Quick History of Languages

The irony is that some languages make AP difficult, to nearly impossible.  For example Lisp, Erlang, Haskell and OCaml are founded on Functional Programming, which manages to eliminate the vast majority of the problems AP faces.  Ironically these languages are not that new, in fact Lisp is one of the oldest languages there is.

Some modern attempts have been made to mitigate the hell we find ourselves in.  For example Rust and Go have made attempts to standardise approaches to programming that removes the subjectivity, verbosity and complexity.

The worst languages ever invented and the main source of hell, unfortunately are probably the most popular, namely Java and C#.  The worst decisions these two languages both made are:

 - Every function has to be arbitrarily tied to a containing scope
 - Class Oriented Programming, by design, forces a complex coupling of many functions
 - Access level modifiers on functions within types.  This is really dumb since the motivation for access level modifiers is either to protect/encapsulate state (i.e. a mutable field/variable) or to protect/encapsulate an interface (i.e. a package or module), so allowing programmers to protect/encapsulate functions of a type only gets in the way and adds absalutely nothing.
 
This is perhaps why Ruby, and especially Python, is becoming increasingly popular since they allow writing functions without containing scopes.

Observe how these catestrophically stupid language design decisions only occur in basically 2 languages out of dozens of others, yet these two languages are the most popular. Anyway, let's move on.

# Theory, Definitions and Theorems

## Central Tenet of PP

There does not exist an objectively perfect (non-trivial) program, but for any pair of programs we can compare them objectively.

Therefore an objective of subjects is to refactor programs so that we make objective improvements.

## Definition - Equivalent

We say program `P_1` and `P_2` are equivilant if for any input `I`, `P_1(I) = P_2(I)` when the two programs are run in identical universes (i.e. the computer and external states are identical).

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

are also equivilent even though they depend on an external state so may not always produce identical results in practice.

## Definition - Call Graph

For any program `P` the **call graph** `G_P` is the graph `(V, E)` where for any `v`, `v` is in the vertex set `V` if and only if it is a function in `P`, and for any `e`, `e` is in the edge set `E` if and only if `e = (f, g)` where `f` calls `g` in the program `P`.

So a **leaf function** is a function that calls no other function.  The **height** of a function `f`, written `H(f)`, is defined recursively as follows:

 - For any **leaf functions** `f`, `H(f) = 0`
 - For any function `f` that calls `g_1, ..., g_n`, then `H(f) = max_i(H(g_i)) + 1`

**Note:** We do not include functions native to a language in the call graph.  How we consider functions from an external library will be addressed later.

## Definition - Abstract Syntax Tree and Basic Complexity

The AST of a function `f` is the function written in reverse polish notation (or equivalently Lisp form).  The **Basic Complexity** of a function is the length of the AST, where each symbol has length 1 (equivalently the length of the Lisp form not counting the parens).

E.g. the polish notation form of the expression `l.map(f)` is `(map l f)`, so it's Basic Complexity is 3.

## Definition - Kolmogorov Complexity

The Kolmogorov Complexity (KC) of a function `f` is the length of the shortest program (in some chosen language L) that outputs the AST of the function.  In practice Kolmogorov Complexity must be intuited, not computed, since (a) the choice of language L is arbitrary, (b) KC is not itself a computable function.

E.g. We intuitively can imagine that the shortest program that outputs the expression `1 + 2 + 3 + 4 + 5 + 6` is shorter than the shortest program that outputs the expression `1 + 2 - 3 + 4 - 5 - 6`.

## Definition - Triangulation

For any function with tests `t_1, ..., t_n` and a type signature `(p_1, ...., p_n) -> r` we call the pair `((t_1, ..., t_n), (p_1, ...., p_n) -> r)` the **functions build constraints**.  The collection of all constrains for program is called the **programs build constrains**.

### Finitary Triangulation

For any function `f` with build constrains `B_f`, let `F` be the set of functions that satisfy `B_f`.  If we partition `F` into equivalent classes (i.e. subsets of `F` where for each subset every function is equivalent) we say `f` is **finitarily triangulated** if this parition is finite. Or we say it is **`N`-Triangulated** where `N` is the cardinality of the partition.

### Ideal Triangulation

This is when a function `f` is `1`-Triangulated.  In other words, every function that satisfies the build constraints is equivalent to `f`.

Note that for any function that is `N`-Triangulated, it is possible to add only `N - 1` tests to make it ideally triangulated.

Usually, in the real world, the tests plus the type signature will not be sufficient to even achieve finatary triangulation. So we define the following

### Complexity Augmented Triangulation

A function `f` is **AST Traingulated** or **A-Triangulated** if and only if, for any function `g` if `g` is not equivalent to `f` but does satisfy the build constraints then `g` has strictly greater Basic Complexity than `f`.

A function `f` is **Kolmogorov Traingulated** or **K-Triangulated** if and only if, for any function `g` if `g` is not equivalent to `f` but does satisfy the build constraints then `g` has strictly greater Kolmogorov Complexity than `f`.

In other words, a function is A or K Triangulated if all non-equivalent functions that build are more complicated.

In practice one will need to intuit whether or not a function is A or K Triangulated.  Usually a function written with perfect TDD will be.  This does not mean the concept is subjective, it just means it is not computable.  In the vast majority of cases one ought to be able to provide a short argument that proves or disproves the triangulation of a function.

### Non Deterministic Triangulation

Some testing frameworks will randomly generate test cases in order to pseudo-rigorously test generic properties on a function (that may be difficult to constrain with the type system alone).

In PP these kinds of tests are forbidden unless the seed of the random generator is fixed.  This essentially collapses a single non-deterministic test into a large collection of ordinary tests.  Strictly speaking then, these kinds of "Property Based Tests" add little, but in practice such tests can be useful to retrospectively add to a codebase with few tests.

## Theorem - Triangulation Strength

1-trangulation is stronger than N-trangulation, which is stronger than K-Triangulation, which is stronger than A-Triangulation, which is stronger than No triangulation

### Proof

Exercise.

## Theorem - Triangulation Transitivity

If a function `f` is X-Triangulated, and `f` calls `g`, then `g` is also X-Triangulated.  Note that the converse need not be true.

### Proof

Exercise (observe how lower level functions tend to have better test coverage than high level functions).

## Definition - Depth Triangulation Table

A programs **depth triangulation table** is the maximum level of triangulation (i.e. `(1, N, A, K, No)`) for functions of each Height.

For example suppose we have a simple program with only 2 functions `f` and `g`, and `f` calls `g`.  Suppose `g` is 1-Triangulated and `f` is A-Triangulated, then we could write the table as follows: `((0, 1), (1, A))`.

Observe that only certain kinds of tables are possible thanks to the Triangulation Transitivity Theorem.  

Furthermore we have an implicit ordering of the strength on tables of the same length, e.g. `((0, 1), (1, A))` is stronger than `((0, N), (1, A))`, and `((0, N), (1, A))` is stronger than `((0, N), (1, No))`.

Note that the 0-height functions are more important for triangulation than the 1-height functions and so on.  This is because we want to triangulate the program in small chunks, and only when all the small chunks have been properly triangulated should we consider triangulated the composition of such chunks.

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

Given an infinitely fast processor and an infinite amount of memory, every program can be refactored to have at most 1 variable (i.e. `var` in Scala) while remaining functionally equivilant.  Furthermore this `var` need only occur in the entry point of the application within the lambda of an infinite stream of inputs.  This means only one function, the entry point, mutates anything, while all other functions are pure.

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

Given an reasonably fast processor and amount of memory, every program can be refactored to have at most 1 variable (i.e. `var` in Scala) while remaining functionally equivilant.  Furthermore this `var` need only occur in a "read-write chain" using interpreters, in the entry point of the application within the lambda of an infinite stream of inputs.  

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

Note there will exist many equivilant formulations where the order of reading, writing, producing new instructions and locking is slightly different.  Now since we only lock parts of the database, and we only read parts of it, this model can be scalled to any application.

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

This is exaclty what we see in industry.  Programmers only exposed to OOP, like Java and C#, take only a year or two to converge upon the same style and happily nod their heads in agreement for the next 20 years.  What is really horrifying is that when these kinds of developers are left to refactor code for long periods of time, they tend to make the programs *longer* and *more complicated* in order to maximise according to their insane principles.

This being the jovial example https://github.com/EnterpriseQualityCoding/FizzBuzzEnterpriseEdition

# Principles of PP

We give the principles only as Comparative Principles since in practice refactoring and improving code should happen in a sequence of small iterations (pull requests). 

Given functionally equivilent programs we ought to use the following principles to choose one over the other, where the principles given earlier take higher priority over those given later

## 1. Triangulation Principle

The program with the strongest Depth Triangulation Table should be preferred.

This principle has the highest priority since correctness should always take preference over anything else.

## 2. Kolmogorov Complexity Principle

The program (not including it's tests) with the lowest Kolmogorov Complexity should be preferred.

This principle is second, since simplicity in it's most formal definition is second to correctness.

## 3. Basic Complexity Principle

The program (not including it's tests) with the lowest Basic Complexity (AST length) should be preferred.

When comparing Kolmogorov Complexity is hard, we can use this.

## 4. Call Complexity Principle

The program (not including it's tests) with the lowest Call Complexity should be preferred.

The justification why this sits above state complexity has it's own section in "Long Justifications"

## 5. State Complexity Principle

The program (not including it's tests) with the lowest State Complexity should be preferred.

## 6. External Inherited Complexity Principle

The program (not including it's tests) with the lowest External Inherited Complexity should be preferred.

## 7. Internal Inherited Complexity Principle

The program (not including it's tests) with the lowest Internal Inherited Complexity should be preferred.

## 8. Runtime (or Algorthimic) Complexity Principle

Finally, faster programs, or programs that use less memory, should be preferred. Please see Long Justifications.

## 9. Test Complexity Principle

This principles 1 - 7 but applied to the tests.

Note that test complexity comes way way after everything else.  Please see Long Justifications.

## Long Justifications

### Call vs State Complexity

The call complexity principle is considered more important that the state complexity principle.  This is because objectively speaking calling a function is actually implicitly mutating a hidden variable; the stack.  Reasoning about a stack is itself a difficult thing to do, especially in the case of recursion since this makes our stack unbounded.

In practice this could mean refactoring a recursive function to be an iterative function that mutates a variable (provided principles 1 to 3 are unchanged).  To many functional programmers this seems counter intuitive, but in Platonic Programming we are interested in the mathematical structure of the entire application within all contexts.  Nevertheless principles 1 - 3 still sit higher than Call Complexity, which means when a recursive function is syntactically obviously simpler than it's *only* iterative counterpart, then we should still pick the recursive function.

Please see Rules of Thumb section.

### Test Complexity Comes Last

This means we should not make our program more complicated to make it easier to test, the classical example being adding a lot of dependency and function injection.  This means we need to be more intelligent about how to organise and generate tests.  In particular we ought to consider test generators (i.e. code that generates test cases, rather than listing all the cases out manually).

Also note that the way we have defined triangulation starts from the bottom up.  Combine this with low call complexity and we can hope that an application is defined in terms of a few layers, where the bottom layer is much wider than the others.  The higher layers ought to be simple compositions of lower level functions, and so they don't need much testing in order to become A-triangulated. This is because a higher level function just composing a bunch of lower level functions is already syntactically very simple.

### Runtime Complexity is low rank

Note that we are comparing functionally equivalent programs.  So if one program takes the length of the universe to run, while another runs in 1 second, these are NOT functionally equivalent.

In large enterprise organisations, they have this archiac notion of "non-functional requirements".  This is a massive abuse of language.  If a requirement, say to run in less than 10 minutes, is not a functional requirement then this implies if we dropped this requirement the program would still be "functional".  This is obviously stupid, but unforuntately many very stupid people work in large enterprise organisations as it's the only place they can hide.

# Exceptions and Serious Application of PP

Programming is in the real world, and every rule that tries to apply to the real world will have exceptions.  Therefore we still don't condone PP as an absalute system.  We do consider PP as a huge improvement since it is at least formally defined.  This means when programmers argue about an approach they at least can start from somewhere where they have to agree.

Currently, no two programmers that have an impressive set of diverse experiences will agree on anything, even definitions.  This is the major success of PP, is that it at least adds some formal definitions we can start with.

## Large Jumps in Lower Ranked Principles

Sometimes we can make a very small gain in, say princple 1, say by adding a call to an external library and removing our own implementation, but this could have catestrophic consequences to principle 6.  I.e. the implementation provided by the external library could itself be terrible.

# Rules of Thumb in Scala


In general what this usually means as that recursively defined types and structures, like graphs and trees, ought to be handled with recursive functions since their stateful counterparts will be too verbose.  Everything else can use mutation.  Moreover, most functional langauges provide many helpful higher order functions to do iteration without recursion and without mutation, e.g. `foldLeft`.

## Dead code is worse than you think!

Note that adding dead code can have a significant negative impact on triangulation (not just call & syntactic complexity).  Suppose we have two functions

```
def f(x: Any) = ...

// dead code, not called anywhere
def fLiar(x: Any) = ...
```

and a higher level function `g` calls `f`.  Suppose `fLiar` does something very similar to `f` but disagrees for just a few inputs.  This means to trangulate `g` we now need to find test cases that exclude `fLiar`, which could be very hard.

### Other Links

https://stackoverflow.com/a/22148186/1586965
