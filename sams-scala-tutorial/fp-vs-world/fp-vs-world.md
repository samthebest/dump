

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
 - Databases
 - Efficiency
 - Compiler speed
 - Education

We will sometimes use examples from Scala, simply because this is a popular 

## Slide 3:

Misconception # 1

FP is about eliminating all mutation of state - WRONG!

 - It's about moving World Interactions to the top of the call hierarchy, and hidding it into frameworks
 - It's about restricting scope of low level efficiency mutations, and hiding it in libraries
 - 99.999% of business logic never needs to mutate

## Slide 4:

DIAGRAM

## Slide 5

Misconception # 2


 - 2. FP does not require a Functional Language, and having a Functional Language doesn't mean you are doing FP


Functional Languages provide syntax to make functional programming easier, that is all, the programmer still needs to engage with the concepts.





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



including databases, state, efficiency, compiler speed, education, OOP domination and general misconceptions.

