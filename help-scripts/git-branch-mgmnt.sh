
function track-all {
  set -e
  for branch in `git branch -a | grep remotes | grep -v HEAD | grep -v master `; do
    git branch --track ${branch#remotes/origin/} $branch
  done
}

function delete-untracked-files {
  git clean -f -d
}

function clean-merged-branches {
  for branch in `git branch --merged master | grep -v master`; do
    echo "Deleting local branch: $branch"
    git branch -d $branch || break
    echo "Deleting remote branch: $branch"
    git push --delete origin $branch || break
  done
}


git branch --merged master | grep -v master | xargs git branch -D


git branch --no-merged master
