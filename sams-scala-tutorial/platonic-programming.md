## Introduction

Platonic Programming (PP) is the future.

Practically PP is quite simple to apply, but the motivating theory assumes some acedemic background.

Another name for PP could be Extreme Functional Programming, meaning Functional Programming practices taken to the extreme (like XP takes general practices to the extreme).

### Hell Is Other Peoples Code

PP is in stark contrast to SOLID, OOP and Design Patterns, which we will collectively refer to as Anthropocentric Programming.  Anthropocentric Programming is fundementally flawed and is the root of all of the following problems:

1. 

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

The following are also equivilent even though they depend on an external state.

## Definition - Call Graph

For any program `P` the **call graph** `G_P` is the graph `(V, E)` where for any `v`, `v` is in the vertex set `V` if and only if it is a function in `P`, and for any `e`, `e` is in the edge set `E` if and only if `e = (f, g)` where `f` calls `g` in the program `P`.

## Definition - Triangulation

For any function with tests `t_1, ..., t_n` and a type signature `(p_1, ...., p_n) -> r` we call the pair `((t_1, ..., t_n), (p_1, ...., p_n) -> r)` the **functions build constraints**.  The collection of all constrains for program is called the **programs build constrains**.

### Ideal Triangulation

For any program with build constrains , when the programs build constrains

Usually, in the real world, the tests plus the type signature will not be sufficient to even acheive finatary triangulation.

## Definition - Non Deterministic Triangulation

Test cases are generated randomly.

In PP these kinds of tests are forbidden unless the seed of the random generator is fixed.  This essentially collapses a single non-deterministic test into a large collection of ordinary tests.

## Objective Comparative Principles of PP

Given functionally equivilent programs

### 1. Triangulation Principle
2. We favour programs with shorter ASTs
3. We favour programs with fewer non-referentially transparent expressions

### Social principle




Not all are compatible, so those with a higher number are favoured over those with a lower number.

## Absalute Principles of PP

The call graph must be fully connected (i.e. every function is transitively called by the entry point).

## Theorem - State Monism

Given an infinitely fast processor and an infinite amount of memory, every program can be refactored to have at most 1 variable (i.e. `var` in Scala) while remaining functionally equivilant.  By the above principles we should favour these programs.

Theorem - 

Given two scopes S_1 and S_2 where S_2 is a subscope of S_1, moving a `var` from S_1 into only S_2 cannot increase the number of non-referntially transparent expressions.




2. We favour programs with fewer non-referentially transparent expressions
3. 
