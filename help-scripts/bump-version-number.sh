#!/bin/bash

# TODO WARNING - This cruft will be migrated to use the sbt-release plugin, but it will probably take a long time
# to suss out how to make sbt-release work.

set -xe

source ./scripts/utils.sh

version_old=`get-version`

cat build.sbt | grep -v "$version_key" > build.sbt.tmp

function get-minor-minor-version {
	echo $version_old | tr '\.' ' ' | awk '{print $3}'
}

function get-majors {
	echo $version_old | tr '\.' ' ' | awk '{print $1 " " $2}' | tr ' ' '\.'
}

function gen-new-minor-minor-version-number {
	old_minor_minor=`get-minor-minor-version`
	expr $old_minor_minor + 1
}

majors=`get-majors`
new_minor_minor=`gen-new-minor-minor-version-number`

echo "${version_key}${majors}.${new_minor_minor}\"" >> build.sbt.tmp

mv build.sbt.tmp build.sbt

version=`get-version`

# echo the appropriate commit message
echo "JENKINS: Bumped version number from $version_old to $version"
