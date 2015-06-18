
## Agile Data, Code Quality and Cross Functional Teams

In this post we will focus on how in Agile and XP delivery velocity was increased and *maintained* through

 - Teams that can build every part of the system, including even infrastructure and deployment
 - Code, design, (mathematics) and architecture is kept clean so that at any given point in time adding to the system is easy

Neither of these things currently are accepted and respected practices in the Data Science world at the moment.  The cause of this is due to a single problem:

Analytics has become tightly coupled with productionization.

This is a problem born out of the very definition of a Data Scientist; someone in between an analyst / mathematician and a software developer / engineer.  The analogy in web development is the full stack developer, who may have started out in their career by tightly coupling their view code (the FE) with the business logic (the BE).  The reaction was to invent and formalize a system of decoupling the roles so that systems became cleaner and easier to change, and so came MVC and it's derivatives.  The first section of this post will propose a similar structure for Data Science.

Data Science has been defined as "a person who is better at maths than a coder and better at coding than a mathematician", but the converse of this is less appealing - "someone who is a worse coder than developer and worse at maths than a mathematician". This is how cross functional teams feeds back into cleanliness, and cleanliness feeds back into closer collaboration. Get the two right and you have yourself a positive feedback loop that will pump out products at speed.  In the second section of this post we will discuss how to get your Data Scientists to go from jack of two trades to a master of both.

### Introducing A2EM

A2EM is an acroynm, mnemonic, "algebriac expression" and represents work flow and separation / decoupling.

**A** - Ad hoc Analysis

**2 x E** - ETL and Evaluation

**2 x M** - Model and Mathematics

We flow from Ad Hoc Analysis 2 ETLs, Evaluation, Models and Mathematics.

The "2" also represents a *separation* between the ad hoc world of interactivity and visualizations and the actual product which is the combination of ETL, models and evaluation.  This separation ought to be physically compounded by splitting a Data Science project out into two repositories and using a different toolset for each. Just as in web development the back end development is done in a different IDE to the front end development. The mantra of MVC is "don't put business logic in your view code" the mantra of A2EM will be "don't put production code, especially ETL & Evaluation, in your Ad hoc interactive environment".

In order for Data Science to embrace the lessons Agile and XP taught to software development, like clean code & design, TDD and cross-funtional teams, we must first embrace a framework that is conducive to clean decoupling of responsibilities and roles.

### Step by Step Work Flow of A2EM

#### 1. Create 2 Repositories

Suppose your project is called "my-project", create two repositories, one called "my-project" and one called "my-project-ad-hoc".  In the latter you can put your notesbooks (like iScala, iSpark, Jupyter, R-Studio files, etc), interactive environments, images, etc.  In the former, henceforth "main" or "EM", your going to put high quality, neatly structured, automatically tested, production worthy code, which you should use a proper IDE for. E.g. if you do Data Science in Scala choose something like Intellij, or if it's Python, choose PyCharm.

You need 2 repositories because unfortunately most notebooks save as data, not code, which completely breaks history.  Still try to keep your images and actual data untracked as these things might bloat the size.  Instead ensure scripts can generate the images or grab the data from a warehouse as required.

Note: I like using no suffix/prefix for the main repo as it is primary, only the ad hoc repo deserves a suffix/prefix as it should be considered secondary to the main repo.

#### 2. Ticket Tracker and Git Flow

