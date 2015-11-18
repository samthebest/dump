# Plugins file

include 

```
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")
```

# Build file

```
import ReleaseTransformations._

//.... dependencies and such and such

// TODO Work out how to add some of the scripts to the artefacts rather than use the resources directory

addArtifact(Artifact(projectName, "assembly"), sbtassembly.AssemblyKeys.assembly)

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := Some(sys.props.get("publish.url").map(url => "altmetrics" at url)
          .getOrElse(Resolver.file("Local ivy", Path.userHome / ".ivy2" / "local")))

releaseProcess := Seq[ReleaseStep](
  publishArtifacts                       // : ReleaseStep, checks whether `publishTo` is properly set up
)

```



# Publish script

```
#!/bin/bash

set -e

sbt -Dpublish.url=http://blar.blar:80/nexus/content/repositories/releases/ release | tee publish.log

echo "INFO: Checking if publish had error because sbt sucks and always gives 0 exit code"

grep "\[error\]" publish.log

publish_had_error=$?

if [ ${publish_had_error} = 0 ]; then
    exit 1
fi
```
