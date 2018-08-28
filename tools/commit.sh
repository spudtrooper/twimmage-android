#!/bin/sh
#
# Adds, commits, and pushes to master.
#
set -e

git add .
git commit -am "$@"
#git remote add origin https://github.com/spudtrooper/twimmage-android.git
git push -u origin master
