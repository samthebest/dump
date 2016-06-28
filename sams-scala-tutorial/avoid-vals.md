
Before we introduce the rules, let's remember the most important rule of them all:

Never dogmatically follow any rule, all rules have exceptions (except axioms and this rule). To follow a rule without thought is missing the point of the rule and can result in "cargo cults".

## Rules Of Thumb

1. Avoid putting `val`s in `objects`
2. Avoid putting `val`s in `class` bodies
3. Avoid putting methods in `class`es
4. Only put fields in `case class`es (but no need to use `val` keyword of course)

## Overview

The justification here is to ensure a clean seperation between data and functions on data, much like in the mathematical world. This is the opposite of Object Oriented Programming that explicitly aims to mix these two concepts together, and I believe is a big source of many of the evils one finds in code bases of OOP, such as:

 - Run away complexity
 - The need to spend more than 10 seconds thinking about dependency injection
 - The need for dependency injection frameworks
 - The need for so many "design patterns"
 - Abstraction leaks
 - Unclear initialisation order of "constants" (static fields)

And more specifically to Big Data:

 - Serialisation problems
 - GC problems

The motivation for OOP (i.e. breaking these rules) is as follows:

 - Humans like to anthropocentrise things to make it easier to understand.  The real world, and especially natural langauge, is indeed object oriented so it "feels" right to use OOP.
 - OOP allows one to manage scope without resorting to boiler plate param passing, nor global mutable state.
 - OOP provides syntactic means to heavily use postfix notation, e.g. `myObject.myMethod(something)` instead of `myMethod(myObject, something)`
 - OOP provides a mechanism for protecting mutable state

Of course the first motivation is illogical and using a tool of **fuzzy** abstraction, that is natural language, to attempt to deal with programming.  The correct approach is to treat programming as a mathematical discipline since that is what it is, and that means using the abstractions of mathematics that are designed to handle rigorous and precise concepts.  Natural language abstractions are built on fuzzy rules, and hence OOP results in a mess since it's using a means of abstraction that does not work for precise domain like programming.

The second reason is genuine, indeed mixing data with functions means those functions need not have many params passed in, nor access some global mutable state. Nevertheless Scala provides two ways to combat this issue without resorting to OOP:

 - implicit parameters
 - ultra low boiler plate syntax to introduce containers for groups of parameters (i.e. case classes)

The third reason is also geniuine. Indeed using infix notation allows for greater readability, particularly because order of expression evaluation now agrees with the syntatical order of the expression. For example in `o1.method1(o2).method2(o3)` method1 will evaluate first, then method2, but in prefix notation that order is broken: `method2(method1(o1, o2), o3)`.

But again most functional languages, and Scala included, provide other mechnasims for introducing infix notation. Scala is perhaps the strangest and provides something called `implicit class`es. Other languages like Haskell, Lisp and R have more direct means to use infix style notation.

Finally the fourth reason is also genuine.  The solution in Functional Programming is to avoid mutable state, but this is impossible all the time, particularly thanks to interactions with the external world and a need to optimise things by mutating.  Thus low level optimised code, or code to help handle world interactions, are the only reasons to use OOP.  Nevertheles in all probability **you** don't need to use OOP since vast libraries already exist.  Most user/application land programming need not use OOP since it can use libraries that have done that work for them.

## 3 Avoid methods in classes

So this is the least contentious / controversial dictim of functional programming, particularly in Scala.  One learns fairly quickly (if one deals with many code bases) that code bases that avoid the OOP approach and thus only have methods in `object`s are significantly easier to refactor, test, understand and maintain than codebases that put methods in `class`es.  So we won't go into great detail about this rule.

In a nutshell, putting methods in classes *makes a decision*, particularly regarding what should be in a scope, and every time you make a decision you a) might be wrong, and b) have created future work to undo that decision should it ever be desirable to do so.  One cannot be wrong if one puts a function in an object and passes all the data into the function since no decision about scope has been made.

If you think you never make the wrong choices, and you write code that you never need to go back and shift around, you are dellusional.  Everyone makes mistakes, and change is inevitable.  Ask yourself how many code bases have you come to where every decision made was the right decision?

## 2 Avoid putting `val`s in `class` bodies

Put `val`s inside `case class`es and put them in the constructor list. If you have dependencies between `val`s introduce an `apply` method to handle initialisation.  There ought to be no reason why you can't use apply, except in cases when you are doing something non-functional (like accessing some mutable state).

`val`s inside a class body are not included in all the wonderful features given to you by `case class`es, e.g. `toString`, `copy`, equality, etc.

## 1 Avoid putting `val`s in `object`s

This is likely the most unusual rule I suggest, even for functional programmers.

# What about `Function`s in `case class` fields?

Surely this is one of the biggest advantages of functional programming - functions are first class citizens.

This is quite different to a method in a class because it doesn't have access to the fields as part of it's scope.

One of the main reasons it is unpleasant to put methods into classes is that it creates a scope dependency. This means that method cannot be "moved around" just as though it where a lego brick.  Imagine if a red lego brick when placed in certain place changed into a green lego brick - trying to build a pretty house becomes quite hard.

Nevertheless we should still use functions in case classes with caution and only use them when necessary. Common use cases are non-static type-classes, e.g. serialisation that can change at run time.

Keep such functions small and simple, and avoid putting business logic here.


