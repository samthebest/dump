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

## Abstractions can obfuscate complexity, not always eliminate it










