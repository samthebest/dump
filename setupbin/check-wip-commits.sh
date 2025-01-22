#!/bin/bash

set -e

min_message_length=25

echo "DEBUG: Last 16 commits"
git log --pretty=format:"%H %s" | head -16

# TODO This logic assumes the most recent merge commit was *into* develop, which may not be so
# Also, we only look back 64 commits (just to avoid it being slow),
# so we assume no one creates branches with more commits than that!
#
# Much nicer would be to simply use `git log ... develop..HEAD` but that doesn't work
# and gitlab seems to make it really hard to make it work

git log -n 64 --pretty=format:"%H" | while IFS= read -r hash; do
  echo "DEBUG: Checking commit $hash"
  num_parents=`git cat-file -p $hash | grep parent | wc -l`
  if [ "${num_parents}" -gt 1 ]; then
    echo "INFO: Assuming we've reached the merge commit with develop, will stop checking: $hash"
    exit 0
  fi

  length=`git show $hash --quiet --pretty=%B | wc -c`
  if [ "$length" -lt $min_message_length ]; then
    if ! git show $hash --quiet --pretty=%B | grep -qi deploy; then
      echo "ERROR: Commit with very short non-deploy message (less than $min_message_length), \
likely a 'wip' commit or simply not descriptive enough:"
      echo "..."
      git show $hash --quiet
      exit 1
    fi
  fi
done
