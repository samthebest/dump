### Style

Please please follow the style guide, http://docs.scala-lang.org/style/, pull requests with bad style will be rejected.

#### Formatting

Please use an auto-reformatter, the out-of-box Intellij one is very good.  We have some slight differences to the settings which are:

1. minimum blank lines should be 1 for 'after package', all other minimum blank line values should be 0,
2. All the fields under 'maximum blank lines' should be 1, 
3. due to an ancient and still unfixed bug in Intellij please check "Align when multiline" for "Chained method calls" and put chains on a new line. This is how code looks before this rule:

```
  val myList = List(1, 2, 3)
    .map {
    case x => x
  }
    .map {
    case x => x
  }
```

and this is how it looks afterwards:

```
  val myList =
    List(1, 2, 3)
    .map {
      case x => x
    }
    .map {
      case x => x
    }
```

Clearly much nicer.

#### Braces

Please refrain from using curly braces wherever one can use round braces.  There is no need to seperate code over multiple lines for trivial one-line lambda expressions.

### Comments

Please only use comments in exceptional circumstance, your code should be self-documenting, which means:

1. Code should have good names and be seperated into different methods so that lines are not too long.  With good names and use of native scala and other libraries, it should be possible to write code where the business logic is clearly readable in the code.
2. Use unit tests to document that which is hard to make clear in the code. Of course you should write unit tests anyway, but when you think "this method is a bit complicated and rather than have a really long name for it I could write a comment" replace "comment" with "unit test"

The exceptions to this rule are TODOs, FIXMEs and a comment that explains a particular line of code is a workaround for an underlying bug in Scala or a library.  For example suppose you discover method `libraryCall1` has a bug in it, and you have to implement a workaround `horrible line of code that basically does libraryCall1`, then it's justifying to write a comment since you don't want someone to come along and refactor that line of code into `libraryCall1` as it will break things.

#### "TODO" Comments

Of course we should try to write super fast elegant unit-tested code the first time round, but sometimes we just need to get something working fast.  In this situation please use a TODO comment, don't just leave the code in a substandard state. Also please try to use our custom additional identifiers that identify what the TODO is quickly. They are roughly in order to importance

1. FUNCTIONALITY: Means this TODO is necessary for certain functionality to exist and may impact user experience. E.g. some function needs to check for valid input or user will just get a 200 rather than a meaningful message.
2. UNHACK: Means this is a hacky way of doing it, it could be done a proper way (e.g. using a Regex instead of a parser, hardcoding some magic number rather than putting it in a config file, etc)
3. UNIT TEST: Means this method/class/etc should have unit tests, but they haven't been written straight away.  This is acceptable when code is quite simple and obviously works, in this situation unit tests would only add documentational value and refactorability value.  When code is non-trivial always write some unit tests.
4. SPEED: Means this code could be made to run faster and may impact user experience, but isn't a priority right now.
5. DRY: Means this code is wet, it needs drying.

These tags are a rule of thumb and not to be taken too seriously, if you add a TODO that is really just a general tidy up, it might not fall into any of the above buckets.  It's important to tag TODOs when it's clear that it really does fall into one of the above buckets.

#### Commented code

Again, this should only happen in exceptional circumstances.  For example suppose you write a unit test, then discover it takes 10 seconds to run due to a bug in an underlying library that makes it super slow.  In this situation it's justifyable to comment out the unit test and leave a comment explaining why e.g. "uncomment this code when we swap out library for something faster".

### Functional Programming

Please write in a functional style, avoid `var`s, avoid mutable types, avoid control structures and loops, etc.

#### Type Annotation

Annotate type on everything except for literals and where a large group of methods all next to each other return the same type.  Check "No tail recursion annotation" under "Inspections" in Intellij in order to avoid accidental infinite loops (set severity to error).
