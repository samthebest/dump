
I think the key to making Data Science Agile is to first understand how trad Software Development used to look nearly exactly like Data Science today.  That is projects would be planned to take months, or even a year, and would be broken down into a “research” step and then an “implementation” step, then a “testing” step.  First computer scientists would go to meetings, plan software, research technologies, run ad hoc performance experiments, draw architecture diagrams, produce charts and plots, read papers, do seminars, etc.

The revolution happened in understanding that each “exploratory” step is itself a deliverable that can be automated and give business value, one does not need the end monolithic product with full functionality to deliver business value.  

In Data Science there are many steps that if automated would deliver business value, yet do not constitute the end “monolithic high-accuracy model” that is usually the ultimate goal.  For example small experiments, ETLs, data cleaning steps, statistics, reports, etc that aim to understand and pre-process the data.  These things can be done bit by bit, they can be automated and have entry points, they can be tested and even generated in a test-first TDD style.  In fact I would argue that making a prediction before running an experiment is the core principle of the Scientific Method that is a) often lacking in Data Science and b) also a core principle of Agile.

For example, in the “research” stage a Data Scientist may want to query some data and produce some simple stats to start looking into the feasibility of a model.  The waterfall approach would be:

1. Write the query directly into some GUI, with little attention to readability or performance
2. Look at the resulting statistics, which upon first iteration may seem odd, one realises a bug exists in the query, so they go back change the query and re-run the query
3. This becomes tedious, so they put in a sample statement into the query so it runs faster. The history of query is not tracked, and crude tools which are only slightly more sophisticated than text editors are used.
4. Eventually the statistics look believable, so maybe the query is correct
5. Take the stats and put them into a word document, perhaps copy and paste the query into a text file on a local laptop (which will get lost).
6. Pass the document to your boss, along with natural language report expressing that you do or do not believe the model is feasible

Firstly, this kind of work is not really Data Science, we already had a word for this kind of work for a long time - it’s Analytics, even if the data is in Hadoop (which it should be in order to do Agile Data Science).

Now I would see the Agile approach would be:

1. Write an acceptance test that takes as inputs some statistics and checks these satisfy some criteria that would mean we can move on to next steps.  
2. Use an IDE to write the test.  Commit the code into a git repository. Push it and ask all your colleagues (not just your boss), to review the acceptance criteria. In fact any member of the organisation can view this unambiguous executable criteria using the browser for the repository. Not just your boss, not just your team.  If the criteria changes there is a version history.  One could even setup notifications in Slack or HipChat.
3. Use TDD to implement the logic that will calculate the statistics, where the data is mocked to hit all the edge cases.
4. Write an entry point that reads the data from HDFS, feeds it into the thing that calculates the statistics, then runs the acceptance tests and logs the results to text files.
5. Hook the entry point to a Jenkins job, so now at the click of a button anyone can run the “query” and see if the acceptance tests pass or fail.

So let’s look at some of the key differences:

 - The Agile version is considerably more collaborative.  All the code ought to be reviewed, and colleagues ought to give their input.
 - The Agile version is ready to react to all kinds of changes.  If the data changes, just click the button, if the requirements change, tweak the acceptance criteria.  If we want more statistics, we can easily add some while knowing we won’t break the existing ones.
 - No one has genuine confidence in the waterfall version if the criteria is valid, if the query is valid or if the statistics are valid.
 - If anything changes in the waterfall version, the whole tedious process needs to be repeated and is just as prone to human error as the first time.  Furthermore there is no neat version control / history.  Mistakes go unnoticed only until they cost the business 1000s four months down the line, at which point no one has any clue who made the mistake or where it lies.
 - The Agile version replaces documentation and reports with executable unambiguous code in some ubiquitous language.

One final apparent difference is time, surely the Agile version takes longer?  Wasn’t the whole point of Agile to be faster?  Well, actually it is faster once you have got used to using the tools and provided you have a good DevOps in your Data Science team.  Once one has got used to using testing frameworks, IDEs, Jenkins, etc it’s just as fast as the ad hoc version.  Slightly more time will be needed to think about the problem in order to phrase it rigorously, but that overhead is quickly repaid in the time saved debugging.

Extending this further, suppose it known that if something about the input changes the model will break due some assumptions about the data, suppose the statistics are pushed to some dashboard.  Now when something in the external world changes, or a DBA makes a mistake, or an engineer breaks an input stream, we will know instantly that the model will break and why.

Now think what is the business value of the one or two extra hours of a Data Scientists time to do it this way? (which is an exaggeration if they are used to the process).  Now think what the business value is of: 
 - having an automated system monitoring change
 - having IP, reports and insights easily accessible to the entire organisation
 - driving decisions from code that is virtually guaranteed to have no mistakes and has been motivated by many pairs of eyes
 - having an audit trail of every grain of the process
 - being able to change any grain of that process easily and know it still works
 - having all of the above completely independently of any single employee - any employee is now free to get run over by bus with practically zero loss to the business

If I was a CEO, I’m pretty sure I’d be happy to pay for that small initial overhead.
