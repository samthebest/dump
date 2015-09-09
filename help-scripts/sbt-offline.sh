#!/bin/bash
sbt "set skip in update := true" "set offline := true" "set scalacOptions in ThisBuild ++= Seq(\"-unchecked\", \"-deprecation\")" "$*"
