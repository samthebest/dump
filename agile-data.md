## Agile Data and *DD

Thanks to the hype and rapid growth of Big Data Engineering and Data Science it seems most companies and practioners have got so excited by hiring, building infrastructure, big clever sounding words and shiny clever sounding technology that one crucial part of the fields seems to be missing - *delivery*.  I hear of countless stories where teams are built, clusters are bought, algorithms are used and software is installed, but it then takes months or even longer to deliver working data driven applications.  It happens in startups and big corps alike, hype is thick in the air but delivery is thin on the ground.

A decade or two ago delivery used be a problem in the software development industry as a whole.  Releasing working software would also take months and even years.  Then came along Agile, XP and TDD and for those correctly practicing it it seemed to solve the problem and suddenly working software was delivered every few weeks.  Automatic tests are absalutely critical in correctly practicing Agile [1], [2], and from TDD evolved more acronyms and terms than many Data Scientists have written automatic tests; there is TDD, BDD, DDD, ATDD, SDD, EDD, unit tests, integrations tests, black box tests, end-to-end tests, systems tests, acceptence tests, property based tests, example based tests, etc.

This post will not labour over the subtle differences between the annoying number of terms, which will henceforth be collectively referred to as *DD, rather we will focus on how to apply the principles they each have in common to a field that at a glance precludes using them.  *DD has worked well in web development but it seems difficult to apply it to data driven fields.

### Problems of *DD in Data Science and Big Data

#### Data Science

How does one write an "automatic test" for classification problem? What is the definition of passing? When we build models we run it, look at the ROC and some evaluation measures.  If the performance seems unbelievably good, we go back and review our features, or our cross validation process, to make sure our model is not cheating.  If the performance seems bad, we go back and iterate on the model a little more.  When the numbers seem reasonable we ship it.

Similarly when we play around with clustering algorithms the line that defines done seems to be quite fuzzy.  We look at charts, we add more colours, we look at the clusters, and when it seems kinda reasonable we stop and consider the task done.

We also have a habit of postponing automation, of say ETL, or running the models, or evaluating the models.  That isn't prototyping, that's productionization, and we will save that for later or get someone else to do it. We say to ourselves "we don't have an awesome model yet, why would I put any effort into productionization?".

The process is interactive and iterative, which is good, but it is also quite manual, which is bad.  I will argue that these "problems" and arguments against automation are not unique to Data Science. Lack of automation is driven by a lack of imagination, clarity of objectives and focus on delivery.

#### Big Data



### The Core of *DD Practices

1. Clearly defining the use case in such simple and unambiguous terms so that success can be defined formally and even executed by a machine
2. Doing 1. *before* trying to solve the problem

### How to Apply *DD

[1] - https://www.youtube.com/watch?v=hG4LH6P8Syk
[2]  - http://en.wikipedia.org/wiki/Agile_software_development#Agile_practices
