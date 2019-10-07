Contributing to the Audit Log Project
--------------------------

We accept Pull Requests via GitHub. The [gitter][project-gitter] is the
main channel of communication for contributors.

Before you dig right into the code, there are a few guidelines that we need
contributors to follow so that we can have a chance of keeping on top of
things and it makes applying PRs easier for us:
+ Create a topic branch from where you want to base your work (this is usually the master branch).
  Push your changes to a topic branch in your fork of the repository.
+ Make commits of logical units.
+ Respect the original code style: by using the same [codestyle][code-style], patches should only highlight the actual difference, not being disturbed by any formatting issues:
  + Only use spaces for indentation.
  + Create minimal diffs - disable on save actions like reformat source code or organize imports.
    If you feel the source code should be reformatted, create a separate PR for this change.
  + Check for unnecessary whitespace with `git diff --check` before committing.
+ Make sure your commit messages are in the proper format. Your commit message should contain the key of the JIRA issue.
```
[JENKINS-XXX] - Subject of the JIRA Ticket

 Optional supplemental description.
```
+ Make sure you have added the necessary tests (JUnit/IT) for your changes.
+ Run all the tests with `mvn -Prun-its verify` to assure nothing else was accidentally broken.
+ Submit a pull request to the repository in the Jenkins project.
+ Update your JIRA ticket and include a link to the pull request in the ticket.
+ For changes of a trivial nature to comments and documentation, it is not always necessary to
create a new ticket in JIRA.  In this case, it is appropriate to start the first line of a commit
with '(doc)' instead of a ticket number.
