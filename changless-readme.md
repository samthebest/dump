
## Introduction

Changless is a Scala framework that allows one to implement stateful applications without use of mutable structures, side effecting functions or vars.  Such applications would typically be RESTful APIs that interact with databases, but could in theory be used for any application (with one restriction*)

Changless is designed to provide abstractions that are sufficient yet minimal in order to interact with **the world**.

Changless differs from traditional approaches to handling the real world in Functional Programming, such as State and IO monads.  The key differences are


* The only restriction ...
