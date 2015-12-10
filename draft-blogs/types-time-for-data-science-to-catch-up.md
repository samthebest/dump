
Alternative title: Why Build Tools Block Data Science From Going Typed,

Spark + Scala + Build Tools Suck! - Why Data Science Isn't Going Typed

# Build Tools Suck! - Why Data Science Isn't Going Typed

The other day was the last straw when I realised even the front end world is going statically typed (and rather functional) after seeing TypeScript; a statically typed version of JavaScript used to write Angular 2.0.

What is going on! Why is Data Science still largely driven by dynamically typed scripting languages? The often mathematical nature of Data Science makes this doubly shocking; if anything, Data Science should have be the first industry to fall in love with compiled statically typed languages, not the last. 

Common arguments are as follows:

1. Lack of high-level machine learning libraries in Scala
2. Dynamic languages are low on boiler plate
3. Barrier to entry, language difficulty
4. Lack of interactive environment(s)
5. Barrier to entry, tooling particularly dependency management

1, 2, 3 and 4 are misconceptions, but I'm not going to focus on those in this post since I'm usually bashing "Type A" Data Scientists for one reason or another (lack of unit testing, automation, transparency, clean code, collaboration, Agile methodologies, MVPs (POCs instead), etc). Rather I'm going to bash the "Type B" Data Scientists and the Scala community for not doing anything *constructive* to make the cross over easier. Hyping up the benifits of functional statically typed languages isn't enough, we need to address issue 5.

# Hello World in Big Data is a World of Hell

Sorting out a proper build file for a Scala project with multiple dependencies is a multi-day ticket.  Throw in a release process, some quirky libraries, deduplicate issues and Spark, and it's more like a whole week.  Now try asking someone completely unfamiliar to Scala (and Java) and completely oblivious of it's benifits.

Your basically asking a Data Scientist to put more time & effort into their build than into some complex algorithm! Furthermore the setup overhead hugely stifles innovation and spikes, something extremly important in Data Science.

Let's loosley compare Scala with some other languages (please comment and I'll add to this list), it seems:

 - Python: install Anacanda and voila you can add an import for nearly anything you ever wanted
 - C# and F#: use a GUI, i.e. Visual Studio, to search for packages then click "add". Pretty simple.
 - R: Not used R, but according to http://www.r-bloggers.com/installing-r-packages/ looks like adding one or two lines to one file

Now Scala

1. install sbt or mvn, 
2. create an incomprehensible build file that is either an epic xml (mvn), or a sequence of largely random characters (sbt) and version numbers,
3. create a ridiculous directory structure using this ridiculous reversed domain name convention
4. create another directory and another file which says what plugins you want, because you always need at least one plugin to package up all your dependencies in some way
5. use an IDE (usually Intellij) to open the build file, click "refresh project" (or setup auto-refreshing), go get a cup of tea, come back and start reading error messages
6. error messages include things like "you need blank lines" (finally fixed in sbt 0.13.7), syntax errors, unresolved dependencies, but mainly crap about version numbers
7. add another plugin to your plugins file to get a tool to allow you to view a dependency graph so you can start debugging your version issues
8. run the dependency graph tool which throws an AIOB exception, Google it and realise you need to use a different command which outputs a file
9. run the graph tool which generates a .graphml file, download another tool to open this file (because a .png would be way too simple)
10. try using some shading plugins
11. try changing the merge strategy
12. try exlcusions, try "provided" annotation
13. Run your jar using `java -cp com.company.EntryPointClass path/to/jar` (much easier than `./my-script.py`!?).  
14. Now your code throws "ClassNotFoundException" or "NoSuchMethodError"
15. commit suicide

This isn't even considering using everyones favourite Big Data processing tool, Spark, a framework that is so epically complicated to setup dependencies it even comes with it's own script `spark-submit` for "doing this for you" (not that it succeeds).

# Solutions

The solution is **not** to bash on about how in Python you spend 90% of your time running and debugging code, or trying to understand someone elses high level libraries because writing your own code is so labourious, then asking them to push through that barrier to entry for Scala.  That is like trying to explain color to a blind person, then proposing some extremly painful five day brain surgery in order to add sight.

