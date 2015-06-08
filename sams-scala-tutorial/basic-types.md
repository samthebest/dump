### Scala Type Basics - Primatives, Any, AnyRef, AnyVal, None, Nil, Unit, Nothing, and Null

TODO Move this introduction to an introduction on types that talks about covariance and contravariance.

#### Introduction - (OPTIONAL)

It's assumed the reader has a basic understanding of types.  

Very roughly, every value has a type, when you write code you can annotate and ascribe types to values, or leave the compiler to infer type. Then the compiler checks your code to ensure assignments, method calls, annotations, ascriptions, etc have a consistent typing. E.g.

```scala
val x: Int = 10  // would compile
val y: Int = "hello"   // would not compile
```

#### Primative Types

Scala docs definition is "... not implemented as objects in the underlying host system", what this really means is that the instance of the type can be represented by a single **word**.  Non-primative types have to be represented by a sequence of words.

##### Word - (OPTIONAL Digression into Computer Science and Theory of Computation)

A word is a sequence of bits where the length is usually a power of two which can be handled by processors by a single "*head read*" / "*head write*", that is the 2^N possible values for that word constitute the alphabet of a [Turing Machine](http://en.wikipedia.org/wiki/Turing_machine). A [Turing Machine](http://en.wikipedia.org/wiki/Turing_machine) is the formal definition of a computer, and you must know it if you want to be a programmer.  Further reading [Word Wiki Page](http://en.wikipedia.org/wiki/Word_%28computer_architecture%29)

#### Any

`Any` is a supertype of everything.

#### AnyVal

`AnyVal` is the type of any class that has a single field (and extends `AnyVal`) or a **primative type**.

#### AnyRef

`AnyRef` is the type of any class that does not extend `AnyVal` and can have many fields.

#### Nothing

`Nothing` is a subtype of every type and nothing is an instance of `Nothing`, i.e. it has no instances.

Analogy: the empty set is a subset of every set but nothing is a member of the empty set

#### Null

`Null` is a subtype of every reference type, i.e. a subtype of anything that is a subtype of `AnyRef`. The only instance of `Null` is `null`, which one could say overrides every method with `throw new NullPointerException()`.

#### Nil

`Nil` is *the* empty list, but specifically with the type `List[Nothing]`, which if you think about it is pretty neat since there are no instances of `Nothing` hence it makes sense to have only one empty list and that empty list is of type `List[Nothing]`.  Consequently, via *covariance*, for any type `T`, `Nil` is a valid instance of `List[T]` since `Nothing` is a subtype of everything, so `List[Nothing]` is a subtype of every `List`.

#### None

`None` is exactly analogous for `Nil` except for `Option`s, that is the type of `None` is `Option[Nothing]` and `None` is *the empty* `Option`.

http://oldfashionedsoftware.com/2008/08/20/a-post-about-nothing/
http://blog.sanaulla.info/2009/07/12/nothingness/
