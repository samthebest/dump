#!/bin/bash

cd src/open-maths
config=src/main/resources/config.txt
pwd=`pwd`
src_dir=`dirname $pwd`

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

if [ "$2" = "prod" ]
then
    interface="0.0.0.0"
    port=80
    use-sudo="sudo "
    shift
else
    interface="localhost"
    port=8080
    use-sudo=""
fi
    
$use-sudo java -cp target/scala-2.11/openmaths-assembly-0.1.0.jar io.openmaths.Main $interface $port $*  
    



