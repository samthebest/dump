#!/bin/bash

# TODO Refactor this

if [ $1 != "-am" ]; then
	echo "Please use -am"
	exit 1
fi

project=SAS

BRANCH_FILE=/tmp/current-git-branch-from-commit-script.txt
git branch | grep "*" | cut -c 3- > $BRANCH_FILE

cat $BRANCH_FILE | grep ${project}-[0-9]- > /dev/null
one_digit=$?
cat $BRANCH_FILE | grep ${project}-[0-9][0-9]- > /dev/null
two_digit=$?
cat $BRANCH_FILE | grep ${project}-[0-9][0-9][0-9]- > /dev/null
three_digit=$?
cat $BRANCH_FILE | grep ${project}-[0-9][0-9][0-9][0-9]- > /dev/null
four_digit=$?

echo $one_digit $two_digit $three_digit $four_digit | grep 0 > /dev/null
NAME_CORRECT=$?

if [ $NAME_CORRECT = "1" ]; then
	echo "ERROR: Cannot use commit script, either you are on develop or master"
	echo "or you named your branch incorrectly"
	exit 1
fi

feature=false
hotfix=false
release=false

cat $BRANCH_FILE | grep ^feature > /dev/null && feature=true 
cat $BRANCH_FILE | grep ^hotfix > /dev/null && hotfix=true 
cat $BRANCH_FILE | grep ^release > /dev/null && release=true

excluding_branch_type="NA"

if [ $feature = true ]; then
	excluding_branch_type=`cat $BRANCH_FILE | cut -c 9-`
fi

if [ $hotfix = true ]; then
	excluding_branch_type=`cat $BRANCH_FILE | cut -c 8-`
fi

if [ $release = true ]; then
	excluding_branch_type=`cat $BRANCH_FILE | cut -c 9-`
fi

DIGITS=0

if [ $one_digit = "0" ]; then
	DIGITS=1
fi

if [ $two_digit = "0" ]; then
	DIGITS=2
fi

if [ $three_digit = "0" ]; then
	DIGITS=3
fi

if [ $four_digit = "0" ]; then
	DIGITS=4
fi

CUT_AMOUNT=`expr 12 + $DIGITS`

TICKET_REF=`echo $excluding_branch_type | cut -c -$CUT_AMOUNT`

echo "INFO: Will use the following ticket ref for commit message prefix: $TICKET_REF"
git commit $1 "$TICKET_REF $2"
