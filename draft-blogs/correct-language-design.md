How Java / Scala Compiler Should Have Been Designed

Java compiles down to bytecode and bytecode is used in dependencies.  This means code duplication is detected A) at the byte level, which means it's subject to cosmetic changes and B) we have binary incompatibility for changing of compiler versions.  

Shading is complicated, and requires additional effort to do it, it could also cause HUGE files.

There should be an abstract representation layer.  This means any code that is essentially the same, i.e. only differs cosmetically, (e.g. field declaration order, variable renames, moving methods around, adding/removing brackets) could result in a different binary. This is why building Scala projects is painful because different versions of libraries that are possibly nearly identical for your needs (you use 3 methods and those have not changed) means you can't have this version and another version.

Scala can solve this problem partially with a "staging" layer.

Versioning is at the project level, not at the expresssion level.  This is likely a throw back from OOP and mutation.  If a codebase uses pure functional programming for most of the codebase, then each expression can be treated entirely in isolation.

You have a dependency on a binary - a jar, not on a commit hash.  One should not depend on a compiled code, one should depend on a library git location together with a commit hash.  This means expensive compilation of huge amounts of code, and also might not work so well for closed source projects.

Scala cannot solve this problem because we will likely still depend on maven repos. It can't solve this problem because compilation is local, so slow.

Only way to solve this problem is have compilation happen in the cloud and cache the ASTs.


No git, no jenkins, no deployment, no dependency management, no environments.  Code will compile to systems level and run on bare metal on the cloud - interfacing will be via REST APIs.
