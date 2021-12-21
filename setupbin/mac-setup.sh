#!/bin/bash
# MAC SETUP SCRIPT

# TODO Make this interactive

# Checking if bash_profile already setup
grep "bash profile already setup" .bash_profile
already_setup=$?


# Install brew
brew >/dev/null || ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

brew install google-chrome

# Change screenshot location to something more appropriate than desktop
mkdir ~/Documents/Screenshots

defaults write com.apple.screencapture location ~/Documents/Screenshots

# Change editor
export EDITOR=nano

set -e

if [ "$already_setup" != "0" ]; then
    echo "# bash profile already setup" >> ~/.bash_profile
    echo "export EDITOR=nano" >> ~/.bash_profile

    # Increase bash history to a million commands (should be enough)
    echo "HISTFILESIZE=10000000" >> ~/.bash_profile
    
    
    echo "shopt -s histappend" >> ~/.bash_profile
    echo "PROMPT_COMMAND=\"history -a; \"" >> ~/.bash_profile
    # Override in memory hist size
    echo "HISTSIZE=1000000" >> ~/.bash_profile
    # Override file hist size
    echo "HISTFILESIZE=2000000" >> ~/.bash_profile

    # Change prompt to linux default
    echo "export PS1='\$(whoami)@\$(hostname):\$(pwd) '" >> ~/.bash_profile

    echo "" >> ~/.bash_profile

    echo "function json {" >> ~/.bash_profile
    echo "  python -m json.tool" >> ~/.bash_profile
    echo "}" >> ~/.bash_profile
    echo "" >> ~/.bash_profile
    
    # nice alias for grepping history (cos CTRL + R not always that great)
    echo "function gh {" >> ~/.bash_profile
    echo "    cat ~/.bash_history | grep \$*" >> ~/.bash_profile
    echo "}" >> ~/.bash_profile
    
    echo ""  >> ~/.bash_profile
    
    # add bash-completion to ~/.bash_profile
    curl https://raw.githubusercontent.com/git/git/master/contrib/completion/git-completion.bash -o ~/.git-completion.bash
    
    echo "if [ -f ~/.git-completion.bash ]; then" >> ~/.bash_profile
    echo "  . ~/.git-completion.bash" >> ~/.bash_profile
    echo "fi"  >> ~/.bash_profile
fi

# Disable thing that makes it impossible to run apps from internet
sudo spctl --master-disable

# Increase timeout for sudo
# sudo visudo # then change Defaults timestamp_timeout=0 to Defaults timestamp_timeout=60
# OR ("dangerous" only try this on a fresh install (or rebuild required))

# sudo sh -c 'echo "\nDefaults timestamp_timeout=60">>/etc/sudoers'

# Fix bug with mac mouse & trackpad
# Stopped working:
# brew install steelseries-exactmouse-tool

brew install wget

wget http://downloads.steelseriescdn.com/drivers/tools/steelseries-exactmouse-tool.dmg
# Must then manually install the dmg (need to work out how to automate these steps:
# https://apple.stackexchange.com/questions/73926/is-there-a-command-to-install-a-dmg
# This seems best:
# https://stackoverflow.com/questions/21428208/how-to-silently-install-dmg-file-in-mac-os-x-system-using-shell-script

# At this point should reboot to ensure the keyboard & mouse settings work

mkdir -p ~/src

# Spectacle means you can resize and move windows with shortcuts
# After you have done the manual step to start, remember to 
# change the default shortcuts for snap right half and snap left half as they conflict with Intellij
brew install spectacle

curl https://raw.githubusercontent.com/samthebest/dump/master/setupbin/mac-fully-automated.sh | bash
curl https://raw.githubusercontent.com/samthebest/dump/master/setupbin/mac-config.sh | bash

# gcloud
curl https://sdk.cloud.google.com | bash
# manual: restart shell, run gcloud init

# Keybase https://keybase.io/
# manual: open and sign in (and add device)
brew install keybase

# start git auto completion
source ~/.bash_profile

# Remove DS_Store abomination for ever and ever
# echo "while true; do find / -name .DS_Store -exec rm -f \"{}\" \; ; sleep 2; done" > ~/.rm-DS_Store-abomination.sh && chmod +x ~/.rm-DS_Store-abomination.sh && echo "screen -ls | grep rm-DS_Store-abomination >/dev/null || screen -S rm-DS_Store-abomination -d -m ~/.rm-DS_Store-abomination.sh" >> ~/.bash_profile && screen -ls | grep rm-DS_Store-abomination >/dev/null || screen -S rm-DS_Store-abomination -d -m ~/.rm-DS_Store-abomination.sh
