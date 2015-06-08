### Scala Type Basics - Primatives, Any, AnyRef, AnyVal, None, Nil, Unit, Nothing, and Null

#### Primative Types

Scala docs definition is "... not implemented as objects in the underlying host system", what this really means is that the instance of the type can be represented by a single **word**.  

##### Word - (OPTIONAL Digression into computer science and theory computation)

A word is a sequence of bits where the length is usually a power of two which can be handled by processors by a single "*head read*" / "*head write*", that is the 2^N possible values for that word constitute the alphabet of a [[Turing Machine|http://en.wikipedia.org/wiki/Turing_machine]]. A [[Turing Machine|http://en.wikipedia.org/wiki/Turing_machine]] is the formal definition of a computer, and you must know it if you want to be a programmer.  Further reading [[Word Wiki Page|http://en.wikipedia.org/wiki/Word_%28computer_architecture%29]]

#### Any

`Any` is a supertype of anything.  

#### AnyVal

`AnyVal` is any class that has a single field or a *primative type*.  

and that field is either an `AnyVal` or is 

`Nothing` is a subtype of every type and nothing is an instance of `Nothing`

Analogy: the empty set is a subset of every set but nothing is a member of the empty set

`Null` is a subtype of every reference type, the only instance of `Null` is `null`, which one could say overrides every method with `throw new NullPointerException()`.



http://oldfashionedsoftware.com/2008/08/20/a-post-about-nothing/
http://blog.sanaulla.info/2009/07/12/nothingness/
