## Agile Data and *DD

Thanks to the hype and rapid growth of Big Data Engineering and Data Science it seems most companies and practioners have got so excited by hiring, building infrastructure, clever sounding words and shiny technology that one crucial part of the fields seems to be missing - **delivery**.  I hear of countless stories where teams are built, clusters are bought, algorithms are used and software is installed, but it then takes months or even longer to deliver working data driven applications.  It happens in startups and big corps alike, hype is thick in the air but delivery is thin on the ground.

A decade or two ago delivery used be a problem in the software development industry as a whole.  Releasing working software would also take months and even years.  Then came along Agile, XP and TDD and for those correctly practicing it it seemed to solve the problem and suddenly working software was delivered every few weeks.  Automatic tests are absalutely critical in correctly practicing Agile [1], [2], and from TDD evolved more acronyms and terms than many Data Scientists have written automatic tests; there is TDD, BDD, DDD, ATDD, SDD, EDD, unit tests, integrations tests, black box tests, end-to-end tests, systems tests, acceptence tests, property based tests, example based tests, etc.

This post will not labour over the subtle differences between the annoying number of terms, which will henceforth be collectively referred to as *DD, rather we will focus on how to apply the principles they each have in common to a field that at a glance precludes using them.  *DD has worked well in web development but it seems difficult to apply it to data driven fields.

### "Problems" of *DD in Data Science and Big Data

#### Data Science

*Evaluation*: How does one write an "automatic test" for classification problem? What is the definition of passing? When we build models we run it, look at the ROC and some evaluation measures.  If the performance seems unbelievably good, we go back and review our features, or our cross validation process, to make sure our model is not cheating.  If the performance seems bad, we go back and iterate on the model a little more.  When the numbers seem reasonable we ship it.

*Model Exploration*: Similarly when we play around with clustering algorithms the line that defines done seems to be quite fuzzy.  We look at charts, we add more colours, we look at the clusters, and when it seems kinda reasonable we stop and consider the task done.

*Data Exploration*: We also have a habit of postponing automation, of say ETL, or running the models, or evaluating the models.  That isn't prototyping, that's productionization, and we will save that for later or get someone else to do it. We say to ourselves "we don't have an awesome model yet, why would I put any effort into productionization?".

The process is interactive and iterative, which is good, but it is also quite manual, which is bad.

#### Big Data

*Long Running Jobs*: How can a test-cycle work for something that takes 5 hours to run? Aren't tests supposed to run in a few seconds?

*Computational Resource Problems*: How can we write a test to catch out of memory errors? Or out of disk space errors?

### The Core Principle of *DD Practices

The "problems" we use to justify not writting automated tests in the data world are not problems with the practice, but the mindset.  Lack of automation in *any* type of software development is driven by:

 - Fear of delivering something that isn't perfect and awesome
 - lack of imagination on how to test
 - lack of clarity of objectives
 - a natural propensity to focus on the fun and clever stuff, not on delivery

Now communicating the core *DD principle can solve this problem of mindset, but over the years the core has been buried under superfluous aspects, such as frameworks, tools, ubiquitis languages, speed, and over emphasis on writting test code before writting main code.

So *the core principle of *DD practices* is just:

1. Defining the use case in such simple and unambiguous terms so that success can be defined formally and even executed by a machine
2. Doing 1. *before* trying to solve the problem



### How to Apply *DD

[1] - https://www.youtube.com/watch?v=hG4LH6P8Syk
[2] - http://en.wikipedia.org/wiki/Agile_software_development#Agile_practices
