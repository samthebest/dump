# Introduction

This workflow I have found to be the least error prone and provide maximal transparency from having worked in Kanban, XP & Scrum and several teams.

# JIRA

## Quick JQL

### Unfinished tickets

project = PROJECTNAME AND (resolution = Done OR resolution = Resolved OR resolution = Fixed) AND type != Epic AND status != "Won't Do" AND status != Duplicate

### Resolved tickets

project = PROJECTNAME AND ((resolution != Done AND resolution != Resolved AND resolution != Fixed) OR resolution is EMPTY) AND type != Epic AND status != "Won't Do" AND status != Duplicate

## Columns - Sprint Board

We have 5 columns on our JIRA sprint board

1. The "To Do (Sprint Backlog)" column - it is the tickets we wish to complete this sprint

2. The "In Progress" column. Each person in the team should try to stick to only having 1 in progress ticket at a time.  If their ticket becomes blocked and they wish to start another ticket, the blocked ticket should be moved back to To Do and Flagged (see Blockers & Blocked).  If you have a few small tasks which should be done in parallel try not to context switch too often and simply slide the tickets back and forth.  Given that the constant sliding can get annoying, the limit is rule of thumb, not a hard limit.

3. The "In Review" column. When you are heppy your task has been done awesomely, you can comment on the ticket and mention the people you wish to review your ticket, or if it's code, you should create a Crucible review(/github pull request) and add the people you want to review the ticket.  *Make sure you have merged in develop into your branch before submitting for review*. It is inevitable that reviewers will not instantly review, therefore if they take a long time you may slide another ticket into In Progress - but try to avoid this as it under Agile you should really wait until the review is finished before starting more work.  It is better practice to poke the reviewers to hurry up, or just research and read up in the down time.  Under Agile there is nothing saying you can't sometimes take break from pumping out tickets and do some research. Anyway, hopefully there will only be a few small suggestions, and so no need to slide the ticket back to In Progress, but if the review process deems significant work is necessary, the ticket should be slide back to In Progress.*

4. The "Ready to release" column. When it has passed review, slide it here. For some tasks this column doesn't make sense and the ticket can go straight to "Released"

5. The "Released" column - obvious.

*This is why it is so important to break objectives down into small single responsibility tasks - the smaller the task the quicker it is to review, and therefore the less likely that reviewing will block progress.

## Blockers & Blocked

As mentioned, you should Flag a ticket when it is blocked - this seems to be the easiest way in JIRA to make it have a different colour on the board and jump out.  As mentioned tickets should only get blocked in the "To Do" and "Ready to release" columns.

## Creating Tickets & Adding to Sprint

You can create tickets at any time and they will be added to the backlog, please refrain from adding tickets into the Sprint once the Sprint has started unless you have the Scrum Master's approval.  Tickets are ideally to be added to Sprints only in Sprint Planning meetings.

## Justification

The justification for the limit on In Progress tickets is to minimize context switching and enforce a philosophy of "Do one thing, do it well, get it done, push it through".

The justification for the "Ready to release" column is that we won't have a separate DevOps team to control release branches, and therefore there needs to be a clear distinction on the UI and on statuses as to what has been developed, and what has actually been released. Without such a distinction mistakes can be made where a ticket is released because it is believed that a dependency is complete (it is in a "Done" column), but in fact the dependency has not been released. (I personally saw this particular thing happen in a past company which was not detected for 6 weeks, the result was models where being trained off stale data and so the precision dropped significantly.)

There is no column for "In Testing" or "In QA", team members should write automated tests and check their code works themselves before it is submitted for review as part of "In Progress". Tickets from time to time may arise that are of the form "perform release", where a release branch is constructed from the develop branch - that ticket may include additional QA and sanity checking.

It can make sense to add an "In QA" column if your company actually has a seperate QA team.

## Reviews

We use Crucible (/github pull request) to review code. You will then be able to use the JIRA reference number when adding diffs to a review (it's also just good practice for tracking changes).  The review flow works roughly like this:

1. Create review and add reviewers (usually 2)
2. Tell reviewers
3. Reviewers add comments using crucible(/github)
4. You address comments when they have finished, commit, push, and add the new commit(s) to the review
5. Repeat 3 & 4 until reviewers stop making comments and click "complete"
6. Reviewers add general comment with Code Quality Score, we use the WTFPM measure (where 0 is perfect), which is very easy to compute: see http://blog.pengoworks.com/enclosures/wtfm_cf7237e5-a580-4e22-a42a-f8597dd6c60b.jpg

## Branching Model

We will use the git-flow branching model which is concisely documented here: http://danielkummer.github.io/git-flow-cheatsheet/

There are a bunch of nice bash scripts which make using the git-flow branching model super easy, for mac you can install by reading: https://github.com/nvie/gitflow/wiki/Mac-OS-X

For each repository when you try to run a git-flow command it will prompt you to configure it, please use the default branch prefixes (master for production releases and develop for integration of next release).  Actions in git flow should loosely map to actions in JIRA:

1. When you move an issue into "In Progress" for the first time you should start a new feature branch and make sure you remember to name the branch with the ticket reference as a prefix. Use correct naming conventions for branch names, so use hyphenated lower case. If in doubt look around you, this is the best way to determine conventions.  Sometimes it may make sense to do more than one ticket in one branch - in general this should be avoided and may mean the JIRA tickets where created without enough thought with regard to separating concerns and order of completion; the larger the branch the harder it is to review and manage.
2. Every time you commit make sure you prefix the commit message with the ticket reference as prefix - if you do not do this it can break Crucible and it can make producing release notes difficult.
3. When the ticket has passed review, use git flow feature finish to merge it back into develop (you may not be able to do this unless you are a devmaster*). Now slide it to "Ready for release"
4. To release the code (usually the responsibility of devmasters* and sometimes as a seperate ticket if it will involve several features) create a release branch using git flow. Final tests and sanity checks should be made on the release branch and any last quick fixes.  The version number should be bumped (in version.sbt), then use git flow to finish the branch (in future we will write a script (/ use a release framework) to automatically bump the version number upon merge with release).

*Sam is the current devmaster, which means if you are not Sam, please do NOT push develop or master, only Sam should merge branches into develop/master and push them. The devmaster should either review all code, or at least oversee reviews had between other team members.

NOTE: MAKE SURE YOU KEEP YOUR BRANCH UP TO DATE WITH DEVELOP, ESPECIALLY BEFORE SUBMITTING FOR REVIEW. If you diverge from develop it can cause a lot of problems.

----Hotfixes
TODO
