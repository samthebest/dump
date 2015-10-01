
## Defining Data Science by the Team not the Individual

It seems the ambiguous and overly broad definition of a "Data Scientist" frustrates recruiters and candidates alike.

What distinguishes a Data Scientist from an Analyst, Software Developer, Acedemic, Engineer, Big Data expert, Visualisation expert, or a BI guy, or even a DevOps?  It seems the industry wants vast quantities of proffesionals to emerge that claim to have all of these skills, this has been sometimes referred to as the "Unicorn Problem of Data Science". Perhaps in time such unicorns will exist, but for now we need to solve this problem more realistically. For the meantime lets ignore the more interesting stuff, like the details of machine learning algorithms or Big Data frameworks, and focus on what is the actual business value Data Science brings.

In a nutshell, the key difference between a Data Scientist and an Analyst is the productionisation and automation of their code, over the manual generation of reports, or the handing over of prototypes to developers to do the productionisation for them.  Without productionisation, we are just renaming a long existing proffesion for the sake of being hip.  Some may argue that there has been a modernisation of tooling, like R over SAS, or Hadoop over Teradata.  This too is an abuse of language, upgrades in tooling do not justify reclassification of proffesions.

Now it is this difference, "productionisation and automation", that is key to the business value of Data Science.  Furthermore it is absalutely central to Agile methodologies.  With it we can close contentric feedback loops where the inner loops constitute logical tests, then evaluation frameworks, then user feedback.  Let's update the definition of a "Data Scientist" to make this central, and let's do away with the unicorn requirement for something more realistic - mulit-skilled teams:

**A Data Scientist is a member of a Data Science team, and a Data Science team pursues the productionisation and automation of analytics and machine learning to deliver business value.**

We will argue that adopting this definition will get a business to it's end goals significantly faster, with significantly higher quality results. Moreover the team members will be happier, more collaborative and eventually evolve into those unicorns over time.  Those focused on engineering practices can and will learn the subtlties of mathematics, and those focused on mathematics can and will learn the importance of clean code, tests, reliability, evaluation and clean archetecture.

It's a known fact that multiple specialized teams deliver much slower than a single cross functional team.  Returning to the web development analogy of my other posts: having a back-end team, a front-end team, a team of DBAs, a team of system administrators, and a team of designers, may seem logical to a factory manager from the industrial revolution, but in software development it's completely counter productive.  Rather a single team devoted to a single (or few) projects with all the skills necessary to deliver the project has been shown to be orders of magnitude faster.

Multiple specialised teams to deliver a single project grow the cost of communication geometrically, and communication in technology projects is nearly always the biggest bottleneck.  Furthermore putting many skills in a single team results in cross-fertilisation of skills through practices like pairing and code reviews.  I believe this sharing of skills results in exponential, or even super-exponential growth of the individuals.

### How to build Data Science Teams

The key roles you do need in a Data Science Team, which may or may not be shared by a single person are:

1. **Software Developer**: Role is to review code, ensure high quality code, ensure *DD is followed, and ensure proper work flow process is followed.
2. **Machine Learning Expert**: This role is most similar to what we call "Data Scientists", they must deeply understand the process of training, prediction and evaluation, and effective modelling techniques. They may be most used to scripting languages like R and Python, but must be willing to learn "production" languages like Scala and Java.
3. **Mathematician**: This role is emphasized distinctly from the above in that they ought to have a mathematics degree.  Mathematics, especially probability, has many subtleties and depth that mean misuse is commonplace through having only a superficial understanding.  The Mathematician must keep a watchful eye on evaluation techniques and model details.
4. **Big Data Engineer**: Role is to contribute skills regarding complexity theory, writting Map Reduce jobs, understand Big Data Databases, oversea ETL, ensure high quality engineering and ensure fast efficient pipelines.
5. Half of a **Big Data DevOps**: This role is the most understated yet important role in the entire Big Data era.  The logic is simple, no infrastructure, no Data Science.  The skills required to be a good Big Data DevOps are also the most understated - good DevOps can save you millions by correctly and efficiently configuring infrastructure. Now a whole DevOps employee per team may be excessive, I'd say as a rule of thumb you need one full DevOps for every eight Data Scientists, or every two teams.

Here importance, and definitely recruitment order, is 5 and 4 first - you need computers and data before you can do Data Science. If you employ some Machine Learning specialists or Mathematicians without first having some DevOps and Big Data Engineers in place, quite frankly this is stupid, that would be like hiring some roofers without hiring any brick layers when building a house.

Again, although listed seperately, the asymtotic goal of the team should be to share these skills around so that each can evolve into unicorns.

#### Pair Programming

Pair anyone with anyone, and pairing should be done a significant amount of time.  10% of time spent pairing is far too little, aim for more like 50%.  Do not fall into patterns of putting the same pairs together.  Ensure you do pairing properly by having two keyboards, good desks, swap drivers every 30 minutes, etc, etc, lookup good pairing practices online.

