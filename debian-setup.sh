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

# Intellij (version dependent)
wget -O /tmp/intellij.tar.gz http://download.jetbrains.com/idea/ideaIC-14.1.4.tar.gz
mkdir idea-IC
tar xfz /tmp/intellij.tar.gz -C idea-IC --strip-components=1
wget -O /tmp/idea-settings.jar https://github.com/jkaving/intellij-colors-solarized/blob/master/settings.jar?raw=true
./idea-IC/bin/idea.sh

# xclip
sudo apt-get install xclip

# Setup an ssh key (change the email)
ssh-keygen -t rsa -b 4096 -C "email@address.com"
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa
xclip -sel clip < ~/.ssh/id_rsa.pub
# MANUAL STEP - Paste into github https://github.com/settings/ssh

# Clone my open-source repos
cd ~/src
git clone git@github.com:samthebest/sceval.git

# MANUAL STEPS - Start intellij and load settings from /tmp

# TODO See mac setup script
