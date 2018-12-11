#!/bin/bash

set -e

# Skip annoying "yes" for cloning git repos
git config --global http.sslVerify false
mkdir .ssh && echo -e "Host github.com\n\tStrictHostKeyChecking no\n" >> ~/.ssh/config

# git setup
git config --global user.name "sam"
git config --global user.email sam@example.com

ssh-keygen -t rsa -b 4096 -C "sams@example.com" -N "" -f ~/.ssh/id_rsa

echo "
Host *
 AddKeysToAgent yes
 UseKeychain yes
 IdentityFile ~/.ssh/id_rsa
" >> ~/.ssh/config

eval "$(ssh-agent -s)"
ssh-add -K ~/.ssh/id_rsa

# APPS

# Install sublime (best text editor ever!)
brew cask install sublime-text

# Download intellij and the solarized dark settings
brew cask install intellij-idea-ce
wget -O settings.jar https://github.com/jkaving/intellij-colors-solarized/blob/master/settings.jar?raw=true

# Install java (8 so compatible with Spark)
brew tap caskroom/versions
brew cask install java8

# Install scala
brew install scala

# Install sbt
brew install sbt

# Install Intellij (Community edition) (doesn't seem to work anymore)
# brew cask install intellij-idea-ce

# ifstat
brew install ifstat

# firefox
brew cask install firefox

# Install GNU style bash commands (gives gdate and such and such)
brew install coreutils

# Hipchat
brew cask install hipchat

# git auto completion
brew install git bash-completion

# spotify for concentration
brew cask install spotify

# open office
brew cask install openoffice

# wget
brew install wget

# s3cmd
brew install s3cmd

# Source tree
brew cask install sourcetree

# really cool json tool
brew install jq

# install pip
brew install python

# install aws cli
pip install awscli

# vagrant - a orchestration for virtual box
brew cask install vagrant

# VLC
brew cask install vlc

# Virtual box
# TODO Requires fix for a manual hoop regarding enabling something
# brew cask install virtualbox

