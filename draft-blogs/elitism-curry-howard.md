
Elitism in Data Science, The Curry-Howard Isomorphism and a Great Link

Apologies in advance to those who have stumbled across this as it's my most discursive narrative-lacking post to date.

This morning I read this: http://www.datasciencecentral.com/profiles/blogs/10-worst-mistakes-for-data-scientists - (it's actual title is "22 easy-to-fix worst mistakes for data scientists"). It's short, concise, content rich and accurate, one of the best articles on Data Science I have read (I have not followed the links yet).

The author claims they are in random order, but I think the first two "1. Not being able to work well in a team 2. Being elitist" are causal of many of the others.  I see reluctance in the field motivated especially by "2. Elitism" to defer "engineering", "development" or "Agile" practices to some other team or person, like 

reusing code "8."
KISS "17."
unit tests (TDD) "5."
MVP focus "16."
ATDD "19."
automation "22."
organised to-do boards "6."

Now a post from Twitter https://medium.com/@rchang/my-two-year-journey-as-a-data-scientist-at-twitter-f0c13298aee6, and general consensus is that Data Scientists come from one of two directions, Maths & Machine Learning or Engineering & Computer Science.  Since most people only have one undergrad, it's safe to assume most people come from a single one of these directions. So when I discuss the great importance I place on the above practices people naturally assume that I'm baised and originate from the Engineering & Computer Science direction.  But this is incorrect.

In fact you won't find a more ivory tower theoretic academic education.  I didn't write a single line of code at university, and our only treatment of computers was through the formal mathematical definition of a Turing Machine!  Rather I claim that I slowly, and painfully, came to value the aforementioned practices through reason and self-experimentation of my own productivity.

Though I've come to see that the intellectual challanges of doing mathematics and writting proofs, in particular doing it well, is the same as writting good programs.  There is a theorem called the Curry-Howard Isomorphism that shows that proofs and programs are equivilent in some sense, but I'm not going to focus on the dry formalities here. Rather I feel a real analogy between them that when explained may dispell "Elitist" myths that these "Engineering" practices are "beneath Data Science", "easy" or "should be deferred to another team".

The analogy all comes to the "objective function" I had to work towards at university - get good grades in maths exams.  Proffessors constantly moaned about sloppy mathematical style, and how important clarity of writting was in exams. The exam paper itself explicitly highlighted the importance of style and clarity.  It was not enough to have in some form all the necessary steps of a proof dumped haphazardly onto the paper. The examiner has limited time and patience to digest it and so it had to communicate the essence clearly, simply and concisely.  Secondly, there was no extra marks for any maths over and above the problem stated.  Thirdly the "stakeholder", i.e. the exam author, clearly stated the problem to be solved prior to any work being done by the candidate - all exploratory work was towards a well defined goal.

In Data Science we have some clear analogies: 

1. It is not enough for the code to "work", "but it work" is not an excuse for bad code
2. One must evaluate ones work according to some business measures defined prior to work beginning (ATDD).  Business people don't care about AUC, they care about profit, cost savings, etc. Don't use "3. jargon that stakeholders don't understand". Don't do Data Science *for the sake of it*, you need a well defined problem and means to evaluate success.
3. No extra marks is like the third law of TDD "You are not allowed to write any more production code than is sufficient to pass the one failing unit test"

The delivery of mathematical content supports the analogy further:

4. The deconstruction of maths into Lemmas, Theorems and Corollaries corresponds to the organisation and design of code into methods and APIs.
5. Theorems are always stated prior to proofs, this corresponds to the first law of TDD "You are not allowed to write any production code unless it is to make a failing unit test pass"

Summary

The mental acrobatics required to decompose problems in mathematics is analogous to clean code design.  Clarity of thought and writting -- TDD, ATDD and clean code

Conclusion

Don't be elitist, give Software Engineering practices the respect they deserve, being good at them is just as hard as being good at maths/stats/machine learning.


Clarity of thought and of mathematical writting was essential to doing well in exams, the lecturers .  


So the value attributed to the above ought to correspond to a natural bias depending on which of these directions the practitioner originates.  Therefore 







