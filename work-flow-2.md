
# Glossary

Task Tracker = An Agile task tracker with a horizontal board, like Trello or JIRA (nearly all other task/ticker trackers suck)

# Fundemental Principles

 - To ensure that an action on the Task Tracker loosly corresponds to an actions in git
 - To ensure than a deployment to an environment corresponds to an action in the Task Tracker and git
 - Done means DONE! http://www.allaboutagile.com/agile-principle-7-done-means-done/
 - To ensure rollback is easy
 - To ensure hotfixing is easy
 - To ensure we always know exactly what code is on production
 - To automate as much as possible so that releases are easy, regular and stress free
 - To be as simple as possible, to minimize unnecessary steps, branches, columns, etc

# Work Flow

## Board

The board should have the following columns:

 - **Backlog / Icebox**: A long list of placeholders for work that needs to be done, not necessarily analysed, nor fleshed out
 - **TODO**: A list of tickets that the team has decided need to be done.  Hopefully some details should be fleshed out on the ticket sufficient for development to start.
 - **Doing**: In progress tickets, in progress includes development, testing and code review.
 - **Ready to release**: A ticket that has been developmed, tested and reviewed and is ready to release
 - **Done**: Released tickets

## Process Step by Step

### Backlog -> TODO

How tickets move from the Backlog to TODO is not particularly important from a development point of view, so will not be documented in detail.  This could be via Sprint plannings (Scrum), or via a Kanban approach, whatever, such details are largely irrelevant to the core Agile methodology.  The core Agile methodology applies to how tickets get from **TODO** to **Done**.

The only requirement is that a ticket has been reduced to the smallest self contained deliverable, that is:

1. If the ticket where released it would be callable, i.e. there exists a way the customer/user can interact with the application that would execute the corresponding code.  If this is not the case, the code is dead code and should not be in production.

2. It is impossible for the ticket to broken down into N tickets where each satisfies 1.








# Epics