#### Code Review

Sometimes code reviews are not necessary for tasks completed by pairing since two pairs of eyes have seen the code.  Nevertheless if the pair was novice-novice, or when neither has software development skills, then reviewing is still necessary.  Code should be clean, having a Phd is no excuse for bad code not written with TDD. If you are smart enough to get a Phd, you are smart enough to realise you should be doing TDD.

Use some technology to assit, like crucible, JIRA, locking develop/master/integrations branches to devmasters, using PRs, pre-commit hooks, etc, etc.

#### Collaboration vs Context Switching

Keep projects to a minimum, try to focus.  Ideally the whole team should work together on MVP, then MLP, then when a project becomes a matter of iteration and maintenance assign less people.  If a single team is working on multiple projects then either that team will become divided and uncollaborative, or people will be constantly context switching to keep up with each project.

#### Some tools are counter to collaboration

The biggest problem of tooling in the Data Science world is over use of notebooks & interactive enviornments.  You should not be shipping code that was written in an editor that does not even support automatic variable renames, or automatic white space formatting.  Other posts will go into the details of work flow and how to correctly switch between notebooks like iScala and iPython, and corresponding IDEs, like Intellij and PyCharm, but here we just note that the latter are more condusive to collaboration that the former.  This is because tested well written code is considerably easier for those other than the author to understand.  If only the author of code can understand it, this will cause divides and isolations in the team.

Just like one does not want to go round a friends home if it's a tip, one doesn't want to work with a colleague's code that is a mess.

### Tests Over Documentation

Finally we need to note the importance of tests and TDD over documentation.  Data Science seems littered with over documented code, comments in code, notebooks that have entire paragraphs of text explaining what some code does.  In the software development world this was recognised as ridiculous over a decade ago - Data Science is quite epically behind.  Documentation is ambiguous and goes out of date.  Tests not only validate code but serve as unambiguous executable documentation that cannot go out of date.


...


### A Note On Language and Tooling

Always always **Choose the best tool for the job.** Invalid reasons to choose a tool: familiarity, convenience, low barrier to entry, easy to learn, etc.  Not only are they subjective by definition, they will be inconsistent across people, teams and companies, which will itself add overhead to overall efficiency.  The biggest problem of tooling in the Data Science world is over use of notebooks & interactive enviornments.  You should not be shipping code that was written in an editor that does not even support automatic variable renames, or automatic white space formatting.  Similarly the Data Science world has an overreliance on dynamically typed and interpreted languages.

Ease of use is inversley proportional to functionality and power. Only use the quick and dirty tools when you have a task that is quick and dirty, when things become larger and more complicated use a more powerful tool.

So time for a small digression on Scala.  I've found Scala, especially in conjunction with Spark, can have the best of all worlds.  One has notebooks: iScala, iSpark, Intellij-worksheets, shells: spark-shell, scala shell and IDEs: Intellij, Eclipse.  Scala is a statically typed functional programming language, what this means is when used correctly if your code compiles, it probably works!  By using Scala I have completly forgotten how to use a debugger and I rarely run my code to determine what it does, my code tends to work first time with the help of a bit of \*DD.  Yes Scala is harder to learn than dynamic languages like Python and R, and I claim it's because it's much more powerful.  Anyway, I could write a book on the advantages of Scala, so I'll stop here.

Now even though I love Scala I will use other tools when they are indeed the best for the job.  I'll wrap Python code that calls libraries that don't exist in Scala in TSV interfaces, I'll write bash scripts and even dip into Java for low level optimizations.  We should always use the best tool for the job, which so happens to usually be Scala.



### 

A2EM

Gianmario:

ACEM

MEAC

CEAM

CEMA

MECA


IVMECA

IVMECA

I don't like Computation or Driver - it's implementation details, and may not necessary. 

VAMP - Visualization & Analytics. Models, Mathematics & Productization.

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

1. Pairing slots should be put into the calendar.  Fridays should perhaps be avoided as such days often have demos, releases, retros, interviews, long lunchs, early finishes, etc.
2. Driver and navigator should switch every 30 minutes. Try to stick to this, this is quite important. Set a timer.
3. Take a 5 minutes break at switches (if necessary), sometimes it can make sense for the navigator to take a short break while the driver is deep in thought
4. Plug in two keyboards into the machine
5. Shortcuts should not be changed from the defaults. If someone insists on non-standard shortcuts, it's up to them to write two scripts to switch between their config and the default quickly.
6. Pairs should take it in turns between slots as to which desk to sit at (since the configuration of the desk will usually favour the desk owner).
7. Navigator must not have their own computer (or phone) near them
8. The driver should always be communicating, pairing is not one person codes and the other watches.
9. Not is it one person types and the other tells them what to type.  If the configuration is Novice-Expert Driver-Navigator resp, the navigator should try to use language that the driver understands, but not give them so much detail that the driver is just typing for the navigator.
