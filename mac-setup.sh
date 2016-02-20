#!/bin/bash
# MAC SETUP SCRIPT

# Change editor (for next step):
export EDITOR=nano

# Checking if bash_profile already setup
grep "bash profile already setup" .bash_profile
already_setup=$?

set -e

if [ "$already_setup" != "0" ]; then
    echo "# bash profile already setup" >> ~/.bash_profile
    echo "export EDITOR=nano" >> ~/.bash_profile

    # Increase bash history to a million commands (should be enough)
    echo "HISTFILESIZE=10000000" >> ~/.bash_profile

    # Change prompt to linux default
    echo "export PS1='\$(whoami)@\$(hostname):\$(pwd) '" >> ~/.bash_profile

    echo "" >> ~/.bash_profile

    echo "function json {" >> ~/.bash_profile
    echo "	python -m json.tool" >> ~/.bash_profile
    echo "}" >> ~/.bash_profile
    echo "" >> ~/.bash_profile
    
    # nice alias for grepping history (cos CTRL + R not always that great)
    echo "function gh {" >> ~/.bash_profile
    echo "    cat ~/.bash_history | grep \$*" >> ~/.bash_profile
    echo "}" >> ~/.bash_profile
    
    echo ""  >> ~/.bash_profile
    
    # add bash-completion to ~/.bash_profile
    echo "if [ -f \$(brew --prefix)/etc/bash_completion ]; then" >> ~/.bash_profile
    echo "    . \$(brew --prefix)/etc/bash_completion" >> ~/.bash_profile
    echo "fi"  >> ~/.bash_profile
fi

# Install brew
ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

# Install brew cask (makes it easier to install mac apps)
brew install caskroom/cask/brew-cask

brew cask install google-chrome

# Spectacle means you can resize and move windows with shortcuts (requires manual step afterwards)
# After you have done the manual step to start, remember to 
# change the default shortcuts for snap right half and snap left half as they conflict with Intellij
brew cask install spectacle

# Install sublime (best text editor ever!)
brew cask install sublime-text

# Setup a link so you can run sublime from command line
ln -s /Applications/Sublime\ Text\ 2.app/Contents/SharedSupport/bin/subl /usr/local/bin/sublime

# Install java
brew cask install java

# Install scala
brew install scala

# Install sbt
brew install sbt

# Install Intellij (Community edition) (doesn't seem to work anymore)
brew cask install intellij-idea-ce

# ifstat
brew install ifstat

# firefox
brew cask install firefox

# git-flow
brew install git-flow

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
sudo easy_install pip

# install aws cli
pip install awscli

# gcloud
curl https://sdk.cloud.google.com | bash
# manual: restart shell, run gcloud init

# start git auto completion
if [ -f $(brew --prefix)/etc/bash_completion ]; then
    . $(brew --prefix)/etc/bash_completion
fi

# Remove DS_Store abomination for ever and ever
# echo "while true; do find / -name .DS_Store -exec rm -f \"{}\" \; ; sleep 2; done" > ~/.rm-DS_Store-abomination.sh && chmod +x ~/.rm-DS_Store-abomination.sh && echo "screen -ls | grep rm-DS_Store-abomination >/dev/null || screen -S rm-DS_Store-abomination -d -m ~/.rm-DS_Store-abomination.sh" >> ~/.bash_profile && screen -ls | grep rm-DS_Store-abomination >/dev/null || screen -S rm-DS_Store-abomination -d -m ~/.rm-DS_Store-abomination.sh
