
### Overview

In this (high-level) tutorial we aim to build and architect a fully distributed streaming (with Kafka) data science application that includes ingestion, etl, feature extraction (in Scala) and prediction (in Python).  The architecture will ultimately include a Dataproc cluster for analysis and model training.

The tutorial will be guided by tests and encourage a test-first TDD approach.  These tests will include unit, integration and end-to-end tests.

Since this tutorial aims to build a complete architecture, which would ordinarily take weeks in a typical enterprise, this tutorial is not short.  Rather it is meta-tutorial; a guide to following many tutorials in such a way to build a complete application.

The components have been arbitrarily split between Scala and Python.  We could easily build the whole thing in Scala, but we split so that we can become familiar with having components in different languages.  Note that I don't think Kafka Streams has a Python equivilent API, so we will have to use Scala for the Join.

In practice we would usually opt for building the entire application in Scala (or Java 8) and only use Python for machine learning libraries that performs well and does not exist in Scala.

#### Questions, Corrections, Suggestions, Spelling

Please raise a github issue or pull request.

#### Target Audience

The target audience is any data scientist or engineer that is interested in understanding the complete picture and who wishes to be involved in the leading and architecture of an entire project.  This would also be useful for data scientists and engineers in startups, where it is assumed a small number of people have to do everything.

The tutorial aims at giving the Why and the What, not the details of the How.  Determining the precise How oneself with the aid of man pages, google, github, documentation and stackoverflow is part of the day to day role of a data scientist or engineer.  Consequently tenacity and initiative will be required to understand and complete the tutorial.

For those who wish to get a better understanding of Kafka conceptually, but not necessarily understand all the technical and engineering details required to get into production, then only follow steps 1 to 5.

#### Imaginary Use Case

We are going to imagine that we are going to build a system for a number of clients `N_C`, where each client has 3 sensors `SX`, `SY` and `SZ`.  Every time we receive an event from any sensor `S \in { SX, SY, SZ }` we will make a prediction whether or not to send an alert (which will just be binary, alert or do not alert).

We will assume some analysis has already been performmed, and for a baseline implementation we believe the following logic could work

 - We treat `SX` and `SY` as categorical inputs, so integer values
 - We wish to extract derived features from a 10 minute sliding window of `SZ` as follows:
   - We extract the sliding average, and bin uniformly into 10 buckets - (FA)
   - We extract the sliding AUC (integral), and bin uniformly into 10 buckets - (FI)
   - We extract the sliding delta (derivative), and bin uniformly into 10 buckets - (FD)
 - This will give us an integer feature vector of length 5
   
Variants of this exercise obviously involve different binning strategies, but such variations would not contribute to the intent of this tutorial.

Consequently we wish to produce the following topic topology, where we assume some upstream component writes to our three input topics (one for each sensor) of the form `< client-key, sensor reading >`, which are already partitioned by client-key with 100 partitions.

```
SX Topic ------------->------------->-----------|
SY Topic ------------->------------->-----------|
SZ Topic --> Window Processor --> FA Topic -->--|---> Join by client key                     prediction Topic
                            |---> FI Topic -->--|          |                                           /\
                            |---> FD Topic -->--|          |--> Features Topic --> Predict Processor-->|
```

It's assumed some downstream component will consume from the prediction Topic that will trigger an action that effects the client (e.g. send an email, change a setting on a device, etc).

Every Topic will be partitioned by client key and have 100 partitions, this means our Join will be easy.

We will essentially have three consumer-producer processors that we need to write:

#### Window Processor

