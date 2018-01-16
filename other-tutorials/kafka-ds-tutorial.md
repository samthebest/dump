
### Overview

In this (high-level) tutorial we aim to build and architect a fully distributed streaming (with Kafka) data science application that includes ingestion, etl, feature extraction (in Scala) and prediction (in Python).  The architecture will ultimately include a Dataproc cluster for analysis.

### Step 1

Create a github repo for your Dummy Kafka project.  As you follow the tutorial try to populate your README.md, remember to include documentation on how to build, test and deploy your application.

### Step 2 - Create a Scala ingestion framework project

In the repo create a subdirecty called `ingestion-and-etl`, in that directory create a Scala project with a build file that includes the latest version of the Kafka and Kafka Streams libraries.  If you are not familiar with creating Scala projects take a look at https://github.com/samthebest/dump/blob/master/other-tutorials/creating-a-scala-project-tips.md

Observe we create our scala component inside a subdirectory instead of the root.  This is to allow for creating further subdirectories for components in different languages.  Then we only need a single repo, which is the modern recommended way to build applications (google Monorepo).

**Estimated Time**:
Scala Expert: 1 minute
Scala Beginner: 1 - 2 hours

### Step 3 - Create a Python dummy model project

For now we are going to write a dummy model in Python just to build something that wires together components.

Create another subdirectory called `model`, **using TDD**, within it write a python method called `predict` that takes as input a vector of length 3 of integers and returns a random double between 0 and 1.  Since this is Python, you'll have to explicitly handle the cases when the vector is of the wrong type since Pythong is dynamically typed.

If you are not used to writing code using TDD, now is the time start as more and more companies require it, and it's an extremly logical way to write code.  It seems https://code.tutsplus.com/tutorials/beginning-test-driven-development-in-python--net-30137 is a reasonable tutorial on how to do TDD with Python, but I'm sure there are many more.

**Estimated Time**
TDD Expert: 20 minutes
TDD beginner: 1 - 2 hours

### Step 4 - Implement ingestion framework logic with unit tests

new to scala:
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/fp-4-newcomers.md
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/introduction-to-typing.md
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/basic-types.md
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/avoid-vals.md
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/avoid-recursion.md




https://github.com/manub/scalatest-embedded-kafka

### Step 5 - Implement model consumer/producer with unit tests

Find an equivilant to https://github.com/manub/scalatest-embedded-kafka for Python (assuming one exists).  If no such unit testing framework exists, this would make Step 7 critical.

### Step 6 - Implement ingestion framework application with integration tests

Let Docker create the topics on startup.



Create an `it` integration test directory.

Add ssh to the Docker file

Call out to docker 

https://github.com/wurstmeister/kafka-docker

(I have no recommended tutorial for Docker since it's usage has changed a lot since I first learnt it.)



### Step 7 - Wire in prediction component with integration tests

### Step 8 - Use a real Confluent Cloud kafka cluster with E2E tests

### Step 9 - Add a consumer to relay all data on kafka to Google Cloud Storage in Parquet

### Step 10 - Create a dataproc cluster to analyse the data in Google Cloud Storage





