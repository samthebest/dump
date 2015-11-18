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

sbt -Dpublish.url=${releases_url} release 2>&1 | tee publish.log
#echo "it worky" | tee publish.log

echo "INFO: Checking if publish had error because sbt sucks and always gives 0 exit code"

errors=`cat publish.log | sed -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[mGK]//g" | grep "\[error\]"`

if [ "${errors}" != "" ]; then
    echo "INFO: Publish had errors, errors:"
    echo ${errors}
    exit 1
fi

exit 0

```
