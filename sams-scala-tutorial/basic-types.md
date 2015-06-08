### Scala Basic Types - Primatives, Any, AnyRef, AnyVal, None, Nil, Unit, Nothing, and Null

Recommend first reading [Introduction to Typing](https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/introduction-to-typing.md)

#### Primative Types

Scala docs definition is "... not implemented as objects in the underlying host system", what this really means is that the instance of the type can be represented by a single **word**.  Non-primative types have to be represented by a sequence of words. Examples include `Int` and `Double`.

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

#### Unit

`Unit` is a bit like `Null` in that it has only a single instance, which can be written `Unit`, `()` or `{}`. It's companion object is also `Unit`, so it's type, it's companion object and it's instance are all written the same way which can be a little confusing.

`Unit` is handled slightly differently by the compiler in that if you annotate it as the return type of any method that method will compile no matter what your *last line* or *return statement* is.  You will get a warning if what you are returning is not actually `()` as what the compiler will do is essentially return `()` for you and ignore whatever you put.  Therefore it is convenient to use when you have a method or function where you do not what it to actually return anything (technically it is returning something, it's returning `()`).  Therefore the return type of methods like `println` is `Unit`. E.g.

```
def x: Unit = 3
// is sugar for
def x: Unit = {
  3
  ()
}
```
