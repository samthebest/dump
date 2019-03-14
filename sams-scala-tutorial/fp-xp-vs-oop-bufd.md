
 - Show of hands of knowledge
 - Intro to FP (take from other slides)
 - FP is to programming as XP/agile is to process, OOP is to programming as BUFD/waterfall is to process
 - Historical root of OOP 
   - encapsulating state
     - no longer necessary (memory & cpu is cheap)
   - Dynamic dispatch via inheritance polymorphism
     - Still useful in some situations to avoid type-param bloat caused by only using type-classes
     - particularly true when dealing with AOP type problems (e.g. logging).  THe FP way to solve this is to wrap all business logic in Monads & ADTs and defer side effects to a single ADT interpreter.
 - Design patterns exist to cope with the OOP being so bad, but fail. E.g.
   - Factory patterns -> currying and returning functions
   - Decorators on streams -> lazily evaluated iterators
   - Visitor pattern -> pattern matching
   - Various ways to do dynamic dispatch -> pattern matching
   - command pattern -> functions as first class
   - https://blog.jooq.org/2016/07/04/how-functional-programming-will-finally-do-away-with-the-gof-patterns/
   - https://stackoverflow.com/questions/327955/does-functional-programming-replace-gof-design-patterns
   
 - SOLID subjevtivity
 - SOLID is a lot about decoupling, in FP you decouple via messages/case-classes/structs, in OOP you decouple via interfaces
 
 - "FP is the paradigm of the data" - Franco Bullgarelli
 
 - Enterprise FizzBuzz & Refuctoring

SOLID Subjectivity

Only LS is objective

SRP is obviously subjective

Open-Closed just means you can't modify an library, so this isn't really a principle of OOP it's a principle of build tools/IDEs

Interesting: https://stackoverflow.com/questions/657987/open-closed-principle-and-java-final-modifier


Dependency inversion:

> As you have seen in the example project, you only need to consequently apply the Open/Closed and the Liskov Substitution principles to your code base. After you have done that, your classes also comply with the Dependency Inversion Principle. 

Interface Segregation:

Basically means don't extend virtual interfaces where you do not need to implement all the methods. 
