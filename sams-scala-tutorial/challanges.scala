
Given a list `l` of `Int`s, that is `val l: List[Int]`, where it is known that exactly one number in the list occurs an odd number of times, write an expression that's value will be the number that occurs an odd number of times.























// What does this line of code do?
def e(s: String): List[Int] = 
  s.grouped(7).toList.take(5).map(Integer.parseInt(_, 16) % 50) ++ s.grouped(2).toList.take(2).map(Integer.parseInt(_, 16) % 9)

def l(s: String): List[Int] = 
  s.grouped(7).toList.take(6).map(Integer.parseInt(_, 16) % 60)

// How to solve the RAID problem in 11 chars:

(0/:l)(_^_)

val l: List[List[Int]] = ???
// What does this line of code do?
(List(Nil:List[Int])/:l)(_|@|_|>(_(_:+_)))


## Full Stack Dev Test:


Write an app in Scala that tails a file on the local filesystem and writes it to S3.

In the interest of saving time you may leave some things unfinished and communicate those things.  The point of the exercise is *not* to provide a perfect working solution or to meet a long list of expectations, but to express what you believe is important.  Nevertheless if the following list is not immediately obvious to you as a bare minimum, this role will not be right for you.

 - Tests
 - Code provided as a link to a public repository version controlled in `git`
 - Installation scripts for any tools or dependencies.  E.g. if you use `sbt` or `docker` say, you must provide a bash script to install (and uninstall!) these.  The script must be (a) non-interactive, (b) idempotent, (c) work on Mac & Linux (both deb & rpm based distros)).  Simply a line of documentation saying, say "install `docker`", will not be acceptable.


## Data Scientist Test:


Please complete the following take home coding test.  You can use any language you like.  You may be asked about your solution(s) in the interview.  We expect the test to take not more than a couple of hours, but you are free to spend as long as you wish.

The solution must be a link to a self contained repo, that means:

 - Provide a link to a public repository version controlled in git
 - Include installation scripts for any tools or dependencies.  E.g. if you use `python` or `docker` say, you must provide a bash script to install (and uninstall!) these.  The script must be (a) non-interactive, (b) idempotent, (c) work on Mac & Linux (both deb & rpm based distros)).  Simply a line of documentation saying, say "install python version 3.2", will not be acceptable.
 - Include a README.md file detailing how to run the unit tests.

We value applying the Scientific Process to writing code, please think carefully about what this means.  Is your code _scientifically_ written?  You will be asked about this in the interview.

### Problem:

Write a function that will be given a list of integers as a parameter.  It is known that exactly one number in the list occurs an odd number of times. The function should return that number.

Optional Bonus:

 - As a comment above the function state the space and time complexity in big O notation (bonus++ in LaTeX).
 - Provide more than one implementation that differs in it's space/time complexity.



