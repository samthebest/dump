
function track-all {
  set -e
  for branch in `git branch -a | grep remotes | grep -v HEAD | grep -v master `; do
    git branch --track ${branch#remotes/origin/} $branch
  done
}

function delete-untracked-files {
  git clean -f -d
}

# NOT WORK
function clean-merged-branches {
  # USE OTHER SCRIPT in help-scripts
}


git branch --merged master | grep -v master | xargs git branch -D


git branch --no-merged master
