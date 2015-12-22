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



In OOP especially when mutating things, you don't have control over your own classes because you need a lot of code to do anything, and you mutate things. 


# 3. 

