#!/bin/bash

project_dir=src/open-maths
cd $project_dir

git checkout develop
git pull origin develop
config=src/main/resources/config.txt
pwd=`pwd`
src_dir=`dirname $pwd`

mkdir src/main/resources
echo "jsonDB=$src_dir/open-maths-content/json" > $config
echo "latexDB=$src_dir/open-maths-content/latex" >> $config
echo "userDB=$src_dir/user-db/users" >> $config
echo "actionsDB=$src_dir/user-db/actions" >> $config

if [ "$1" != "skip-build" ]
then
    sbt assembly
else
    shift
fi

if [ "$1" = "prod" ]
then
    interface="0.0.0.0"
    port=80
    use_sudo="sudo "
    shift
else
    interface="localhost"
    port=8080
    use_sudo=""
fi

echo "${use_sudo}java -cp target/scala-2.11/openmaths-assembly-0.1.0.jar io.openmaths.Main $interface $port enable-db-mutation $* 2>&1 | tee -a open-maths.log" > last-run.sh

screen -dm bash -c "./last-run.sh"
