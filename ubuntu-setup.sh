#!/bin/bash

# To run execute:
# wget https://raw.githubusercontent.com/samthebest/dump/master/ubuntu-setup.sh && chmod +x ubuntu-setup.sh && ./ubuntu-setup.sh

# Java
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer

# sbt
echo "deb http://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-get update
sudo apt-get install sbt

# git
sudo apt-get install git

# mkdir src
mkdir src
