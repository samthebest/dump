import stashy
import sys
import getpass
import subprocess

# pip install stashy before running this

user = sys.argv[1]
project = sys.argv[2]

stash = stashy.connect("https://stash.url", user, getpass.getpass())
repo_list = stash.projects[project].repos.list()
for repo in repo_list:
  # this bit probably won't work, need project to be lowercase
  bashCommand='git clone ssh://git@stash.url:7999/$project/' + repo['name'] + '.git'
  print bashCommand
  subprocess.call(bashCommand, shell=True)
