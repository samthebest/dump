
# Types - Time for Data Science to Catch Up

https://youtu.be/NW5h8d_ZyOs?t=18m40s

"You can write literally 100s of lines with [Scala collections] or Spark [RDDs with the Scala API] then once all the type errors are fixed the chances are very very high that your program will run. If you don't have the static types that is of course not true, then essentially you run your program and debugging starts and you say *why did I get this weird thing!*"

Python / R developers require very high level libraries that make a lot of decisions about the way a machine learning algorithm will run.  Many argue this is why Python and R are great Data Science languages; because over the years many high-level libraries have been written, i.e. if you want high-level libraries, choose Python or R.  I would argue that the converse is also true, that if you want to use Python or R you *must have* high-level libraries that do most of the job for you because writting the lower levels of code is just so hard in these languages.  

So we have a codependent relationship between the two that has resulted in people forgetting that what they want is the end result.  A Data Scientist doesn't *want* high-level libraries, rather they *want* to get their job done as painlessly as possible.  High-level libraries have become synonymous with "getting a job done painlessly", but this is only because Data Scientists are working with a language that makes everything painful.  

The analogy is that Bill lives works in an office with upturned nails sticking out of the ground, and the office provides Bill with metal shoes.  Bill learns to associate metal shoes with a lack of pain.  Alice invites Bill to work in another office with carpets, Bill asks "does your office provide metal shoes?" She respons "no, we have no need because ...", Bill interupts "Bah! that's crazy it must be so painful to work in your office, now leave me alone."




The only industry where static typing has a genuine difficulty is infrastructure because it genuinely operates a level of text only and exit codes.  Nevertheless Puppet does have a type system of sorts. 


Even Natural Language is typed, if I say "I don't want much apples" this is grammatically incorrect because the type of "apples" is discrete and so we should really say "I don't want many apples". Whether it's dynamic or static is a debatable since natural language is not "run". Nevertheless word processors can spot these kinds of "dependent type" issues immediately after you have written the statement, so under this loose analogy, it's statically typed.

If static types are so great, why did dynamically typed languages come about?  The issue with statically typed languages is that the compiler needs to be significantly more complex, and any short cuts taken in the compiler result in the language user having to introduce a lot of "Boiler plate" to assist the compiler in it's type checks.  The more powerful the type system, either the user has to do a lot of work to make get there program to compile, or the compiler has to do a lot more work to implicitly fill in gaps of reasoning.  Martin points out that coq requires a Phd to use it.

The solution with Scala is to find a comprimise, in particular it offers type inference, implicit conversions and consequently smooth ways to do ad-hoc polymorphism.  This pretty much eliminates most redundant boiler plate code while giving a tolerably fast compiler (I say tolerable because it's not quite as fast as some people would like).  Perhaps when Ruby, R, JavaScript and Python where invented the effort and speed of computers would prohibit such approaches - consequently in the quest for eliminating boiler plate, language inventers chose dynamic typing.



Rust

TypeScript


