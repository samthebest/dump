#!/bin/bash

set -e

test "${1}" != ""

email=${1}

# To run execute:
# wget https://raw.githubusercontent.com/samthebest/dump/master/debian-setup.sh && chmod +x debian-setup.sh && ./debian-setup.sh

# Bash history to something sensible
echo "HISTSIZE=1000000" >> ~/.bashrc

echo "function json {" >> ~/.bash_profile
echo "	python -m json.tool" >> ~/.bash_profile
echo "}" >> ~/.bash_profile

echo "function gh {" >> ~/.bash_profile
echo "    cat ~/.bash_history | grep \$*" >> ~/.bash_profile
echo "}" >> ~/.bash_profile

# Chrome
wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
sudo sh -c 'echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list'
sudo apt-get update 
sudo apt-get install -y google-chrome-stable

# git
sudo apt-get install -y git

# Java
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install -y oracle-java8-installer

# sbt
echo "deb http://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-get update
sudo apt-get install -y sbt

# mkdir src
mkdir src

# Curl (some distros don't have curl)
sudo apt-get install -y curl

# Google cloud https://cloud.google.com/sdk/
curl https://sdk.cloud.google.com | bash
source .bashrc

# Flash
sudo apt-get install -y pepperflashplugin-nonfree

# Skype
sudo apt-get install -y skype

# Sublime
sudo apt-get install -y sublime-text

# cool json tool
sudo apt-get install -y jq

# docker
wget -qO- https://get.docker.com/ | sh
sudo gpasswd -a ${USER} docker
newgrp docker
sudo service docker restart
# Requires restart

# Intellij (version dependent)
wget -O /tmp/intellij.tar.gz http://download.jetbrains.com/idea/ideaIC-14.1.4.tar.gz
mkdir idea-IC
tar xfz /tmp/intellij.tar.gz -C idea-IC --strip-components=1
wget -O /tmp/idea-settings.jar https://github.com/jkaving/intellij-colors-solarized/blob/master/settings.jar?raw=true
#./idea-IC/bin/idea.sh

# xclip
sudo apt-get install -y xclip

# Setup an ssh key
ssh-keygen -t rsa -b 4096 -C "$email"
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa
xclip -sel clip < ~/.ssh/id_rsa.pub
# MANUAL STEP - Paste into github https://github.com/settings/ssh

# Hipchat
sudo su 
echo "deb http://downloads.hipchat.com/linux/apt stable main" > /etc/apt/sources.list.d/atlassian-hipchat.list
wget -O - https://www.hipchat.com/keys/hipchat-linux.key | apt-key add -
apt-get update
apt-get install -y hipchat
exit

# Clone my open-source repos
cd ~/src
#git clone git@github.com:samthebest/sceval.git

# MANUAL STEPS - Start intellij and load settings from /tmp

# TODO See mac setup script