Use a light weight tracker like Jira, Trello or even Mingle, avoid monolithic dinasours like ServiceNow.  Then follow the [git flow branching model](http://danielkummer.github.io/git-flow-cheatsheet/) for your "main" repo.  What branching model you use for the ad-hoc repo is up to you as it's not so important.

This post will not labour over the details of various work flows, rather I have found the following key points are the most important in any Agile work flow:

*(A)* [Done means done](http://www.allaboutagile.com/agile-principle-7-done-means-done/), completely finish a ticket before moving on to the next.  A single 100% done ticket is better than ten 99% complete tickets.
*(B)* Try to map `git flow` commands to the moving a ticket between columns on JIRA.
*(C)* Tickets should only last days, not a week or more.  If they take too long it means you have incorrectly scoped.

#### 3. Write a Test

Before writting code in the main repo write a test according to [Agile Data and *DD](LINK). Of course you might not know what main code you want to write, so go ahead and use the ad-hoc repo.

#### 4. Ticket Completion - Tidy, Review and Run

A ticket should not be considered completed if no code has been committed to the main repo. This can be some little utility functions you wrote to help you with your ad hoc analysis, a script to generate some images / html, some ETL code, some evaluation code, some mathematical functions or some modelling code.  You should then remove that code from the ad hoc repo and import it as a library upon further use.  You should not end up with production code in your ad hoc environment, and you definitely should not end up with any hacky ad hoc cruft in your main repo.

Ensure your code is clean and tidy and submit it for review to a colleague.  The colleague ought to be able to run scripts and or tests in your main repo and see what you have acheived.  The colleague should not have to open your notebooks, or whatever ad hoc environment you use, in order to see what you have been doing.  The colleague should consider the ticket complete if and only if what they observe you have done is exactly what the ticket stated, the code is clean, and the code is automatically tested.

#### 5. Ship It

Artefacts for deployment onto production environments should only be from the main repo.  Be sure to use an artefact repository and CI along with git flow. You should be releasing once a week or fortnight. It might take many sprints to get to a full ETL -> Model -> Evaluation pipeline but get into the habbit of releasing even when it's not perfect. Downstream processes for taking the data and using it need to hook in until you are happy the data is of suitable quality, nevertheless you should be showing business value and progress every sprint. Agile embraces the philosophy that nothing is ever truly complete, most things are an ongoing iterative process. The smaller the iterations the faster the ultimate aim will be met and the higher the quality will be.

### Cross Functional Data Science Teams

I have heard and seen increasing frustration in the market as to what actually is a "Data Scientist", and how finding candidates with the full skill set seems to be extremly hard.  How can a company build "a team of half a dozen Data Scientists", when they cannot even seem to find one.  When the goal is unrealistic, the posts should shift, and here the focus should be on building a "Data Science Team", not "a team of Data Scientists".

According to [this slightly offensive article](http://www.computerworld.com/article/2929766/data-analytics/youre-hiring-the-wrong-data-scientists.html) we should be hiring more "Data Engineers".  Now I agree with many of the points made in the article, but the attitude regarding Data Science is a little arrogant.  In particular it uses a hospital analogy where it compares Data Engineers to janitors and Data Scientists to Surgeons, ignoring the rudeness, it's quite wrong:

1. Data Engineers often have very difficult to attain skills, like writting Map Reduce jobs, understanding complexity theory and understanding modern hardware, which a "Data Scientist" may not have or would take weeks/months to learn.  This isn't the case for the janitor-surgeon analogy.
2. Data Engineers can usually quickly (weeks/months) learn a lot of Data Science skills provided they closley work with good mentors.  It would take a janitor many years, even a decade, to become a surgeon.

The key point being the length of time to switch places is probably roughly equal, but isn't for the janitor-surgeon analogy.  

#### Dirty Data is a Process Problem

Cleaning dirty data is not a job, so you shouldn't hire someone to do it.

Writting a function to remove trailing white space from IDs, or removing duplicate entries isn't actually that horrible a task, and when you have billions of records it's not trivial either.  What is a horrible task is understanding why the data got that way and what assumptions about the data hold true, this is not engineering, scientific or technical, this is liasing and investigating - perhaps we need a "Data Detective"?  No, dirty data is generated by dirty software developed using dirty dinasour processes like waterfall.  Data Scientists and Data Engineers can't cure it, they can only treat it for long enough to get their own job done.  What they should be doing is raising bug tickets, tracking down product owners and communicating to managers that you can't build a house on shaky foundations.

Now let's correct the analogy.  Suppose a doctor spills blood onto the floor every time they take a blood sample due some process being followed incorrectly, this results in dirty floors all over the hospital and means other doctors have to clean it up themselves before doing their own job.  As a manager of the hospital, you observe this is ridiculous and your doctors are being slowed down, would you a) invent a new title, "Blood Cleaner", and employ ten of them immediately or b) track down the doctor that spills the blood and correct his behaviour?

#### So who should we hire?




Do not condone a culture of work arounds by working around the problem

If your email client duplicated all your emails, would you write a script to deduplicate them then ignore it? I hope not, I hope you would raise a bug.



Engineers, Scientists and Mathematicians cannot solve the problem of dirty data, they just.  

Indeed some data engineering work 

paraphrased states "Data Engineers are to nurses and cleaners as Data Scientists are to are compared with nurses and cleaners 

Mix of members:

Maths degree, trad Data Scientist, Data Engineer, Software Developer, 0.5 DevOps for every four team members.

### A Note On Language Choice

Choose best tool for the job.

Scala means you can prototype and deploy in a single language.

Statically typed.

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