So firstly a build tool that did **full shading by default** would massively lower the barrier to entry for Data Scientists (anyone care to volunteer a fork? please comment).  Some old school Java developers who still have magnetic disks might complain about jar sizes, load times and build times, but there are solutions to such problems.  In particular several times Martin Odersky has talked about "Typed Trees" or a "staging" layer to introduce abstract typed syntax trees mainly to tackle the binary incompatibility problem in Scala.  Such functionality could also allow for significantly greater compile time optimisations and jar size minimisation by deduplication at the AST level.  Though I doubt such solutions will arise any time soon.

## An Interim Solution - Calling all Scala fans

When I start a new project I tend find a one on github that is similar to my needs, then modify their build file.  Starting from scratch or following a tutorial each time is quite painful and this is probably what Data Scientists are doing.

Here is a new repo https://github.com/samthebest/scala-build-files that aims to serve as a repository or archive of build files examples and template projects.  The theory being that if just one Scala fan from every couple of companies removed the code, intellectual property, internal URLs and company names from their project it would serve as a 

sbt-build-files

# Finally a brief rebuttal to points 1 - 4

1. Writing mathematical code in a mathemtical functional language is easier, more direct, flexible and transparent than using the high level highly approximate highly parametric interpretations of mathematics provided by others, which is ultimately written in vast amounts of highly procedural impenatrable code.
2. See language features type inference, implicits, and ad-hoc polymorphism. See the collections library, scalaz and shapeless.
3. The language is easy, first unlearning OO and procedural styles is what is hard.  Mathematicians who have never learnt any language tend to find functional languages easier to learn.
4. See iScala/iSpark notebook, Zeppelin, etc





Some Data Scientists argue it's the lack of high-level machine learning libraries for Spark, orbut I somewhat disagree and I'll address this further down.




Some experienced developers might just think I'm lazy and have not invested enough time reading the source code for sbt or mvn to understand how it works (do python programmers read the source code for the python interpreter?).  That's partially true but I do have a good understanding of compilers and can undoubtedly say the issue with the JVM ecosystem is by design (rather lackof) not necessity.  In a nutshell, the top two issues are (a) JVM language compilers should have shading by default and (b) use intermediate abstract typed syntax trees for jar size optimization, runtime optimization and binary incompatiability resolution. Alas, I won't digress into the details, the conclusion is clear, build tools suck.


...

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

There are various arguments why Data Science hasn't caught up, but I'm going to argue that all but one are invalid.  My conclusion is in the title of course, the reason why the best candidate for statically typed Data Science, Scala, hasn't gained quite as much adoption as I wish is because of build tools, especially for projects using Spark.

# Why should you care about static typing, in a nutshell

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

The biggest complaint people have about Scala, and it's certainly mine, is getting an IDE, SBT and my libraries set up.  Sorting out a proper build file for a Scala project with multiple dependencies is a multi-day ticket in JIRA even for an experienced Scala/Java developer.  Throw in a release process, some quirky libraries, deduplicate issues and Spark, and it's more like an entire sprint.  Now try asking someone completely unfamiliar to Scala (and Java) and completely oblivious of it's benifits to spend potentially weeks figureing out how to get their laptop & production environment into a position where they can deploy a single line of code.

Your essentially saying to a Data Scientist, your going to have to put more time and effort into getting a "word count" application to build than to get highly complex mathematical algorithm to work, it's ridiculous.

Compare with Python, one just installs Anaconda and boom you have everything you ever wanted without ever even seeing a build file!

Now throw in everyones favourite Big Data processing tool, Spark, a framework that is so epically complicated to setup dependencies it even comes with it's own scripts for "doing this for you" (not that it succeeds).  In the early versions of Spark you could run your jar with `java -cp`, now you have to use `spark-submit` because so many users had problems getting a working build file.

I used to believe that I couldn't understand how to get build files to work was because I never invested enough time into it.  Now over the years I certainly have spend a lot of time on them.

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
