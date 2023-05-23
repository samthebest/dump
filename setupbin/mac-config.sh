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

# Disable shortcut that clashes with Intellij (not properly tested yet)
defaults write -g NSUserKeyEquivalents -dict-add "Open man Page in Terminal" nil



# Track direction, and turning off stupid gestures and stuff is all manual.


# To always show function keys (F1, F2 and so on) in the Touch Bar for specific apps, 
# choose Apple menu > System Settings, click Keyboard in the sidebar (you may need to scroll down), 
# click Keyboard Shortcuts on the right, select Function Keys in the list on the left, then add the apps on the right.
# https://support.apple.com/en-gb/guide/mac-help/mchl5a63b060/mac#:~:text=To%20always%20show%20function%20keys,the%20apps%20on%20the%20right.
