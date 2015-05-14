## Introduction

In Advanced Data Analytics team we take an agile MVP approach to everything we do.

## TDD & BDD & DHH

Technically TDD means writing tests before writing code, but one can end up with the same end by ensuring one unit tests everything, henceforth TDD will mean both "test first" and "test everything".  This section will discuss the pros and cons of this.  The overriding point of the section is to not do anything dogmatically, except to dogmatically pick the best and most relevant parts to the specific problem at hand.

We should all be familiar with the less controversial pros of TDD, which briefly are:

 - Provides unambiguous documentation
 - Ease of refactoring
 - Confidence in correctness of code
 - But there is some debate as to whether TDD is hammer to every nail particularly because it can influence design and result in code design that is serving tests not MVP.

http://www.infoq.com/news/2014/06/tdd-dead-controversy & http://david.heinemeierhansson.com/2014/test-induced-design-damage.html

The take away point, and within the context of Agile & MVP in my mind is:

1. TDD can cause the creation of many unnecessary abstractions, interfaces, and methods.
2. Designing code to be testable can distract from designing code to actually get the use case done.  
3. The core code ought to be the bare minimum, simplest, least possible code in order to deliver an MVP. Sometimes code is so simple it does not need tests, but designing it to be testable makes it so complicated that it then does need tests - the justification for the tests becomes circular!
4. Tests should be extraneous to the functionality of the application, and therefore not influence the main code.

Now contrary to this sometimes TDD can have the opposite effect in that it helps us steer our code to satisfy a use case and only that use case. It helps with naming and signatures:

"TDD also helps me with good naming, because I started with the use-case."

http://www.thoughtworks.com/insights/blog/using-tdd-influence-design

http://www.drdobbs.com/tdd-is-about-design-not-testing/229218691

One final point, before we move on to my solution is that in Data Science and Big Data, TDD is often impossible because one often cannot write units tests for things like accuracy in modelling and OOMs, serialization problems, disk errors, timeouts, runtime, etc in Big Data. Also highly optimised Big Data / Machine Learning code can become intentionally tightly coupled to reuse computations and be as efficient as possible - untangling it into several classes and methods can cause performance hits in memory, GC and call overhead.

So I have invented a new term which I believe we should follow (but not dogmatically (smile) )

## UCDD - Use Case Driven Development - Solving the TDD debate and providing Data professionals with hope

The procedure step-by-step:

### Step 1 - Map Work to a Ticket and the Ticket to a Use Case

So before you do any work you need to think about a use case, even refactors need to be justified.  Ugly code may be ugly, but if it's not impacting development time of other features, it's easy to read and it's fully tested don't get too ideological.  If you have a feature/task that will be made more difficult to implement thanks to that ugly code then go ahead and create a ticket to rewrite it. Always try to follow best practices, but best practices should be motivated by use case.  Of course the question of how did the code become ugly in the first place is important, and since development is a cycle the chronological place to answer this is at the end.

Ensure what you are about to do corresponds exactly to at the very least a ticket (JIRA / trello). Sometimes some documentation (confluence / md file) ought to also accompany the ticket.

*NOTE about Data Science*: Exploratory work should also be tracked, if your mission is to produce some graphs and stats to better understand data, think about who or what is going to consume that output.

### Step 2 - Map the Ticket to an Automatic Test

Every piece of work should have some kind of "automatic" test that decides if the work is complete.  When deciding on what kind of automatic test prioritize in the following order:

#### Can we implement the code so simply that testing is unnecessary?

That is the "test" is automatic in that it need not be created nor run, just writing and having it reviewed is sufficient.  In practice what this means is trying to avoid reinventing the wheel try to always find an existing library (native first, then third party) that does the job. All too often I see multiple lines of code implementing something that can be done in single line of code had the author just spent a little longer scrolling through the 'dot autocomplete' list of available methods.

##### To abstract or not to abstract in order to unit test - the UCDD ultimate question

Unit tests are the next priority as they are fast, run before a build and can be run anywhere (unlike systems/integrations/performance tests). Now old school TDDers would always say "yes we can", we can introduce some interface / abstraction / dependency injection / IoC, whatever, so that we can write fake implementations in our tests.  This is wrong under UCDD, the use case, the MVP, the application, is not it's tests, it is (tautologically) it's use case.  Avoid over-decoupling things just so that you can write a unit test. When you introduce abstractions and when you decouple ask yourself is this actually a better design? Does this abstraction actually dry up the code or just add unnecessary hierarchy? Is the code easier to read? Does the reader have to fit more information or less information into their brain?  If the only justification you can think of for a non-trivial design decision is the unit tests then it's not good design - only use case should motivate design.

Here is a good rule of thumb to follow when deciding whether to abstract:

1. When abstracting classes/interfaces do I 
 - have two concrete instances
 - will the number of LOCs actually decrease
2. When abstracting methods do I
 - have three concrete use cases OR
 - two use cases and the method is ~3 or more LOCs
 - will the number of LOCs actually decrease
