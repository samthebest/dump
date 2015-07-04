HISTFILESIZE=1000000
export PS1='$(whoami)@$(hostname):$(pwd) '

if [ -f $(brew --prefix)/etc/bash_completion ]; then
    . $(brew --prefix)/etc/bash_completion
fi

function gh {
    cat ~/.bash_history | grep $*
}

# added by Anaconda3 2.1.0 installer
export PATH="/opt/anaconda/bin:$PATH"

export EDITOR=nano

function catr {
    find . -name "$*" -print0 | xargs -0 cat
}
