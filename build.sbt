import sbtassembly.Plugin.AssemblyKeys._

assemblySettings

val companyName = "openmaths"
val domain = "io"

val mavenRepo: String = "maven." + companyName + "." + domain

resolvers ++= Seq(
  "Concurrent Maven Repo" at "http://conjars.org/repo",
  "mvnrepository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "spray repo" at "http://repo.spray.io"
)

val akkaV = "2.3.6"
val sprayV = "1.3.2"

libraryDependencies ++= Seq(
  // Could try this in order to upgrade to newest specs2
  "org.scalacheck" %% "scalacheck" % "1.12.1" % "test" withSources() withJavadoc(),
  //"org.scalacheck" %% "scalacheck" % "1.10.1" % "test" withSources() withJavadoc(),
  // newest specs2
  "org.specs2" %% "specs2-core" % "2.4.15" % "test" withSources() withJavadoc(),
  // Trying this for 2.11, it caused "Modules were resolved with conflicting cross-version suffixes" BAA!
  //"org.specs2" % "specs2_2.11.0-RC3" % "2.3.10" % "test" withSources() withJavadoc(),
  // Only works with 2.10
  //"org.specs2" %% "specs2" % "1.14" % "test" withSources() withJavadoc(),
  // Used to be 7.0.5
  "org.scalaz" %% "scalaz-core" % "7.1.0" withSources() withJavadoc(),
  //
  "org.apache.commons" % "commons-math3" % "3.2" withSources() withJavadoc(),
  "org.apache.commons" % "commons-lang3" % "3.3.2" withSources() withJavadoc(),
  //
  "org.parboiled" %% "parboiled" % "2.0.1" withSources() withJavadoc() exclude("com.chuusai", "shapeless_2.10.4") exclude("org.scalamacros", "quasiquotes_2.10.3") exclude("org.scalamacros", "quasiquotes_2.10.4") exclude("org.scalamacros", "quasiquotes_2.10"),
  //
  "com.googlecode.java-diff-utils" % "diffutils" % "1.2",
  //
  "io.spray" %% "spray-json" % "1.3.1" withSources() withJavadoc(),
  "io.spray" %% "spray-can" % sprayV withSources() withJavadoc(),
  "io.spray" %% "spray-routing" % sprayV withSources() withJavadoc(),
  "io.spray" %% "spray-testkit" % sprayV  % "test" withSources() withJavadoc(),
  //
  "com.typesafe.akka" %% "akka-actor" % akkaV withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-testkit" % akkaV % "test" withSources() withJavadoc()
  //"org.apache.spark" % "spark-sql_2.10" % "1.0.0-cdh5.1.3" % "provided" withSources() withJavadoc(),
  //"org.apache.spark" % "spark-core_2.10" % "1.0.0-cdh5.1.3" % "provided" withSources() withJavadoc()
)

scalaVersion := "2.10.4"

javaOptions ++= Seq("-target", "1.8", "-source", "1.8")

organization := domain + "." + companyName

name := "openmaths"

parallelExecution in Test := false

version := "0.1.0"

// to make work: https://github.com/sbt/sbt-assembly need to google "not found assembly", might need an import or something
//jarName in assembly := name + "-" + version + "-assembly.jar"