This will calculate the windowed values.  We choose to write this out to 3 topics, one for each feature, rather than combine this with the Join processor.  Reasons that could justify this architectural choice include:

 - For reporting/monitoring/analysis another consumer of these feature topics may exist
 - If the Window Processor is computationally expensive then we have finer control over
   - threading
   - maintenance windows
   - replayability
   - the ability to use different languages / libraries with Kafka as an interface (note that if a Topic is *soley* used as an interface one would typically set it's retention policy to use less storage space)

The Window Processor will have

 - One consumer group
 - Potentially many consumers within the group for parallelism. The feature extraction code should live with the consumer code.
 - Three producers, one for each feature Topic

#### Join Processor

This will perform a copartitioned join on the 5 feature topics, and write out a `< client-key, feature-vector >` to the Feature Topic.

#### Predict Processor

This will consume from the Feature Topic, use the feature vector to make a prediction, then produce the prediction to the predicttion Topic.

### Step 1

Create a github repo for your Dummy Kafka project.  As you follow the tutorial try to populate your README.md, remember to include documentation on how to build, test and deploy your application.

### Step 2 - Create a Scala ingestion framework project

In the repo create a subdirecty called `ingestion-and-etl`, in that directory create a Scala project with a build file that includes the latest version of the Kafka and Kafka Streams libraries.  If you are not familiar with creating Scala projects take a look at https://github.com/samthebest/dump/blob/master/other-tutorials/creating-a-scala-project-tips.md

Observe we create our scala component inside a subdirectory instead of the root.  This is to allow for creating further subdirectories for components in different languages.  Then we only need a single repo, which is the modern recommended way to build applications (google Monorepo).

**Estimated Time**: Scala Expert: 1 minute, Scala Beginner: 1 - 2 hours

### Step 3 - Create a Python dummy model project

For now we are going to write a dummy model in Python just to build something that wires together components.

Create another subdirectory called `model`, **using TDD**, within it write a python method called `predict` that takes as input a vector of length 5 of integers and returns a random Boolean.  Since this is Python, you'll have to explicitly handle the cases when the vector is of the wrong type since Python is dynamically typed.

If you are not used to writing code using TDD, now is the time start as more and more companies require it, and it's an extremly logical way to write code.  It seems https://code.tutsplus.com/tutorials/beginning-test-driven-development-in-python--net-30137 is a reasonable tutorial on how to do TDD with Python, but I'm sure there are many more.

**Estimated Time**: TDD Expert: 5 - 20 minutes, TDD beginner: 1 hour

### Step 4 - Implement ingestion framework logic with unit tests

Implement the Window Processor and the Join Processor in Scala *via TDD*.  You will want to use the Kafka Streams API for the Join processor.  It may be a good exercise to implement the Window Processor in both the low level Kafka consumer-producer APIs, as well as the Kafka Streams API.  The API documentation can be found here: https://kafka.apache.org/documentation/#api

Do not worry too much about topic creation code at this stage, assume they exist or are auto-created.  Try using an embedded kafka for unit tests: https://github.com/manub/scalatest-embedded-kafka

If you are new to Scala, suggest reading the following:

https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/fp-4-newcomers.md
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/introduction-to-typing.md
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/basic-types.md
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/avoid-vals.md
https://github.com/samthebest/dump/blob/master/sams-scala-tutorial/avoid-recursion.md

Consider looking for examples (particularly for unit tests) here:
https://github.com/confluentinc/kafka-streams-examples

**Estimated Time**: Scala, TDD & Kafka Expert: 1 day, beginner: 5+ days

### Step 5 - Implement model consumer/producer with unit tests

Find an equivalant to https://github.com/manub/scalatest-embedded-kafka for Python (assuming one exists).  If no such unit testing framework exists, this would make Step 7 critical.

Using the Consumer and Producer APIs for Python, consume from the Feature Topic and write to the Prediction Topic.

#### TUTORIAL UNFINISHED FROM HERE

### Step 6 - Implement ingestion framework application with integration tests

TODO

Let Docker create the topics on startup.

Create an `it` integration test directory.

Add open-ssh to the Docker file (so we can reuse the deployment scripts).

Call out to docker commands from `it` tests, so we can spin up and run all tests with `sbt it:test`

https://github.com/wurstmeister/kafka-docker

(I have no recommended tutorial for Docker since it's usage has changed a lot since I first learnt it.)

### Step 7 - Wire in prediction component with integration tests

TODO

### Step 8 - Use a real Confluent Cloud kafka cluster with E2E tests

TODO

https://www.confluent.io/confluent-cloud/

### Step 9 - Add a consumer to copy data on kafka to Google Cloud Storage

Do not use Spark, use the Parquet and Google Cloud Storage APIs directly!

TODO


```
SX Topic ------------->------------->-----------|
SY Topic ------------->------------->-----------|
SZ Topic --> Window Processor --> FA Topic -->--|---> Join by client key                     prediction Topic
                            |---> FI Topic -->--|          |                                           /\   |
                            |---> FD Topic -->--|          |--> Features Topic --> Predict Processor-->|    |
                                                                       |                                    |
                                                                       \/                                  \/
                                                                            Google Cloud Storage consumers
                                                                                           |
                                                                                           |
                                                                                           \/
                                                                                Google Cloud Storage Parquet
```

### Step 10 - Create a dataproc cluster to analyse the data in Google Cloud Storage

TODO



