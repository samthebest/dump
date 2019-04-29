#!/bin/bash

set -e

git checkout master
git pull origin master

function track-all {
  for branch in `git branch -a | grep remotes | grep -v HEAD | grep -v master `; do
    git branch --track ${branch#remotes/origin/} $branch
  done
}

track-all

for branch in `git branch --merged master | grep -v master`; do
  git merge master
  git diff master --stat
  git push origin $branch
  differences=`git diff master --stat | wc -c`
  if [ "$differences" = "0" ]; then
    echo "Deleting local branch: $branch"
    git branch -d $branch || break
    echo "Deleting remote branch: $branch"
    echo "DRY RUN"
    #git push --delete origin $branch || break
  fi
done

