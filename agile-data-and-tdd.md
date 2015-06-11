## Agile Data and *DD

This post will not labour over the subtle differences between the annoying number of terms mentioned in the [precursor post](LINK), which will henceforth be collectively referred to as *DD, rather we will focus on how to apply the principles they each have in common to a field that at a glance precludes using them.  *DD has worked well in web development but it seems difficult to apply in data driven fields.

### The Core Principle of \*DD

The "problems" we use to justify not writing automated tests in the data world are not problems with the practice, but the mindset.  Lack of automation in *any* type of software development is driven by:

 - Fear of delivering something that isn't perfect
 - a misconception that exploratory work precludes automated deliverables
 - lack of imagination on how to test
 - lack of clarity of objectives
 - a natural propensity to focus on the fun and clever stuff, not on delivery

Over the years the core principles of \*DD have been buried under superfluous (but still desirable) aspects, such as frameworks, tools, ubiquitous languages, speed, and ambiguity over what it means to write test code before writing main code.  **The absalute core is simply**:

1. Defining the use case in such simple and unambiguous terms so that success can be defined formally and even executed by a machine
2. Doing 1. *before* trying to solve the problem

### Test Code First (TDD) or Main Code First (DDT)?

Uncle Bob (in a talk I struggle to find) defined "legacy code", **not** as ugly code, or old code, but code that is not tested.  In [a TDD debate](https://www.youtube.com/watch?v=KtHQGs3zFAM) he also defined "professionalism" as "Not shipping a line of code that does not have an executing unit test" \[1\]. Both definitions are fantastic, but note how neither actually requires writing tests before code even though Uncle Bob is a TDD evangelist.

Arguments against writing test code first:

**(A)** The main code may turn out to be so simple it doesn't need a test, it might just call one or two native libraries, or well known third party libraries

**(B)** too much focus on tests can result in bad and bloated design (see http://www.infoq.com/news/2014/06/tdd-dead-controversy and http://david.heinemeierhansson.com/2014/test-induced-design-damage.html).  This is more common in OO and procedural languages, sometimes projects have overly decoupled code into hundreds of classes and methods, epic dependency injection and towering hierarchies of interfaces and abstractions.

**(C)** This work is exploratory, I don't know if I'll need this method in future, I might ditch it

Both (A) and (C) can usually be countered by just writing a simple low-effort test, *except* in the case where we would then need to redesign the code in order to abstract out or decouple context and dependencies, i.e. (B).  When you argue about anything (except mathematics) for long enough, and your a reasonable person, you usually conclude "well it depends".  What it depends on should become the focus of clarification, and in the TDD vs DDT argument it boils down to the following:

### Domain Specific Code and Generic Code

As a rough rule of thumb:

For **Domain Specific Code**: use \*DD, let use cases motivate tests, which in turn will then drive design and development. Then your tests and your design will both effectively meet exactly your use case while communicating your code in the language of the domain.

For **Generic / Domain Agnostic Code**: relax the rule a bit and let complexity and low level design drive tests (DDT). If it would be a small effort to write a simple failing test first, like "does not throw exception", then you might as well, remember even "not compiling" is considered a failing test in TDD.  You do not really need a complex test for a generic couple of lines of code that just call native or standard third party libraries.  If a method becomes complex or makes calls to non-native methods then time to write some logic tests.

Which then should be combined with the following methodology.

### Outside In methodology to \*DD

Firstly it's assumed your team uses git, a sensible branching model, like git-flow (or trunk can work for 1-2 person teams), and a light weight task tracker, like JIRA or Trello. I discuss work flow in more detail in my (Agile Cross Functional Teams)[LINK] post, but within the context of *DD here is the step by step process

1. Broadly speaking all tickets must relate directly to a demonstrable use case and an automatable deliverable.  Even for tickets for exploring data and producing some plots / html pages think about the output, the consumer of that output, what's the minimum work required to generate business value from the exploration and how can I hook in an entry point (like a bash script).
2. Given the ticket, start a TDD/BDD cycle from the *outside* in, so start with the entry point. My favourite first test is "script returns zero exit code and produces an output of non-zero size".
3. Work your way down the layers in the TDD/BDD cycle filling out the high level business logic and design. As we get closer to details our tests ought to become more detailed and complex.
4. When we hit the need for low level code that is domain agnostic code, apply the above rule of thumb. This is also an cycle, we try to implement simply, when we seem to be failing we introduce a test then get back to implementation, which then may require introduction of another even lower level routine, if we can't implement that simply we introduce a test for that, and so on.  We still have tight iteration between the test and main code, but unlike TDD main code can come slightly before test code.
5. Before pushing your code delete any code that when removed does not make any tests fail, such code must either be pointless or steps 1 - 5 haven't been correctly followed.

The rule of thumb combined with the outside in methodology then gives us the best of all worlds:

 - **High level** design represents and communicates use cases in the language of the domain
 - No code bloat; all code contributes business value
 - 100% test coverage in some form or another
 - Unambiguous executable documentation
 - Assurance that complicated code works
 - No redundant tests that test trivial code
 - **Low level** design is elegant, minimal in number of classes & methods, terse, concrete and optimisable

### "Problems" in applying *DD to Data Science & Big Data

#### Use of Notebooks and Web GUIs

So before you can write a test for your entry point, you need to ensure what you are going to do is going to have an entry point. Similarly for methods and classes one needs a testing framework.  In the world of the notebooks, shells and labs, like iPython, iScala, iSpark, R Studio, ScalaLab, Jupyter, HUE, Zeplin, Intellij worksheets, bash, spark-shell, scala shell, python shell etc, we don't really have an entry point or easy hook-ins for testing frameworks.  Notebooks are great for exploration and interactivity, but they encourage bad practices if used for production code.

1. Notebooks encourage flat structure, resulting in disorganised code
2. They are powerful interactive tools, but **not** powerful code editors; code inspections, refactoring tools, etc are weak
3. They encourage manual testing
4. They cannot be tracked by git (they are stored as JSON, not code)
5. Your output, your business value, has a dependency on a software environment

In essence you are coupling your output to yourself and your environment.  Use the environment to write the code, but plan to deliver something independent of that environment.  The solution is to maintain two repositories, one for libraries and entry points, the other for notebooks.  Use the notebooks for playing, fiddling and exploring, but also use an IDE to produce production worthy code that has tests and commit this into the other repository.

In my other post on [Cross Functional Teams](LINK) I'll go into more detail on how one should maintain two repositories.

#### Model Performance

What is the definition of passing for model performance? Don't we just look at the ROCs and say "yup, ship it, looks fine"?  No, we have a use case and that use case ought to be able determine a few desirable score-thresholds along with a minimum level of acceptable performance.  Then you can write a test that at thresholds A, B and C say, the models precision say, is greater than the acceptable levels of A', B' and C' say.

Not only does this mean you now have a nice way to automatically test your model, you have also thought a lot about the business value of that model to arrive at the test. Now you won't waste any time over optimising a model, focusing on measures of performance that are not relevant or focusing on completely meaningless measures of accuracy like AUC (which I won't digress into, but I'll save for another post).

#### Speed Tests and Slow Jobs

This is a genuine problem because we ideally want tests to run quickly, which often just isn't the case with Big Data.  Tests can be similar to the above, that is we have some "acceptable limit" on how long the jobs should take.  The best advice to give is to have a good CI pipeline that automatically runs nightly tests on your develop branch on a realistic cluster.  Provided your team correctly practices a good git-flow workflow if you introduce a bug that slows your job down at least you will probably find out tomorrow.  That's not as good as the sub minute times in web development, but it's better than finding out just before you want to release.

Another trick which can work well for jobs that are known to downscale (i.e. work on less nodes but just slower) is to make your dev cluster much larger than your prod cluster so you can develop faster.

#### Resource Problems

This is again a real problem in Big Data. It's hard to write a test for out of memory errors, or disk space errors. The solution is again similar to the above, setup a good CI pipeline to automate the running of your jobs every night and before release.

#### Writing Test Cases is Boring

Yes it often is, that's why languages like Scala have awesome DSLs for **property based testing** (see ScalaCheck).  Jim Coplien in the debate with Uncle Bob pointed out the power of CDD - Contract Driven Development.  Using these frameworks one can write high level properties, or contracts, in a predicate calculus like DSL, then the framework will automatically generate test cases for you - as many as you want!

Combining CDD with good automated CI can be awesome.  We have a parameter in our tests which switches on "uber test mode", this tells all the property based tests to use an order or two of magnitude more test cases.  The tests then take an order or two longer to run, but if this is happening at night, then it doesn't really matter.  The "uber test mode" has successfully found several bugs while the developer effort was fractional.
