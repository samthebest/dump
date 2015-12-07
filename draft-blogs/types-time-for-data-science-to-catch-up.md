
Alternative title: Why Build Tools Block Data Science From Going Typed,
create a github called sbt-build-files, format:

find-project.sh
example-name/
/README.md
/build.sbt
/project/plugins.sbt


# Types - Time for Data Science to Catch Up

Recently a front end / UX developer showed me his code and I was shocked to see beautifully typed and even functional looking code.  He wasn't showing me Javascript though, he was showing me TypeScript, which is the language Angular 2 is written in. It's basically a statically typed version of Javascript and seems to be gaining rapid adoption. Now even front end developers are using statically typed languages, something I didn't think would ever happen.

This raises a big concern I have with the Data Science industry, Data Science is perhaps the last code-heavy practice that is still dominated by dynamically typed languages.  The often mathematical nature of Data Science makes this doubly shocking; if anything, Data Science should have be the first industry to fall in love with compiled statically typed languages, not the last. 

Let's try to understand why this is, and hopefully explain why Data Science needs to catch up. Though explaining static typing to someone who isn't used to it can feel like explaining red to a blind person, and perhaps this is part of the problem.

# Why, In a Nutshell

"You can write literally 100s of lines with [Scala collections] or Spark [RDDs with the Scala API] then once all the type errors are fixed the chances are very very high that your program will run. If you don't have the static types that is of course not true, then essentially you run your program and debugging starts and you say *why did I get this weird thing!*" - Martin Odersky https://youtu.be/NW5h8d_ZyOs?t=18m40s

# So Why Not?

## High Level Libraries?

Python / R developers require very high level libraries that make a lot of decisions about the way a machine learning algorithm will run.  Many argue this is why Python and R are great Data Science languages; because over the years many high-level libraries have been written, i.e. if you want high-level libraries, choose Python or R.  I would argue that the converse is also true, that if you want to use Python or R you *must have* high-level libraries that do most of the job for you because writting the lower levels of code is just so hard in these languages.  

So we have a codependent relationship between the two that has resulted in people forgetting that what they want is the end result.  A Data Scientist doesn't *want* high-level libraries, rather they *want* to get their job done as painlessly as possible.  High-level libraries have become synonymous with "getting a job done painlessly", but this is only because Data Scientists are working with a language that makes everything painful.  

The analogy is that Bill works in an office with upturned nails sticking out of the ground, and the office provides Bill with metal shoes.  Bill learns a "cargo cult" association of metal shoes with a lack of pain.  Alice invites Bill to work in another office with carpets, Bill asks "does your office provide metal shoes?" She responds "no, we have no need because ...", Bill interupts "Bah! that's crazy it must be so painful to work in your office, now leave me alone."

Now high level libraries should not be confused with less code.  Alternatives to dynamic langauges like Scala (or F# for the Windows users out there) result in amazingly concise code, in fact so terse some even think it makes readability hard like in mathematical symbolism.  The brevity comes from the composability and power of the fundemental tools rather than a high level method that hides huge amounts of complex procedural code.  

Powerful compsability results in a rather nice feature when it comes to mathematical programming or modelling: when you want to tweak and tune a model, you don't tune some parameters that encapsulate someone elses *interpretation of behaviour* you edit an *unambiguous direct representation* of that behaviour.

So "NO", high level libraries doesn't provide justification.

## History?

If static types are so great, why did dynamically typed languages come about?  The issue with statically typed languages is that the compiler needs to be significantly more complex, and any short cuts taken in the compiler design result in the language user having to introduce a lot of "Boiler plate" to assist the compiler in it's type checks.  More powerful type systems either require the user to do a lot of work to make their program compile, or the compiler has to do a lot more work to implicitly fill in gaps of reasoning.  Martin Odersky points out that overly powerful type-systems, like coq, "require a Phd to use it".

The solution with Scala is to find a comprimise, in particular it offers type inference, implicit conversions and consequently smooth ways to do ad-hoc polymorphism.  This pretty much eliminates most redundant boiler plate code while giving a tolerably fast compiler (I say tolerable because it's not quite as fast as some people would like). In fact heavy use of implicits can slow down your IDE even with quad-core i7s, so I imagine 5 - 10 years ago implicits would have made a language unusable.  Perhaps when Ruby, R, JavaScript and Python where invented the effort and speed of computers would prohibit such approaches - consequently in the quest for eliminating boiler plate, language inventers chose dynamic typing.  

So "maybe" history and processing power is partly to blame for the existence of dynamic programming, but it doesn't quite fully explain it.

## Barrier to Entry?

The biggest complaint people have about Scala, and it's certainly mine, is getting an IDE, SBT and my libraries set up.  Sorting out a proper build file for a Scala project with multiple dependencies is a multi-day ticket in JIRA even for an experienced Scala/Java developer.  Now try asking someone completely unfamiliar to Scala (and Java) and completely oblivious of it's benifits to spend potentially a week figureing out how to get their laptop & production environment into a position where they can deploy a single line of code.  

It's not an easy sell and I concede I don't have a counter argument other than "please just try to push through that barrier, *trust me* the other side is worth it".

So "yes", this is probably the main reason why adoption slow.




...

Another post: An Open Letter to The Creators of SBT, Scala, Spark and Intellij

Type-class derivation (serialization is still a pain), spores, "staging" (typed-trees), GUI for SBT



In fact heavy use of implicits in Scala does cause noticable lag in ones IDE, and many second, even many minute, compile times.


Maths is extremly typed, and extremly functional, and Machine Learning is just a rather straight forward application of mathematics & computer science.

Rust

TypeScript
Even Natural Language is typed, if I say "I don't want much apples" this is grammatically incorrect because the type of "apples" is discrete and so we should really say "I don't want many apples". Whether it's dynamic or static is a debatable since natural language is not "run". Nevertheless word processors can spot these kinds of "dependent type" issues immediately after you have written the statement, so under this loose analogy, it's statically typed.




The only industry where static typing has a genuine difficulty is infrastructure because it genuinely operates a level of text only and exit codes.  Nevertheless Puppet does have a type system of sorts. 
