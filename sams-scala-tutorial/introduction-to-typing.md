
### Introduction

It's assumed the reader has a basic understanding of types from other languages.

Very roughly, every value has a type, when you write code you can annotate and ascribe types to values, or leave the compiler to infer type. Then the compiler checks your code to ensure assignments, method calls, annotations, ascriptions, etc have a consistent typing. E.g.

```scala
val x: Int = 10  // would compile
val y: Int = "hello"   // would not compile
```

When you introduce a class you introduce a type.  Classes can have type parameters, which are denoted with square brackets, consequently their type then becomes parameterized. E.g.

```scala
class A[T](val x: T)

val aInt: A[Int] = A(10)
```

#### Primative Types

Scala docs definition is "... not implemented as objects in the underlying host system", what this really means is that the instance of the type can be represented by a single **word**.  Non-primative types have to be represented by a sequence of words. Examples include `Int` and `Double`.

##### Word - (OPTIONAL Digression into Computer Science and Theory of Computation)

A word is a sequence of bits where the length is usually a power of two which can be handled by processors by a single "*head read*" / "*head write*", that is the 2^N possible values for that word constitute the alphabet of a [Turing Machine](http://en.wikipedia.org/wiki/Turing_machine). A [Turing Machine](http://en.wikipedia.org/wiki/Turing_machine) is the formal definition of a computer, and you must know it if you want to be a programmer.  Further reading [Word Wiki Page](http://en.wikipedia.org/wiki/Word_%28computer_architecture%29)

### Subtyping

If type `T` and `U` are not parameterized, then in general `T` is a subtype of `U`, written `T <: U` when `T` corresponds to a class that extends `U`, or extends a class that extends `U`, and so on for any chain of extends.

#### Covariance

When we declare a class to have type parameters, we can annotate that type to be covariant with a `+`, e.g. `class A[+T](val x: T)`. This means if `T <: U` then `A[T] <: A[U]`.

#### Contravarient

When we declare a class to have type parameters, we can annotate that type to be contravarient with a `-`, e.g. `class A[-T](val x: T)`. This means if `T <: U` then `A[U] <: A[T]` (notice it is the other round now).