3. I want to abstract in order to unit test, but I only have one concrete instance in my main code, then
 - The abstraction should be quite trivial, just introducing a few more LOCs and not require a non-trivial refactor
 - There must exist a potential use case for a 2nd instance in future.  Common examples are file system abstractions (local vs hdfs), web server abstractions, processing framework abstractions (spark vs hadoop), third party libraries, database, etc.
 - The abstraction is motivated by a system outside of our control, again the common examples are above ...
 - ... and conversely to c, abstracting / decoupling raw business logic which is inside our control without an immediate use case should be strongly discouraged, for that we move on ...
 
 
#### Black Box Build Time Tests

Unit tests usually correspond to a specific method or class and are named accordingly and use a unit testing framework, a build time black box test still use unit testing frameworks but do not necessarily correspond to a specific class or method - they can call many. E.g. Customer might have a unit test class called CustomerSpecs.

To use a real life example from my current work: we have an algorithm that can essentially take arbitrarily many near arbitrary SQL queries and optimize them into single spark job with a constant number of stages and a single read over transactional data.  Assume this is called SQLOptimizer. Now we have a test class called PrivacyEngineSpecs which ensures information generated by our core algorithm is aggregated at a sufficiently high level as not to cause privacy violations, but there is no class called PrivacyEngine.   We do not decouple the privacy logic from the SQL optimization logic as doing so does not satisfy the criteria above.  If hypothetically one day we release to multiple countries with different privacy regulations then at that point we should abstract out, not before.

Therefore the solution is to still have a nice build time automatic test using nice testing frameworks (we use ScalaCheck and Specs2) but implement that test at a higher level.  Indeed the test will be more complicated, it will have to work it's way in from the outside treating the algorithm as a black box, which in this example means defining multiple mock transactional data sets that when they fail do not have as nice a message as a unit test would.  When black box tests fail unfortunately they don't say "this class is broken" or "this method returned the wrong thing", they say "given this input, your output did not satisfy the use case" which may cover many methods and classes.  Debugging can be tedious, the art here is to start with the simplest possible use cases and to produce as many scenarios as possible then what I find is when you break one part of the black box this corresponds to specific set of tests even though all tests are essentially operating at the same entry point.  ScalaCheck is an extremely powerful tool here since it generates examples for you using a powerful DSL.

This approach may seem perverse and may mean the effort required to ensure your automatic tests make debugging just as easy as if you had of done TDD is greatly increased, nevertheless tests are extraneous to functionality rather they serve to test the use case and only test the use case.  Your main code wants to be as simple as possible to meet the use case.

Integrations Tests
TODO - scripts that run applications, over build time tests, but still test logic

Accuracy and Performance Tests
TODO - tests that do not test logic.  These can still be well defined by introducing thresholds, e.g. a ticket may say "Optimize Job X", if job X takes 5 hours the mission might be to make it run under 2.

# Random Quotes

"Do not write code to be reusable. Resuse code"

"When writing code, agile developers often stop to ask themselves, "What is the simplest thing that could possibly work?" They seem to be obssessed with simplicity. Rather than anticipating changes and providing extensibility hooks and plug-in points, they create a simple design that anticipates as little as possible, as cleanly as possible. Unintuitively, this results in designs that are ready for any change, anticipated or not."

"The XP guys have patterns in their toolbox, it's just that they refactor to the patterns once they need the flexibility."

"Bill Venners: So what do the XP guys do first, if they don't use patterns? They just write the code?

Erich Gamma: They write a test."

"

The system (code and tests together) must communicate everything you want to communicate.
The system must contain no duplicate code. (1 and 2 together constitute the Once and Only Once rule).
The system should have the fewest possible classes.
The system should have the fewest possible methods.

"
 

http://www.jamesshore.com/Agile-Book/simple_design.html



"A designer knows he has achieved perfection not when there is nothing left to add, but when there is nothing left to take away."

http://www.brainyquote.com/quotes/quotes/a/antoinedes121910.html

Do not over engineer, do not write code that is not used, do not abstract or encapsulate unnecessarily.



"Any intelligent fool can make things bigger, more complex and more violent. It takes a touch of genius and a lot of courage to move in the opposite direction." —Albert Einstein.



"Done means DONE!"

http://www.allaboutagile.com/agile-principle-7-done-means-done/

1 perfectly finished small feature is better than 10 half finished features.



"YAGNI - You aren't gona need it"

"design decisions should be deferred until the "last responsible moment", so as to collect as much information as possible on the benefits of the chosen option before incurring its costs."

"the practice is often reduced to the acronym YAGNI, for "You Aren't Gonna Need It"; this alludes to the usual counter-argument when a programmer tries to propose a costly design element based on its future benefits only ("We're going to need this Factory sooner or later, we might as well put it in now." "No, you aren't gonna need it.") "

http://guide.agilealliance.org/guide/simple-design.html


"Capture your initial architecture at the most compressed level of expression that covers the scope of your business. Avoid abstraction ..."

http://www.leansoftwarearchitecture.com/home/lean-and-agile-architecture-techniques

The point of abstraction is to DRY up your code (avoid duplication), and only some cases when methods or classes become very long and complicated to separate concerns - the principle of "separation of concerns" should come after the principles of simplicity and minimal code.
