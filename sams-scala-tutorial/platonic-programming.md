## Introduction

Platonic Programming (PP) is the future.

Practically PP is quite simple to apply, but the motivating theory assumes some acedemic background.

Another name for PP could be Extreme Functional Programming, meaning Functional Programming practices taken to the extreme (like XP takes general practices to the extreme).

### Hell Is Other Peoples Code

PP is in stark contrast to SOLID, OOP and Design Patterns, which we will collectively refer to as Anthropocentric Principles or Anthropocentric Programming (or just AP). AP currently makes up the vast majority of code, and the vast majority of programmers follow these principles by faith and fashion.  We argue AP is fundementally flawed and is the root of all of the following problems:

1. High bug density
2. Code that is difficult understand, refactor and test
3. Code that is difficult to add new features to
4. Verbose code
5. Unhappy programmers, late delivery and the **end of the world**.  No seriously, due to compounding super-exponential technology advancments human programming incomptentency could be the main reason we don't yet live in a utopia.

The justification is as follows:

 - SOLID & Design Patterns are not formally defined, nor is it even possible in principle to formally define them (with the exception of Liskov Substitution).  They are inherently subjective.
 - SOLID & Design Patterns are at best Social Sciences, yet try to apply to what is really Mathematics.  No other branch of mathematics has ever had some set of anthropocentric and social principles imposed upon it, to do so would be absurd.
 - Even if we consider AP as a Social Science, for a theory of Social Science to be a success it will require vast amounts of experiments and statistical work.  AP has no such evidence base; very few studies exist.

### Quick History of Languages

The irony is that some languages make AP difficult, to nearly impossible.  For example Lisp, Erlang, Haskell and OCaml are founded on Functional Programming, which manages to eliminate the vast majority of the problems AP faces.  Ironically these languages are not that new, in fact Lisp is one of the oldest languages there is.

Some modern attempts have been made to mitigate the hell we find ourselves in.  For example Rust and Go have made attempts to standardise approaches to programming that removes the subjectivity, verbosity and complexity.

The worst languages ever invented and the main source of hell, unfortunately are probably the most popular, namely Java and C#.  The single worst decisions these two languages both made is:

 - Every function has to be arbitrarily tied to a containing scope
 - Class Oriented Programming, by design, forces a complex coupling of many functions
 
This is perhaps why Ruby, and especially Python, is becoming increasingly popular since they allow writing functions without containing scopes.

Observe how these catestrophically stupid language design decisions only occur in basically 2 languages out of dozens of others, yet these two languages are the most popular. Anyway, let's move on.

## Central Tenet of PP

There does not exist an objectively perfect (non-trivial) program, but for any pair of programs we can compare them objectively.

Therefore an objective of subjects is to refactor programs so that we make objective improvements.

## Definition - Equivilant

We say program `P_1` and `P_2` are equivilant if for any input `I`, `P_1(I) = P_2(I)` when the two programs are run in identical universes (i.e. the computer and external states are identical).

## Examples - Equivilant

Below, `f` and `g` are equivilent.

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

## Definition - Triangulation

For any function with tests `t_1, ..., t_n` and a type signature `(p_1, ...., p_n) -> r` we call the pair `((t_1, ..., t_n), (p_1, ...., p_n) -> r)` the **functions build constraints**.  The collection of all constrains for program is called the **programs build constrains**.

### Ideal Triangulation

For any program with build constrains , when the programs build constrains

Usually, in the real world, the tests plus the type signature will not be sufficient to even acheive finatary triangulation.

## Definition - Non Deterministic Triangulation

Test cases are generated randomly.

In PP these kinds of tests are forbidden unless the seed of the random generator is fixed.  This essentially collapses a single non-deterministic test into a large collection of ordinary tests.

## Definition - Depth Triangulation Number

Is

## Objective Comparative Principles of PP

Given functionally equivilent programs

### 1. Triangulation Principle
2. We favour programs with shorter ASTs
3. We favour programs with fewer non-referentially transparent expressions
4. We favour programs with 

### Social principle

### Inherited Complexity Principle


Not all are compatible, so those with a higher number are favoured over those with a lower number.

## Absalute Principles of PP

The call graph must be fully connected (i.e. every function is transitively called by the entry point).

## Theorem - State Monism

Given an infinitely fast processor and an infinite amount of memory, every program can be refactored to have at most 1 variable (i.e. `var` in Scala) while remaining functionally equivilant, and this `var` need only occur in the entry point of the application.  This means only one function, the entry point, mutates anything, while all other functions are pure.  By the transparency principle we should favour these programs.

**Note:** Most modern functional languages provide many native functions that hide away `var`s and most modern computers are very powerful, therefore most modern programs should respect State Monism.

Theorem - 

Given two scopes S_1 and S_2 where S_2 is a subscope of S_1, moving a `var` from S_1 into only S_2 cannot increase the number of non-referntially transparent expressions.




2. We favour programs with fewer non-referentially transparent expressions
3. 


### Other Links

https://stackoverflow.com/a/22148186/1586965
