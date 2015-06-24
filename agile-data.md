## Agile Data Scientists Do Scale

Subtitle: Time to deliver!

Picture: Venn diagram with "Software Development Skills", "Maths, particularly Probability", "Conception of MVP + iteration" title "Agile Data"

"Software Development Skills", "Probability & Machine Learning", "MVP + Iteration" - intersection "Agile Data"

Due to the hype and rapid growth of Big Data Engineering and Data Science, it seems many companies and practitioners have gotten so excited by hiring, building infrastructure, fashionable models and shiny technology that one crucial part of the field seems to be missing - **delivery**.  I hear of countless stories where teams are built, clusters bought, prototype algorithms written and software is installed, but it then takes months or even longer to deliver working data driven applications, or for insights to be acted on.  It happens in startups and big corps alike, hype is thick in the air but delivery is thin on the ground.

The Harvard Business Review [Data Scientist's Don't Scale](https://hbr.org/2015/05/data-scientists-dont-scale) and related blogs correctly point out that we should focus on AI **applications**, that is automation. My addition is that these applications can not always be easily bought for many domains.  In such cases they should be built in-house and the builders ought to be **Agile** Big Data Engineers and Data Scientists that understand the importance of weekly or fortnightly iteration. The title of Data Scientist is not dead, but keeping Data Science alive means shifting the focus of the Data Scientist away from hacking, ad hoc analysis and prototyping and on to high quality code, automation, applications and Agile methodologies. Let's remember the technology industry has a habbit of finding ways to automate the job of those that lack the imagination to transition to automators, i.e. those that cannot be scaled.

A decade or two ago delivery used be a problem in the software development industry as a whole.  Releasing working software would also take months or even years.  Then came along Agile, XP, Scrum and TDD and for those correctly practicing it, it seemed to solve the problem and suddenly working software could be delivered every few weeks.

I'm going to write some posts on how to apply and adapt Agile methodlogies to Data Science and Big Data.  I doubt I'll cover all areas, but rather focus on the areas that seem most difficult to adapt for data and those areas that appear to be most overlooked.

Why Agile methodologies are so lacking in Data Science and Big Data is confusing.  Perhaps it's the age of the industry? To be frank I believe a smidgen of snobbism and elitism aim to distinguish the industries from *regular* software development as though the practices and principles are beneath the concerns of mighty data minds.  One other issue seems to be a big misconception that "exploratory" work precludes frequent iteration over automated end to end applications, that is Data Scientists claims they need to "explore" for a month or two before they can deliver. This I see as ironic since the tension between exploratory work and continuous delivery is exactly what Agile solves when deeply understood.  Finally another recurring misconception is that the day to day practices of Agile, like tests, automation, clean code and clean structure are "time consuming" and will slow down "exploratory" work.  This is also ironic since again Agile aims to make exploratory work *faster* and less labourious.  Hopefully the details of my posts will flesh out why these objections are misconceptions.

### Lost Agile Concepts

**Automatic tests** are absolutely critical in correctly practicing Agile [1], [2], and from TDD evolved more acronyms and terms than many Data Scientists have written tests; there is TDD, BDD, DDD, ATDD, SDD, EDD, CDD, unit tests, integrations tests, black box tests, end-to-end tests, systems tests, acceptence tests, property based tests, example based tests, functional tests, contract based tests, etc. At a glance things like interactive work, long running jobs, unclear objectives, peculiar development environments, etc preclude \*DD approaches, but by only slightly adapting the \*DD process we *can* accomodate such problems.  We can even apply \*DD principles to how we evaluate models to improve a sense of MVP.

The next most lacking parts in the data profession seems to be a lack of **high-quality code**, code structure and **cross-functional teams**, which go hand in hand.  Most definitions of Data Science actually include "hacking" as a skill.  If writing bad code is part of the definition of Data Science it's no wonder then that some believe the profession "can't scale".  Cross-functional teams and cross-functional team members have the obvious benefit of being able to deliver end to end working (data-driven) software.  In the data professions this means the team must be able to ETL, clean, prototype, collaborate **and productionize**.  Collaboration and productionization cannot happpen without high-quality code, which is why I consider the principles to go hand in hand.

The industry desperately needs to learn the lessons already learnt in web development about clean code structure that decouples seperate responsibilities. We should take inspiration from the MVC (Model View Controller) architectural pattern to clean up Data Science development.  We'll introduce A2EM (Ad Hoc Analysis To ETL & Evaluation, Models & Mathematics) that's primary goal is to decouple production code (EM) from ones ad hoc analytical enviornment via a simple pattern & process where two locations and two development tools (notebooks & IDEs) are used.

### Why Should Data Professionals Care?

Data Scientists and Big Data Engineers have a collective moral responsibility to the business and to the profession to be Agile and focus more on continuous delivery.  Ask yourself "do I deliver working (data driven & automated) output that has some business value, has some entry point independent of my own skills, knowledge & environment and do this every couple of weeks?". Any more than a month and that is too slow.  Deliver fast, and iterate, iterate, iterate, close the feedback loop as often as possible.

Now when executives start noticing lack of return on investment, slow delivery or scalability problems they will start hunting for a silver bullet, i.e. an easy to understand solution to a complicated problem.  Sometimes silver bullets do exist, particularly when the solution required is either not domain specific, or the domain is large enough to span many companies.  For example it would be insane for a company to try to build their own chat platform since this is obviously going to be a solved problem.  As platforms or out-sourcing solutions are required by less and less companies there comes a cut off point where in house solutions become more efficient.  It's a trade off between economies of scale and diseconomies of middle men and lack of specificity.

If we as data professionals fail to show ongoing business value in-house and keep saying "just one more month", then executives may turn to an external solution when such a solution might be completely inappropriate and fail miserably.  The reaction in software development to heal the divide between the business and developers was Agile.  Agile in-house teams would often be chosen over the solutions providers, consequently the businesses saved money, developers made money, and society overall became more efficient.

This is why data professionals should care.  It's also up to them to change the culture bottom up because Agile happens mainly at the bottom, in the details regarding processes and practices of writting code.  Agile is not just standing up for meetings, or having a retrospective every two weeks.  Executives don't write code so it's up to data professionals to read up on Agile and watch videos so you can know what it is in all it's fine details.  Applying the same principles to *data* may be challanging, and this is what my follow up posts explore.

My Posts:

LINKS again to follow up posts.

Agile Data - Clean Structure & Code

Agile Data - Dirty Data

Agile Data - Cross Functional Teams

Agile Data - *DD

Agile Data - Meaningful Evaluation

Random Links:

[Agile Manifesto](http://agilemanifesto.org/)

[(Hilarious) The Land That Scrum Forgot](https://www.youtube.com/watch?v=hG4LH6P8Syk)

[Clean Architecture and Design](https://www.youtube.com/watch?v=Nsjsiz2A9mg)

[(another) Clean Architecture](https://www.youtube.com/watch?v=Nltqi7ODZTM)

[Simple Design](http://www.jamesshore.com/Agile-Book/simple_design.html)

[Done Means Done](http://www.allaboutagile.com/agile-principle-7-done-means-done/)

[(another) Simple Design](http://guide.agilealliance.org/guide/simple-design.html)

[Proffesional Software Development](https://www.youtube.com/watch?v=zwtg7lIMUaQ)
