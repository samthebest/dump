## Agile Data Scientists Do Scale

Subtitle: Time to deliver!

Picture: Venn diagram with "Software Development Skills", "Maths, particularly Probability", "Conception of MVP + iteration" title "Agile Data"

Thanks to the hype and rapid growth of Big Data Engineering and Data Science it seems most companies and practioners have got so excited by hiring, building infrastructure, clever sounding words and shiny technology that one crucial part of the fields seems to be missing - **delivery**.  I hear of countless stories where teams are built, clusters are bought, prototype algorithms written and software is installed, but it then takes months or even longer to deliver working data driven applications, or for insights to be acted on.  It happens in startups and big corps alike, hype is thick in the air but delivery is thin on the ground.

The Harvard Business Review [Data Scientist's Don't Scale](https://hbr.org/2015/05/data-scientists-dont-scale) and related blogs correctly point out that we should focus on AI **applications**, that is automation. My addition is that these applications can not be easily bought in for most domains, rather they should be built in-house and the builders ought to be **Agile** Data Scientists and Big Data Engineers. The title of Data Scientist is not dead, but keeping Data Science alive means shifting the focus of the Data Scientist away from hacking, ad hoc analysis and prototyping and on to high quality code, automation, applications and Agile methodologies.

Those Data Scientists that do not adapt into the role of the automator will be automated.

A decade or two ago delivery used be a problem in the software development industry as a whole.  Releasing working software would also take months and even years.  Then came along Agile, XP, Scrum and TDD and for those correctly practicing it it seemed to solve the problem and suddenly working software was delivered every few weeks.

I have written two posts on how to apply Agile methodlogies in Data Science and Big Data. The two areas I have written about were chosen because they are critical in correctly practicing Agile while being the most overlooked and misunderstood practices in the data proffesions.

**Automatic tests** are absalutely critical in correctly practicing Agile [1], [2], and from TDD evolved more acronyms and terms than many Data Scientists have written tests; there is TDD, BDD, DDD, ATDD, SDD, EDD, CDD, unit tests, integrations tests, black box tests, end-to-end tests, systems tests, acceptence tests, property based tests, example based tests, functional tests, etc. So I wrote: (LINK)

The second most lacking part in the data proffesions seems to be a lack of **high-quality code** and **cross-functional teams**.  Most definitions of Data Science actually include "hacking" as a skill.  If writting crap code is part of the definition of Data Science it's no wonder then that the proffesion is failing to deliver.  Cross-functional teams and cross-functional team members have the obvious benifit of being able to deliver end to end working (data-driven) software.  In the data proffesions this means the team must be able to ETL, clean, prototype, collaborate **and productionize**.  Collaboration and productionization cannot happpen without high-quality code, which is why I consider the two principles to go hand in hand. So I wrote: (LINK)

Given sufficient interest I may write a few more posts on applying Agile principles to archetecutre, work flow and even modelling.

### Why Should Data Proffesionals Care?

Data Scientists and Big Data Engineers have a collective moral responsibility to the business and to the proffession to be Agile and focus more on delivery and less on having fun with the latest tech/algorithms.  Ask yourself "do I deliver working (data driven & automated) output that has some business value, has some entry point loosely coupled to my own skills & knowledge and do this every couple of weeks?". Any more than a month and you have been paid a salary in exchange for a promise. Furthermore if you get sick, die or leave prior to that delivery will a colleague be able to immediately pick up from where you left off? Documentation (or comments) at a technical level are largely useless and do not count, at a business or mathematical level it can have some value but won't explain to anyone how your code or model works. It's a known fact amoungst Agile proponents that there are only two things that truly document your code: tests and your code.  If you write hacky dirty code and you have no automatic tests then your code is not documented.

Now when executives start noticing lack of return on investment they will start hunting for a silver bullet, i.e. an easy to understand solution to a complicated problem.  Sometimes silver bullets do exist, particularly when the solution required either is not domain specific, or the domain is large enough to span many companies.  For example it would be insane for a company to try to build their own chat platform since this is obviously going to be a solved problem.  As platforms or out-sourcing solutions are required by less and less companies there comes a cut off point where in house solutions become more efficient.  It's a trade off between economies of scale and diseconomies of middle men and lack of specificity.

If we as data proffesionals fail to show ongoing business value in house and keep saying "just one more month", then executives may turn to an external solution when such a solution might be completely inappropriate and fail miserably.

**But** there is hope.  It has happened in many companies since the early 2000s in software development - teams became Agile.  Agile in-house teams would often be chosen over the solutions providers, consequently the businesses saved money, developers made money, and society overall became more efficient.

This is why you should care.  It's also up to you to change the culture bottom up because Agile happens mainly at the bottom.  All the executives can do is point and shout "hey do Agile, I don't know what it is, but do it!", so it's up to you to read and watch videos so you can know what it is.  Applying the same principles to *data* may be challanging, and this is what my follow up posts explore.

My Posts:

LINKS again to follow up posts.

Random Links:

[Agile Manifesto](http://agilemanifesto.org/)

[(Hilarious) The Land That Scrum Forgot](https://www.youtube.com/watch?v=hG4LH6P8Syk)

[Clean Architecture and Design](https://www.youtube.com/watch?v=Nsjsiz2A9mg)

[(another) Clean Architecture](https://www.youtube.com/watch?v=Nltqi7ODZTM)

[Simple Design](http://www.jamesshore.com/Agile-Book/simple_design.html)

[Done Means Done](http://www.allaboutagile.com/agile-principle-7-done-means-done/)

[(another) Simple Design](http://guide.agilealliance.org/guide/simple-design.html)

[Proffesional Software Development](https://www.youtube.com/watch?v=zwtg7lIMUaQ)
