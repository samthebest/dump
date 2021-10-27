#!/bin/bash

# Show Battery %
defaults write com.apple.menuextra.battery ShowPercent YES
killall SystemUIServer

# Turn off annoying stuff
defaults write .GlobalPreferences com.apple.mouse.scaling -1
defaults write .GlobalPreferences com.apple.trackpad.scaling -1

# Fix key repeat settings (might need a reboot, seems a little flakey sometimes)
defaults write -g InitialKeyRepeat -int 10 # normal minimum is 15 (225 ms)
defaults write -g KeyRepeat -int 1 # normal minimum is 2 (30 ms)

# Have to enable dock zoom manually
# Change dock size to be basically invisible
defaults write com.apple.dock tilesize -int 1
defaults write com.apple.dock largesize -float 512
killall Dock


# Track direction, and turning off stupid gestures and stuff is all manual.

