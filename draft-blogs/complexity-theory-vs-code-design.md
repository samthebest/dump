# Complexity Theory as a means to formalize code quality and design

A lot of time spent in writing code in a proffesional environment is on arguing about code style, quality and design.  This is especially true for those who are particularly passionate about their job and producing the best work possible.  The more passionate the proffesional, the more arguing they will do.

Different languages, programming paradigms and disciplines have different notions of what is good code and good code design.  Unfortunately the majority of these notions have become dogmatic rituals applied with little thought beyond "it's the convention or best practice".  Conventions and best practices lower project ramp up time for new comers and facilitate implicit communication between colleagues.  We can think of best practices and conventions as meta-level algorithms or optimizations that optimize against time or pain for various situations.  Best practices cut time in debugging code, understanding code, understanding tests, runtime, build times, etc.

## No Free Lunch Theorem

The issue with meta-level algorithms is that none exist that similtaneously optimize for all of the concerns of coding.  This is a well known result in Machine Learning for https://en.wikipedia.org/wiki/No_free_lunch_theorem.

## A Good Overaching Heuristic

Length of AST, i.e. count the length of chunk of code where each symbol in the language is considered to have a length of 1, so a method name has a length of 1.  Examples:

def myMethod(fred: Int): Int = fred + foo(fred)
d m(f:I):I=f+g(f)

injection is bad
d m(f:I,g:I-I):I=f+g(f)

## Cognitive Load

The length of an AST doesn't correspond to difficulty in understanding linearly.  To the compiler it nearly does work this way for a lot of languages since a lot of programming languages are close to "deterministic context-free grammars", but for humans it does not.  If the language is close to CFG, then compiling will be close to polynomial time. This is because humans can understand symbolic information that is far from DCFGs, or even CFGs, through imagary, sound, iconagraphy, natural language, etc.  Languages that are not CFG in general take exponential time to parse.

Therefore when a human looks at code, although it formally is quite simple, a human may having much higher cognitive load than necessary since in parsing the code it may be reusing the same exponential time internal parser for the code.

It's not surpising then that those languages that are the most verbose, due to lack of features or bad design, are ones that seem to have the greatest of "religous following" of patterns, practices and styles.  In more powerful langauges it seems the proffesionals care less about such practices.  The best example of verbosity causing dogmatic convention is Java.  Java is so verbose that great effort is required to keep it understandable.

## Abstractions can obfuscate complexity, not always eliminate it

One "trick" used a lot in Java is to chop up a 20 line method into many smaller methods, sometimes just 2 or 3 lines long.  This shows a complete failure of the language, and does not result in code that is easier to understand.  By chopping the information up it does have an effect on cognitive load since the sum of exponents of numbers summing to N will always be smaller than the exponent of N.  But this is an utter failure on the language which then either places greater pressure on other mental resources, particularly memory, or great pressure on trust and naming.  To read Java you need a very good short term memory in order to wade through the call hearchy, or a lot of trust in method names.

Reall the method it acheives this is also fake, in reality complexity has been increased, but the reader is prevented from viewing all of it at once by placing it in different locations.  The same ends albeit more crudely is acheived in other languages with whitespace or comments like //---------.   One sees this kind of thing in scientific code.

Early abstraction obfuscates the possibility of refactoring.

So what should we be doing instead? We should be trying to keep AST length down to an absalute minimum, including obfuscation. This means abstractions, like pulling apart one method into many can only be justified if it can result in deuplication.  Abstraction a method which is only used once should only ever be done when:

1. The 
2. 


Example:

Suppose method X has as a subroutine checks for an even number in a list

val list = ???
var foundEven: Boolean = false
var index: Int = 0
while (!foundEven && index < list.length) {
  val nextNumber = list(index)
  if (nextNumber % 2 == 0) {
    foundEven = true
  } else {
    foundEven = false
  }
  index = index + 1
}



This example is an exhageration, but you will often see the first course of action is start abstractions.  E.g.


def evenAtIndex(list: List[Int], index: Int): Boolean = {
  val numberAtIndex = list(index)
  if (numberAtIndex % 2 == 0) {
    return true
  } else {
    return false
  }
}

def hasEven(list: List[Int]): Boolean = {
  var foundEven: Boolean = false
  var index: Int = 0
  while (!foundEven && index < list.length) {
    foundEven = evenAtIndex(list, index)
    index = index + 1
  }
}


Using the Scala collections library we would just do:

list.exists(_ % 2 == 0)



