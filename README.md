# Audit Logging Plugin

Project Status
---
[![Build Status](https://travis-ci.org/jvz/audit-log-plugin.svg?branch=master)](https://travis-ci.org/jvz/audit-log-plugin)

#### License [license] | Cover Coverage Status (https://todothis.com) | Maven Central Location (https://todothis.com)

## Project Overview

In [Jenkins][jenkins-site], there are several categories of user-initiated actions, such as:
- Starting and/or stopping a Jenkins build.
- Performing a change to the Jenkins configuration settings.
- TODO: Add some other user-actions

As well as actions not initiated by the user, such as by the Jenkins system, for example:
- Jenkins system agent joins or leaves a Jenkins cluster.
- TODO: Add some others non-user or system actions

The goal of this project is to utilize [Apache Log4j Audit](https://logging.apache.org/log4j-audit/latest/index.html),
which is an audit logging framework, to implement the logging of audit events within the Jenkins server.
This requires the following tasks, among other project objectives:
- Identification and definition of the audit events of importance.
- Performing updates to the Jenkins Core system to support any additional event-listeners needed, for example, for acquiring audit information.
- Creation of new audit events, with the provision of customizable interfaces for user-defined configuration. For example;
    - Enable users to configure how audit logs are stored and used.
    - Provide admin settings to configure a relational database or local directory to write log files into.
    - TODO: Add some other use cases
- TODO: Add more use cases and applications of the plugin

Getting Started: Prerequisites
---------------

+ Make sure you have a [JIRA account](https://issues.apache.org/jira/).
+ Make sure you have a [GitHub account](https://github.com/signup/free).
+ If you're planning to implement a new feature, it makes sense to discuss your changes
  on [Gitter](https://gitter.im/jenkinsci/outreachy) first.
  This way you can make sure you're not wasting your time on something that isn't
  considered to be in the audit-logging project's scope.
+ Submit a ticket for your issue, assuming one does not already exist.
  + Clearly describe the issue, including steps to reproduce when it is a bug.
  + Make sure you fill in the earliest version that you know has the issue.
+ Fork the [repository][project-repo] on GitHub, take a look at the [project's JIRA page][project-jira] and [Wiki]([audit-log-wiki]).

Getting Started: Setup
----------------------

#### TODO - how to setup/start the plugin with Jenkins, etc



Usage
----------------------

#### TODO - demo, screenshot, gifs of running audit logging, etc


Contributing to the Audit Log Project
--------------------------

We accept Pull Requests via GitHub. The [gitter](https://gitter.im/jenkinsci/outreachy) is the
main channel of communication for contributors.

Before you dig right into the code, there are a few guidelines that we need
contributors to follow so that we can have a chance of keeping on top of
things and it makes applying PRs easier for us:
+ Create a topic branch from where you want to base your work (this is usually the master branch).
  Push your changes to a topic branch in your fork of the repository.
+ Make commits of logical units.
+ Respect the original code style: by using the same [codestyle][code-style],
  patches should only highlight the actual difference, not being disturbed by any formatting issues:
  + Only use spaces for indentation.
  + Create minimal diffs - disable on save actions like reformat source code or organize imports.
    If you feel the source code should be reformatted, create a separate PR for this change.
  + Check for unnecessary whitespace with `git diff --check` before committing.
+ Make sure your commit messages are in the proper format. Your commit message should contain the key of the JIRA issue.
```
[MCOMPILER-XXX] - Subject of the JIRA Ticket
 Optional supplemental description.
```
+ Make sure you have added the necessary tests (JUnit/IT) for your changes.
+ Run all the tests with `mvn -Prun-its verify` to assure nothing else was accidentally broken.
+ Submit a pull request to the repository in the Apache organization.
+ Update your JIRA ticket and include a link to the pull request in the ticket.
+ For changes of a trivial nature to comments and documentation, it is not always necessary to
create a new ticket in JIRA.  In this case, it is appropriate to start the first line of a commit
with '(doc)' instead of a ticket number.



Additional Resources
--------------------

+ [General GitHub documentation](https://help.github.com/)
+ [Developing Jenkins Plugins](https://wiki.jenkins.io/display/JENKINS/Extend+Jenkins)
+ [Jenkins Developer Documentation](https://jenkins.io/doc/developer)
+ [Jenkins Main site][jenkins-site]
+ [Jenkins Developer Mailing List](https://jenkins.io/mailing-lists/)


[license]: https://todothis.com
[new-contributor-guide]: https://wiki.jenkins.io/display/JENKINS/Beginners+Guide+to+Contributing
[jenkins-site]: http://jenkins.io/
[code-style]: https://logging.apache.org/log4j-audit/latest/javastyle.html
[audit-log-wiki]: https://todothis.com
[project-repo]: https://github.com/jenkinsci/audit-log-plugin
[project-jira]: https://issues.jenkins-ci.org/browse/JENKINS-54082
[test-results]: https://todothis.com
[build]: https://needtodo.com
