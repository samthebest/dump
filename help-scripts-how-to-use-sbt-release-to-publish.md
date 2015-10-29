1) Add `addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")` to `project/plugins.sbt`

2) Add

```
import ReleaseTransformations._

name := projectName

// Ensures fat jar gets published too

addArtifact(Artifact(projectName, "assembly"), sbtassembly.AssemblyKeys.assembly)

import ReleaseTransformations._

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := Some(sys.props.get("publish.url").map(url => "project-name" at url).getOrElse(Resolver.file("Local ivy", Path.userHome / ".ivy2" / "local")))

releaseProcess := Seq[ReleaseStep](
  publishArtifacts                       // : ReleaseStep, checks whether `publishTo` is properly set up
)
```

To your `build.sbt`

3) Run `sbt -Dpublish.url=http://your-domain:80/nexus/content/repositories/releases/ release`
