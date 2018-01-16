1. Do NOT use the Intellij "create project" setup wizard.
2. Do NOT try to read the official SBT documentation, it's very verbose.
3. Install sbt, on a mac this is probably `brew install sbt` (assuming you have brew), on debian based linux this will be `apt-get install sbt`, rpm based linux `yum install sbt`.
4. Instead find a existing project on the web that uses a lot of the libraries you intend on using. It seems searching for `library1 ... libraryN build.sbt` tends to give a long list of github pages with example projects that use libraries 1 - N.
5. `cp --parents` the `project/plugins.sbt` file and `build.sbt` file.
6. Reccomend using the Assembly plugin if it isn't already in the `plugins.sbt`.  Latest plugin line will be here: https://github.com/sbt/sbt-assembly then to build a fat jar with the assembly plugin just run `sbt assembly`
