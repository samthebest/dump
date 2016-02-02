
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

## Process Step by Step with Git 

### Backlog -> TODO

How tickets move from the Backlog to TODO is not particularly important from a development point of view, so will not be documented in detail.  This could be via Sprint plannings (Scrum), or via a Kanban approach, whatever, such details are largely irrelevant to the core Agile methodology.

The only requirement is that a ticket has been reduced to the smallest self contained deliverable, that is:

1. If the ticket was released it would be callable, i.e. there exists a way the customer/user can interact with the application that would execute the corresponding code.  If this is not the case, the code is dead code and should not be in production.

2. It is impossible for the ticket to be broken down into N tickets where each satisfies 1.

### TODO -> Doing

If you already have a ticket in Doing, but for whatever reason that ticket is pending/blocked, you should first slide it back to TODO and ensure comments on the ticket explain why it's been moved back.  Otherwise:

1. Create a branch *from master* that uses the ticket reference number as a prefix to the branch name.  In Trello these are just short numbers, in JIRA these are usually a short string that includes a project ID and a ticket number.  For example we may have a ticket "5 add back button", then the branch would be 5-add-back-button (observe lower kebab case convention).
2. Slide the ticket from TODO to Doing and add a comment with the branch name (desirable). Ensure the ticket is assigned to you.
3. Implement the ticket via ATDD, BDD, TDD cycles (TDD may not be possible for languages that don't naturally support unit tests).
4. While you work on the ticket you should regularly merge master into your branch, and backup your branch by pushing it.  If you don't merge master into you branch regularly merge conflicts may become unweildy.
5. When you think you have finished **ask someone to review your branch** by mentioning them on the ticket.  It's nice to use a tool like Intellij, github or crucible to review code, but not necessary. There may be some back and forth to tidy things up between the reviewer and the reviewee.
6. If they are happy the code is good, they should say "I'm happy, review passed" or something on the ticket, and they should perform step 1 in the next section

### Doing -> Ready to release

1. The aforementioned reviewer should login to Jenkins and there should be a "merge feature branch" job or similar, with a parameter that is the branch.  The reviewer should run the job.  This job should:
    - (Desirable) Check that the reviewer is not the assignee of the ticket
    - asdf



























# Adversarial Pairing


# Epics


