#!/bin/bash

# TODO Clone repos


# APPS

# Install sublime (best text editor ever!)
brew cask install sublime-text

# Install java
brew cask install java

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

# DEPRECATED use oneflow
# git-flow
# brew install git-flow

# git auto completion
brew install git bash-completion

# Skip annoying "yes" for cloning git repos
# FIXME Not idempotent
git config --global http.sslVerify false
echo -e "Host github.com\n\tStrictHostKeyChecking no\n" >> ~/.ssh/config

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

