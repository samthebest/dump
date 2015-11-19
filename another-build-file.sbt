import ReleaseTransformations._

val companyName = "ddd"
val domain = "ddd"

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.12.1" % "test" withSources() withJavadoc(),
  "org.specs2" %% "specs2-core" % "2.4.15" % "test" withSources() withJavadoc(),
  "org.specs2" %% "specs2-scalacheck" % "2.4.15" % "test" withSources() withJavadoc(),
  "org.apache.spark" %% "spark-core" % "1.5.1" withSources() withJavadoc(),
  "org.rogach" %% "scallop" % "0.9.5" withSources() withJavadoc(),
  "org.scalaz" %% "scalaz-core" % "7.1.4" withSources() withJavadoc(),
  "io.spray" %% "spray-json" % "1.3.2" withSources() withJavadoc(),
  "io.argonaut" %% "argonaut" % "6.1-M4" withSources() withJavadoc(),
  "com.m3" %% "curly-scala" % "0.5.+" withSources() withJavadoc(),
  "com.amazonaws" % "aws-java-sdk" % "1.10.30"
)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
)

// Strat copied from defaultMergeStrategy with the "fail and confuse the hell out the user" lines changed to
// "just bloody work and stop pissing everyone off"
mergeStrategy in assembly <<= (mergeStrategy in assembly)((old) => {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps@_*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs@_*) =>
    (xs map {
      _.toLowerCase
    }) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps@(x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first // Changed deduplicate to first
    }
  case PathList(_*) => MergeStrategy.first // added this line
})

val projectName = "altmetrics"

name := projectName

// TODO Work out how to add some of the scripts to the artefacts rather than use the resources directory

addArtifact(Artifact(projectName, "assembly"), sbtassembly.AssemblyKeys.assembly)

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := Some(sys.props.get("publish.url").map(url => "altmetrics" at url)
                  .getOrElse(Resolver.file("Local ivy", Path.userHome / ".ivy2" / "local")))

releaseProcess := Seq[ReleaseStep](
  publishArtifacts                       // : ReleaseStep, checks whether `publishTo` is properly set up
)

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-feature", "-language:reflectiveCalls")

javaOptions ++= Seq("-target", "1.8", "-source", "1.8")

organization := domain + "." + companyName

parallelExecution in Test := false

version := "0.1.20"
