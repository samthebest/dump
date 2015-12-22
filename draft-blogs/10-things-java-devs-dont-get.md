# Top 10 Struggles From OOP to FP

Not just a philosophy of FP, but a philosophy of not making production code more complicated in order to make test code easier to write

# 1. You rarely need private

# 2. You rarely need dependency injection

You inject that which do not have control over only.

There are no hidden dependencies since you have no side effects.

You don't need epic fixtures, you have a function that takes some arguments and returns some arguments

Example

def myMethod(blar) = {
  calls isTitleMatch somewhere
}

def isTitleMatch

Want to be sure isTitleMatch is being used to evaluate X = Y, rather than just ==.

SOLUTION: Generators, and deduplication.  "Full Triangulation" would mean you write code to generate your test cases.


val testCasesIsTitleMatch: Map[Params, Result]

val testCasesMyMethod: Map[Params, Result] = permutes over testCasesIsTitleMatch

"Phased Triangulation", you only use a subset of testCasesIsTitleMatch just to "give confidence" that it is being called, not to "prove" it is being called. For most projects this will suffice. You manually write out the cases.


The benefits: production code is as simple as it can be, no unnecessary complexity. Each function is as simple as it can possibly be.

Test code is also transparent and doesn't have to mock things up, nor track what is and what is not called.

Only negative is that when one component breaks, for example we break isTitleMatch, then all the tests above that level will break too. It could then be hard to see which function is broken.  This isn't so much of an issue in modern times since we get feedback pretty quick, so we would have to forget which method it was we where editing.  This might not be so clear in refactors where we change many functions together, suppose we are moving things around and the code only compiles again after 5 methods are moved - in one we made a mistake in the move.

This is indeed a problem, it's yet another example of "no free lunch theorem", or in other words "you have to put complexity somewhere".  The OOP mindset might be to put complexity in the production code to eliminate complexity in debugging times.  This bias is probably due to that debugging programs is much harder in OOP because you need so much more code to get anything done.

In FP most of your time is spent thinking and reading code because little time is needed for actually writing code, so you want the code to be as simple and easy to reason about as possible.

If we had better tooling that could analyse the stack and suggest which method is in the stack for all the newly broken tests, then we would be able to know which method broke.

"No Free Lunch Theorem" https://en.wikipedia.org/wiki/No_free_lunch_theorem applies to software development and time spent.  When we optimise for one situation, like "debugging" time by using DI, we have costs somewhere else, like in production code complexity and readability (due to increased parameters).  Each language has it's own shortcomings which mean we need to prioritise certain kinds of optimisations.  In OOP DI makes sense, in FP, not as much.  My preference is to keep production code as simple as it can possibly be since the real truth lies in the code, in the details.  Furthermore as a general principle it is best to optimise for what is obviously a problem *now*, not for what we try to predict is going to be a problem in future.


In OOP especially when mutating things, you don't have control over your own classes because you need a lot of code to do anything, and you mutate things. 


# 3. 

