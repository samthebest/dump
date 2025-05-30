#!/bin/bash

# Change terminal to bash
chsh -s /bin/bash

# Need wget first
wget -O pro.terminal https://raw.githubusercontent.com/samthebest/dump/master/setupbin/pro.terminal

# Add alias to open pro terminal:
alias pt='open ~/pro.terminal'

echo "alias pt='open ~/pro.terminal'" >> ~/.bash_profile

set -ex

# Exclude -N "" to add passphrase
ssh-keygen -t rsa -b 4096 -C "sams@example.com" -N "" -f ~/.ssh/id_rsa

# Skip annoying "yes" for cloning git repos
# git config --global http.sslVerify false
mkdir .ssh && echo -e "Host github.com\n\tStrictHostKeyChecking no\n" >> ~/.ssh/config

chmod 700 .ssh

# git setup
git config --global user.name "sam"
git config --global user.email sam@example.com

git config --global alias.co checkout

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
brew install sublime-text

# Download intellij and the solarized dark settings
brew install intellij-idea-ce
# Deprecated, have to use a plugin now, but plugin sucks
wget -O settings.jar https://github.com/jkaving/intellij-colors-solarized/blob/master/settings.jar?raw=true

# Install java (8 so compatible with Spark)
# brew tap caskroom/versions # Not work anymore
# brew install java8 # Not work anymore
# brew install adoptopenjdk/openjdk/adoptopenjdk8

# Java (compataible with other stuff) (not tested yet)
sudo softwareupdate --install-rosetta
brew install adoptopenjdk/openjdk/adoptopenjdk11

# Install scala
brew install scala

# Install sbt
# No longer works
#https://stackoverflow.com/questions/61067260/how-can-i-install-openjdk-8-and-sbt-on-mac-and-have-openjdk-8-not-13-the-defau
#brew install sbt

# Install Intellij (Community edition) (doesn't seem to work anymore)
# brew install intellij-idea-ce

# ifstat
brew install ifstat

# firefox
#brew install firefox

# Install GNU style bash commands (gives gdate and such and such)
brew install coreutils

# Hipchat
#brew install hipchat

# git auto completion
brew install git bash-completion

# spotify for concentration
brew install spotify

# open office
brew install openoffice

# wget
brew install wget

# s3cmd
#brew install s3cmd

# Source tree
#brew install sourcetree

# really cool json tool
brew install jq

# install pip
brew install python

# # install aws cli (not sure the export PATH is necessary?)
# curl -O https://bootstrap.pypa.io/get-pip.py
# python3 get-pip.py --user
# pip3 install awscli --upgrade --user
# pip3 install awscli --upgrade --user
#export PATH=~/Library/Python/3.7/bin:$PATH
#echo "export PATH=~/Library/Python/3.7/bin:$PATH" >> ~/.bash_profile

# New brew command aws cli, not sure works
brew install awscli
# Manual step: `aws configure`

brew install --cask flux

brew install steam

# Rust
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
rustup component add rust-analyzer


# vagrant - a orchestration for virtual box
#brew install vagrant

# Zeppelin:
#brew install apache-spark
#brew install apache-zeppelin

# VLC
#brew install vlc

# Virtual box
# TODO Requires fix for a manual hoop regarding enabling something
# brew install virtualbox

