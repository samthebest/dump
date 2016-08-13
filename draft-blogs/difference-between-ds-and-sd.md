I claim that the difference between Data Science and Software Development can be succinctly captured by the difference between < and = respectively. That is Data Science (DS) corresponds to the Order relation, while Software Development (SD) corresponds to the Equality relation.

In DS success is defined by a function satisfying a certain threshold, while in SD success is defined by a function satisfying an equality.  For example a Fraud Detection algorithm may require that *less than* 100 frauds are missed a year, while a sorting algorithm requires that the resulting list *is precisely* the sorted list.

Furthermore I claim there really is nothing else to it. All beliefs that DS is more than "just checking f(x) > y" or that SD is more than "just checking f(x) = y", is in fact lack of clarity of thought regarding the objective.

Lack of clarity of objective is the number one cause of delivery failure in both DS and SD. When a problem is clearly defined, the problem tends to solve itself - rather quickly!  Therefore anyone can be both good at DS and SD, for the real skill is writing down your problem in terms of a single simple symbol.

All other differences are detail and detail can be Googled.  What's the fastest algorithm for lexicographical sorting for uniformly distrubted random strings? Google it.  What's the best Neural Net configuration for recognising speech? Google it. At the end of the day you are probably wiring and composing together a bunch of libraries, tools and frameworks that is mostly already built.  This is true for both DS and SD.

Now what is it that I am actually trying to do? Hmm, can't Google that, will actually have to think about it!

The building of something in either DS or SD is the easy part, then by The Law of Triviality this is what people focus on before even focusing on clarifying their objectives.  The result is a lot of Rube Goldberg machines in both industries, that is very complicated contraptions that greatly obfuscate a very simple purpose, mainly because the creator didn't put enough effort into understanding what the purpose was prior to building the contraption.

In spite of my claims, DS and SD may still *appear* to be quite different, now that I will argue is just historical and methodological.  SD has a longer history, and so longer for people to realise the importance of clarity of objective, and the automation of testing against this objective.  Not that this means SD is always successful; there are still a great number of developers who don't write tests before their main code.

Ask a Software Developer "do you do TDD?" and they might say "yeah I know what that is, but I'm too lazy to think clearly so I keep making these complicated messes instead", but ask a Data Scientist "do you do TDD?" and they will say "What's TDD? Thinking clearly about what I'm trying to do? Pfft. I have a Phd, I don't need to do that. Look at this wonderfully complicated mess I made".

In both cases the "proffesional" keeps getting paid, not because they have delivered an automated self documented elegant masterpiece, but because their half working complicated contraption requires their constant involvement.  What's the solution? Company polices? Laws? I have no idea, but at least through ranting raving flaming and arguing we can raise awareness.  Maybe one day TDD in DS and SD will be as habitual as surgeons washing their hands.

