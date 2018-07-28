Platonic Programming (PP) is the future.

# Central Tenet of PP

There does not exist an objectively perfect (non-trivial) program, but for any pair of programs we can compare them objectively.

Therefore an objective of subjects is to refactor programs so that we make objective improvements.

# Objective Comparative Principles of PP

Given functionally equivilent programs

1. We favour programs with shorter ASTs
2. We favour programs with fewer non-referentially transparent expressions

# Theorem - State Monism

Given an infinitely fast processor and an infinite amount of memory, every program can be refactored to have at most 1 variable (i.e. `var` in Scala) while remaining functionally equivilant.  By the above principles we should favour these programs.

Theorem - 

Given two scopes S_1 and S_2 where S_2 is a subscope of S_1, moving a `var` from S_1 into only S_2 cannot increase the number of non-referntially transparent expressions.




2. We favour programs with fewer non-referentially transparent expressions
3. 
