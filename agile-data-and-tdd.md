## Agile Data and *DD

This post will not labour over the subtle differences between the annoying number of terms mentioned in the [precursor post](LINK), which will henceforth be collectively referred to as *DD, rather we will focus on how to apply the principles they each have in common to a field that at a glance precludes using them.  *DD has worked well in web development but it seems difficult to apply in data driven fields.

#### "Problems" of *DD in Data Science

*Evaluation*: How does one write an "automatic test" for a model? What is the definition of passing? We usually look at the ROCs, some charts and evaluation measures and consider the model "good" when the numbers are large (but not so large that something fishy is going on with our features or validation framework).

*Model Exploration*: Often using single measures of performance doesn't really make sense, rather we look at charts, we add more colours and dimensions, we look at clusters, and when it seems kinda reasonable we stop and consider the task done.

*Data Exploration*: We also have a habit of postponing automation, of say ETL, or running the models, or evaluating the models.  That isn't prototyping, that's productionization, and we will save that for later or get someone else to do it. We say to ourselves "we don't have an awesome model yet, why would I put any effort into productionization?".

The process is interactive and iterative, which is good, but it is also quite manual, which is bad.

#### "Problems" of *DD in Big Data

*Long Running Jobs*: How can a test-cycle work for something that takes 5 hours to run? Aren't tests supposed to run in a few seconds?

*Computational Resource Problems*: How can we write a test to catch out of memory errors? Or out of disk space errors?

### The Core Principle of \*DD Practices

The "problems" we use to justify not writting automated tests in the data world are not problems with the practice, but the mindset.  Lack of automation in *any* type of software development is driven by:

 - Fear of delivering something that isn't perfect
 - a misconception that exploratory work precludes automated deliverables
 - lack of imagination on how to test
 - lack of clarity of objectives
 - a natural propensity to focus on the fun and clever stuff, not on delivery

Over the years the core principles of \*DD have been buried under superfluous (but still desirable) aspects, such as frameworks, tools, ubiquitis languages, speed, and ambiguity over what it means to write test code before writting main code.  **The absalute core is simply**:

1. Defining the use case in such simple and unambiguous terms so that success can be defined formally and even executed by a machine
2. Doing 1. *before* trying to solve the problem

### Test Code First (TDD) or Main Code First (DDT)?

Uncle Bob (in a talk I struggle to find) defined "legacy code", **not** as ugly code, or old code, but code that is not tested.  In [a TDD debate](https://www.youtube.com/watch?v=KtHQGs3zFAM) he also defined "proffesionalism" as "Not shipping a line of code that does not have an executing unit test" \[1\]. Both definitions are fantastic, but note how neither actually requires writting tests before code even though Uncle Bob is a TDD evangelist.

Arguments against writting test code first:

*(A)* The main code may turn out to be so simple it doesn't need a test, it might just call one or two native libraries, or well known third party libraries

*(B)* too much focus on tests can result in bad and bloated design (see http://www.infoq.com/news/2014/06/tdd-dead-controversy and http://david.heinemeierhansson.com/2014/test-induced-design-damage.html).  This is more common in OO and procedural langauges, sometimes projects have overly decoupled code into hundreds of classes and methods, epic dependency injection and towering hierarchies of interfaces and abstractions.

*(C)* This work is exploratory, I don't know if I'll need this method in future, I might ditch it

Both (A) and (C) can usually be countered by just writting a simple low-effort test, *except* in the case where we would then need to redesign the code in order to abstract out or decouple context and dependencies, i.e. (B).  When you argue about anything (except mathematics) for long enough, and your a reasonable person, you usually conclude "well it depends".  What it depends on should become the focus of clarification, and in the TDD vs DDT argument it boils down to the following:

\[1\] Jim Coplien makes a nice addition relating to CDD, that is we should write tests in terms of "contracts" or "properties" that execute via the random generation of examples rather than singular examples - we will come back to this point.

### Domain Specific Code and Generic Code

As a rough rule of thumb:

For **Domain Specific Code**: use \*DD, let use cases motivate tests, which in turn will then drive design and development. Then your tests and your design will both effectively meet exactly your use case while communicating your code in the context of the domain.

For **Generic Domain Agnostic Code**: relax the rule a bit and let complexity and low level design drive tests (DDT). If it would be a small effort to write a simple failing test first, like "does not throw exception", then you might as well, remember even "not compiling" is considered a failing test in TDD.  You do not really need a complex test for a generic couple of lines of code that just call native or standard third party libraries.  If a method becomes complex or makes calls to non-native methods then time to write some logic tests.

Which then should be combined with the following methodlogy.

### Outside In methodology to \*DD

Firstly it's assumed your team uses git, a sensible branching model, like git-flow (or trunk can work for 1-2 person teams), and a light weight task tracker, like JIRA or Trello. I discuss work flow in more detail in my (Agile Cross Functional Teams)[LINK] post, but within the context of *DD here is the step by step process

1. Broadly speaking all tickets must relate directly to a demonstratable use case and an automatable deliverable.  Even for tickets for exploring data and producing some plots / html pages think about the output, the consumer of that output, what's the minimum work required to generate business value from the exploration and how can I hook in an entry point (like a bash script).
2. Given the ticket, start a TDD/BDD cycle from the *outside* in, so start with the entry point. My favourite first test is "script returns zero exit code and produces an output of non-zero size".
3. Work your way down the layers in the TDD/BDD cycle filling out the high level business logic and design. As we get closer to details our tests ought to become more detailed and complex.
4. When we hit the need for low level code that is domain agnostic code, apply the above rule of thumb. This is also an cycle, we try to implement simply, when we seem to be failing we introduce a test then get back to implementation, which then may require introduction of another even lower level routine, if we can't implement that simply we introduce a test for that, and so on.  We still have tight iteration between the test and main code, but unlike TDD main code can come slightly before test code.

The rule of thumb combined with the outside in methodology then gives us the best of all worlds:

 - **High level** design represents use cases and communicates domain
 - No code bloat; all code contributes business value
 - 100% test coverage in some form or another
 - Unambiguous executable documentation
 - Assurance that complicated code works
 - No redundant tests that test trivial code
 - **Low level** design is elegant, minimal in number of classes & methods, terse, concrete and optimizable

