
## Agile Data, Code Quality and Cross Functional Teams

In this post we will focus on how in Agile and XP delivery velocity was increased and *maintained* through

 - Teams that can build every part of the system, including even infrastructure and deployment
 - Code, design, (mathematics) and architecture is kept clean so that at any given point in time adding to the system is easy

Neither of these things currently are accepted and respected practices in the Data Science world at the moment.  The cause of this is due to a single problem:

Analytics has become tightly coupled with productionization.

This is a problem born out of the very definition of a Data Scientist; someone in between an analyst / mathematician and a software developer / engineer.  The analogy in web development is the full stack developer, who may have started out in their career by tightly coupling their view code (the FE) with the business logic (the BE).  The reaction was to invent and formalize a system of decoupling the roles so that systems became cleaner and easier to change, and so came MVC and it's derivatives.  The first section of this post will propose a similar structure for Data Science.

Data Science has been defined as "a person who is better at maths than a coder and better at coding than a mathematician", but the converse of this is less appealing - "someone who is a worse coder than developer and worse at maths than a mathematician". This is how cross functional feeds back into cleanliness, and cleanliness feeds back into closer collaboration. Get the two right and you have yourself a positive feedback loop that will pump out products at speed.  In the second section of this post we will discuss how to get your Data Scientists to go from jack of two trades to a master of both.

### Introducing A2EM

A2EM is an acroynm, mnemonic, "algebriac expression" and represents work flow and separation / decoupling.

**A** - Ad hoc Analysis
**2 x E** - ETL and Evaluation
**2 x M** - Model and Mathematics

We flow from Ad Hoc Analysis 2 ETLs, Evaluation, Models and Mathematics.

The "2" also represents a *separation* between the ad hoc world of interactivity and visualizations and the actual product which is the combination of ETL, models and evaluation.  This separation ought to be physically compounded by splitting a Data Science project out into two repositories and using a different toolset for each. Just as in web development the back end development is done in a different IDE to the front end development. The mantra of MVC is "don't put business logic in your view code" the mantra of A2EM will be "don't put production code, especially ETL & Evaluation, in your Ad hoc interactive environment".

In order for Data Science to embrace the lessons Agile and XP taught to software development, like clean code & design, TDD and cross-funtional teams, we must first embrace a framework that is conducive to clean decoupling of responsibilities and roles.

### Step by Step Work Flow of A2EM



1. create 2 repositories (you need two distinct repos because A precludes effective version control).
2. Start with a ticket (follow a parallel git-flow)
3. Start by writting a test, or if you really have no idea what you want to do, then go ahead and start exploring
3. Suppose you have some dirty data, and you explore it, you start cleaning it as you go, trimming fields, adding types, etc.  Make sure you move any helper methods, etc into your EM code-base.
4. The ticket should not be considered completed until some code has been commited into the EM part of the code base.
5. You ship EM, you build artefacts from EM, A is only for the Ad hoc Analysis


EM contains your product, your libraries, your entry poitns and your tests.



Data Scientists are quite familiar with notebooks and interactive environments, so let's start with the bit they are usually less familiar with IDEs and productionization.

### 

(Footnote: Alternatives, AV2EM, AI2EM, IV2EM, V2EM, A2P (p for product), IV2P, )

AWESOME ACRONYM



If the ticket is quite technical/mathematical and just a step in order to achieve some other task that is more clearly business facing, then at least ensure the tickets are linked together using links, labels, epics or whatever - so that a business person could trace the ticket through the tracker and see it's (in)direct business value.





- code quality
- language choice and tool choice.  Particularly static typing.
- pair programming (change every 30 mins, have two keyboards plugged into single computer, ensure shortcuts are the same across team).
- Code review
- Document review
- Atlassian stack is awesome
- Phds are unnecessary, more than one per team is unnecessary
- two projects approach - one for notebooks one for libraries, tests and entry points

Engineers are roughly evaluated by the number of technologies multiplied the number of years of use.
Data Scientists seem to currently be roughly evaluated by the number of out-of-box algorithms they can list multiplied by the number of projects they have applied it to.

So engineers are happy to learn new technology and languages, it gets them excited, "great another acronym for my CV".  Data Scientists seem less enthused and just want to stick with whatever tools they are used to.  On a Software Development CV you will see a lot of tech, but you will also see a lot of focus on Agile methodologies, especially for the more experienced CVs.

In order to increase collaboration, ramp up, cross project collaboration, job satisfaction, focus and productivity the team will try to implement more pairing.  We will also try to ensure each member has at least one JIRA from one project that is not their main project.  Such tickets are good candidates for pairing tasks.  We should aim to rotate and pair at least twice a week since the day to day distractions of BAU often mean we do not initialise pairing, nor do we want to drop what we are doing, I propose we use the Calendar to book slots.  Each slot ought to be either 10 - 12 or 14 - 16/17.  We should put the invites in at sprint planning, of course such slots can be moved, but once created it's harder to overlook.  After doing some reading I consider the following techniques to be ways to do good pairing, each rule is a rule of thumb not a law of god

1. Pairing slots should not be too long (like an entire day), with the same person
2. Driver and navigator should switch every 30 minutes. Try to stick to this, this is quite important. Set a timer.
3. Take a 5 minutes break at switches (if necessary), sometimes it can make sense for the navigator to take a short break while the driver is deep in thought
4. Plug in two keyboards into the machine
5. Shortcuts should not be changed from the defaults. If someone insists on non-standard shortcuts, it's up to them to write two scripts to switch between their config and the default quickly.
6. Pairs should take it in turns between slots as to which desk to sit at (since the configuration of the desk will usually favour the desk owner).
7. Navigator must not have their own computer (or phone) near them
8. The driver should always be communicating, pairing is not one person codes and the other watches.
9. Not is it one person types and the other tells them what to type.  If the configuration is Novice-Expert Driver-Navigator resp, the navigator should try to use language that the driver understands, but not give them so much detail that the driver is just typing for the navigator.
