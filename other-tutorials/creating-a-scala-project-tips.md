Unfortunately learning to create Scala projects is not as easy as other languages, but at least you only need to learn this once.


1. Do NOT use the Intellij "create project" setup wizard.
2. Do NOT try to read the official SBT documentation, it's very verbose.
3. Install sbt, on a mac this is probably `brew install sbt` (assuming you have brew), on debian based linux this will be `apt-get install sbt`, rpm based linux `yum install sbt`.
4. Instead find a existing project on the web that uses a lot of the libraries you intend on using. It seems searching for `library1 ... libraryN build.sbt` tends to give a long list of github pages with example projects that use libraries 1 - N.
5. `cp --parents` the `project/plugins.sbt` file and `build.sbt` file.
6. Reccomend using the Assembly plugin if it isn't already in the `plugins.sbt`.  Latest plugin line will be here: https://github.com/sbt/sbt-assembly then to build a fat jar with the assembly plugin just run `sbt assembly`
7. Avoid using plugins that do any thing above and beyond building the jar.  Do not use sbt to release, or deploy, your application - use bash scripts instead.  **If you ever create an sbt file with .scala extension, you are doing something wrong, if you ever create a build.sbt file with more than 100 lines you are probably doing something wrong**
8. Never create a "multi-project" build, use separate packages within your single project and make the `object`s within them private when necessary.
