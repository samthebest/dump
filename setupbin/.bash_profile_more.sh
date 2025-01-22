# Currently most of this gets populated from my setup scripts, should change my script to just pull this file.

# bash profile already setup
export EDITOR=nano
HISTFILESIZE=10000000
shopt -s histappend
PROMPT_COMMAND="history -a; "
HISTSIZE=1000000
HISTFILESIZE=2000000
export PS1='$(whoami)@$(hostname):$(pwd) '


export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home

function json {
  python -m json.tool
}

function gh {
    cat ~/.bash_history | grep $*
}

if [ -f ~/.git-completion.bash ]; then
  . ~/.git-completion.bash
fi

eval "$(/opt/homebrew/bin/brew shellenv)"
alias pt='open ~/pro.terminal'
alias sbp="subl ~/.bash_profile"
alias dbp="source ~/.bash_profile"
alias cbp="cat ~/.bash_profile"
alias gs="git status"
alias gp="git pull origin develop"
alias gm="git pull origin master"



function squash_until {
  (
    set -e
    commit="$1"
    message="$2"
    branch=`git branch --show-current`
    tmp_branch="${branch}-tmp-for-squash"
    echo "INFO: Reseting ..."
    git reset --soft $1
    git checkout -b "${tmp_branch}"
    echo "INFO: committing ..."
    git commit -am "${message}"
    echo "INFO: deleting ..."
    git branch -D "${branch}"
    read -p "Sure you want to run: 'git push origin :${branch}'?: " answer
    if [ "$answer" == "yes" ]; then
      git push origin :${branch}
      echo "INFO: recreating ..."
      git checkout -b "${branch}"
      git branch -D "${tmp_branch}"
      echo "INFO: pushing ..."
      git push origin "${branch}"
      echo "INFO: done"
    else
      echo "Aborting"
    fi
  )
}

function last_merge_commit {
  git log -n 1024 --pretty=format:"%H" | while IFS= read -r hash; do
    num_parents=`git cat-file -p $hash | grep parent | wc -l`
    if [ "${num_parents}" -gt 1 ]; then
      echo $hash
      break
    fi
  done
}

# squash until last merge commit, using $1 as the commit message
function squash {
  squash_until `last_merge_commit` $1
}

# apply all changes on develop in a single commit, , failing if there are merge conflicts
function rebase_and_squash {
  (
    set -ex
    branch=`git rev-parse --abbrev-ref HEAD`
    git reset --soft `last_merge_commit`
    git stash
    git checkout develop
    git pull origin develop
    git checkout -b ${branch}-rebased
    set +e
    git stash pop
    set -e
    exit_code=$?
    if [ "${exit_code}" != 0 ]; then
      echo "ERROR: FIX CONFLICTS"
    else
      git commit -am "${1}"
    fi
  )
}

# the commit immediately after the given commit
function commit_after {
  git log --ancestry-path --format="%H" ${1}..HEAD | tail -1
}

function sq {
  commit="$1"
  if [ $commit == "" ]; then
    echo "ERROR: hash is empty"
  else  
    commit_plus_one=`commit_after $commit`
    message=`git log --format="%s" -n 1 $commit_plus_one`
    squash_until $commit "$message"
  fi
}

function git_undo {
  git reset -- $1
  git co -- $1
}


function old_branches {
  num_weeks_old=8
  threshold_date=$(date -v-${num_weeks_old}w -u +%Y-%m-%dT%H:%M:%S)
  # Iterate through all local branches
  for branch in $(git for-each-ref --format="%(refname:short)|%(committerdate:iso-strict)" refs/heads/); do
    # echo "branch = $branch"
    branch_name=$(echo $branch | cut -d'|' -f1)
    commit_date=$(echo $branch | cut -d'|' -f2)
    # echo "branch_name = $branch_name"
    # echo "commit_date = $commit_date"
    
    # Compare the commit date with the threshold date
    if [ "$commit_date" \< "$threshold_date" ]; then
      echo "Old branch: $branch_name"
      if [ "$1" = "delete" ]; then
        read -p "Sure you want to delete: 'git branch -D $branch_name'?: " answer
          if [ "$answer" == "y" ]; then
            git branch -D $branch_name
          else
            echo "Skipping"
          fi
      fi  
    fi
  done
}


function nest_project {
  dir=$1
  file_list=/tmp/${dir}-file-list
  mkdir $dir
  exit_code=$?
  if [ $exit_code != 0 ]; then
    echo "ERROR: Could not create directory"
  else
    ls -a | grep -vE "^\.git$" | grep -vE "^\.$" | grep -vE "^\.\.$" | grep -vE "^${dir}$" > $file_list
    while read line; do
      echo "INFO: Running: mv $line ${dir}/"
      mv $line ${dir}/  
    done < $file_list
  fi
}
