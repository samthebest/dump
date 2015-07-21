#!/bin/bash

# To run execute:
# wget https://raw.githubusercontent.com/samthebest/dump/master/ubuntu-setup.sh && chmod +x ubuntu-setup.sh && ./ubuntu-setup.sh

# Bash history to something sensible
echo "HISTSIZE=1000000" >> ~/.bashrc

# Chrome
wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
sudo sh -c 'echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list'
sudo apt-get update 
sudo apt-get install google-chrome-stable

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

# Curl (some distros don't have curl)
sudo apt-get install curl

# Google cloud https://cloud.google.com/sdk/
curl https://sdk.cloud.google.com | bash
source .bashrc

# Flash
sudo apt-get install pepperflashplugin-nonfree

# Sublime
sudo apt-get install sublime-text

# TODO See mac setup script
