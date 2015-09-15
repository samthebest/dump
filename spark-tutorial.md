
# Spark Shell

To start a shell:

spark-shell --master yarn-client --executor-memory 60G --driver-memory 12G --num-executors 5 --executor-cores 20 --jars your-lib.jar

## To determine defaults:

Executors & Executor Memory:
Use Spark UI (spat out at beginning) and look at Executors to determine the number of executors and amount of memory.

Driver Memory:
No idea

Num Cores:
Try this: 

sc.makeRDD(1 to 10000).repartition(100).mapPartitions(_ => {java.lang.Thread.sleep(10000); Iterator()}).count()

and count the number of started tasks and divide by the number of executors


Spark shell script:

Uses LIB_DIR and BIN_DIR, unless one has root, it will be hard to work out what these things are.  

http://spark.apache.org/docs/latest/programming-guide.html

http://spark.apache.org/docs/latest/submitting-applications.html


## Under Cloudera

Login to cloudera manager, click YARN, should be able to see resource manager and history manager
