# DRAFT

## Slide 1:

 - Lisp, 1958, first Functional Language
 - Simula 67, 1968, first Object Oriented Language, TEN years later
 - 2016, OOP & Procedural still dominates
 - Why?

## Slide 2:

FP can solve the majority of programming problems better than OOP and Procedural/Imperative. So why isn't the majority of code written functionally?

Talk will include:

 - Misconceptions
 - Linguistic Fundementals
 - The Real World: Databases, Efficiency, Compiler speed
 - Education

We will sometimes use examples from Scala, simply because this is a popular 

## Slide 3:

Misconception # 1 - State Elimination Misconcption

FP is about eliminating *all* mutation of state - WRONG!

 - It's about moving World Interactions to the top of the call hierarchy, and hidding it into frameworks
 - It's about restricting scope of low level efficiency mutations, and hiding it in libraries
 - FP is about eliminating all mutation of state in *business logic*

## Slide 4:

DIAGRAM

## Slide 5

Misconception # 2 - The Syntax Misconception

Doing Functional Programming if and only if Using Functional Language - WRONG, in both directions

 - Functional *Languages* provide syntax that make FP considerably less verbose
 - In principle FP is possible in nearly any language
 - High order functions, and lambda expressions, are syntactic sugar, not core concepts


2 Toy examples, one where Spray just returns (very simple since many frameworks like this exist). Second where we have a toy state monad for database.

E.g. in Spray, we provide a function to a Route, could also combine with a "State" Monad type thingy, e.g.


```
// Completely pure function
def businessLogic(s: String)(w: World): World

// Provided by a framework, either internal or open source
val state: DatabaseStateMonad = ...

// State mutating

// Pseudo Spray Code
path("add/user")((s: String) => databaseMonad.map(businessLogic(s)).unsafePerform)

mutateDatabase(businessLogic(s)))
```

observe you can still do this kind of thing in OOP

## Slide 6

So what is it all about?

The Lego Axioms!

1. Lexical closures should be restricted to a functions scope
2. Purity

Or roughly speaking, expressions and function definitions can be moved around a code base without changing what they do.

A yellow brick is always a yellow brick, no matter where you put it.

A cog is always a cog, no matter where you put it.

(note referential transparency is omitted since most formal linguistic definitions define it in terms of purity)

## Slide 7

 - Axioms 2 & 3 are well known
 - Axiom 1 is not
 - Axiom 1 is broken by OOP, over and over

## Slide 8

OOP Breaks Axiom 1:

Having methods inside classes means their scope closes over the scope of the class. This means you cannot move a function between two classes and expect it to do the same thing - usually, it simply will not compile, and in dynamically typed languages it may end up doing something very different.

Using inheritance heiarchies to implement polymorphism again means functions now depend on their enclosing scope.

## Slide 9

All complexity and all immutable unmaintainable code bases can be attributed to Lego Axiom ignorance. Especially the first.

 - If we can move code around, refacotring and rearcetecting is easier
 - Pure functions are easier to test
 - Restricted closures are easier to test - no need for dependency injection

OOP makes changing code much harder, their solution is

DESIGN PATTERNS, DI, decoupling, MVC, encapsulation, and millions and millions of people writing millions and millions of lines of code, frameworks, libraries.

## Slide 10

If OOP sucks so bad, why is it so popular? 

Two reasons

1. Because we needed it, but we don't need it anymore.

 - OOP is indeed the best way to encapsulate state
 - State is very important for low level efficiency or when memory isn't cheap

We cannot jump straight to diggers, we had to invent a shovel first. With the shovel, we built digger factories.

(could have an image of a shovel, digger factory (show first ever digger) and digger, with an arrow pointing between the factory and the digger and call that "now")

2. Infix notation

## Slide 11

How to move forwards?

 - Need more mathematicians
 - Better education - it can seem harder to use a digger, but if we don't learn we will never build skyscrappers.


...

...

...

including databases, state, efficiency, compiler speed, education, OOP domination and general misconceptions.

