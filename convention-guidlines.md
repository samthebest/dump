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

The exceptions to this rule are TODOs, FIXMEs and a comment that explains a particular line of code is a workaround for an underlying bug in Scala or a library.  For example suppose you discover method `libraryCall1` has a bug in it, and you have to implement a workaround `horrible line of code that basically does libraryCall1`, then it's justified to write a comment since you don't want someone to come along and refactor that line of code into `libraryCall1` as it will break things.  Similarly sometimes weird low level optimizations need a comment, like using mutability or vars, a comment might be need to justify using a non-pure functional idiom.

### Functional Programming

Please write in a functional style, avoid `var`s, avoid mutable types, avoid control structures and loops, etc.

#### Type Annotation

Annotate type on everything except for literals, obvious types in local scope, and where a large group of methods all next to each other return the same type.  Check "No tail recursion annotation" under "Inspections" in Intellij in order to avoid accidental infinite loops (set severity to error).
