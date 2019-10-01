<!---
 ~ The MIT License
 ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy
 ~ of this software and associated documentation files (the "Software"), to deal
 ~ in the Software without restriction, including without limitation the rights
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 ~ copies of the Software, and to permit persons to whom the Software is
 ~ furnished to do so, subject to the following conditions:
 ~
 ~ The above copyright notice and this permission notice shall be included in
 ~ all copies or substantial portions of the Software.
 ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 ~ THE SOFTWARE.
 -->

# Audit Logging Plugin

Project Status
---
[![Build Status](https://travis-ci.org/jvz/audit-log-plugin.svg?branch=master)][project-build]
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)][license]

#### [Cover Coverage Status][code-coverage]  | [Maven Central Location][maven-central]

## Project Overview

In [Jenkins][jenkins-site], there are several categories of user-initiated actions, such as:
- Starting and/or stopping a Jenkins build.
- Creating, Modifying and/or Deleting a Jenkins Job.
- Performing a change to the Jenkins configuration settings.
- **TODO:** Add some other user-actions

During the operation of Jenkins other events and actions occur which are not initiated by the user, for example:
- Jenkins system agent joins or leaves a Jenkins cluster.
- Authentication and Authorization of a new user succeeds or fails.
- Generation of system events due to a build succeeding or failing.
- Build-specific Jenkins events occur
- **TODO:** Add some others non-user or system actions

The goal of this project is to utilize [Apache Log4j Audit][log4j-audit-site], which is an audit logging framework, to implement the logging of audit events within the Jenkins server.
This requires the following tasks, among other project objectives:
- Identification and definition of the audit events of importance.
- Performing updates to the Jenkins Core system to support any additional event-listeners needed, for example, for acquiring audit information.
- Creation of new audit events, with the provision of customizable interfaces for user-defined configurations. For example;
    - Enable users to configure how audit logs are stored and used.
    - Provide admin settings to configure and add a relational database or local directory to write log files into.
    - Enable users to configure and add an external syslog server.
    - **TODO:** Add some other use cases
- **TODO:** Add more use cases and applications of the plugin

Getting Started: Prerequisites
---------------

+ Make sure you have a [GitHub account][github-site].
+ If you're planning to implement a new feature, it makes sense to discuss your changes
  on [Gitter][project-gitter] first.
  This way you can make sure you're not wasting your time on something that isn't
  considered to be in the audit-logging project's scope.
+ Submit a ticket for your issue, assuming one does not already exist.
  + Clearly describe the issue, including steps to reproduce when it is a bug.
  + Make sure you fill in the earliest version that you know has the issue.
+ Fork the [repository][project-repo] on GitHub, take a look at the [project's issues page][project-issues] and [Wiki][project-wiki].

Getting Started: Setup
----------------------

**TODO:** - how to setup/start the plugin with Jenkins, etc



Usage
----------------------

**TODO:** - demo, screenshot, gifs of running audit logging, etc



Additional Resources
--------------------

+ [General GitHub documentation](https://help.github.com/)
+ [Developing Jenkins Plugins](https://wiki.jenkins.io/display/JENKINS/Extend+Jenkins)
+ [Jenkins Developer Documentation](https://jenkins.io/doc/developer)
+ [Jenkins Main site][jenkins-site]
+ [Jenkins Developer Mailing List](https://jenkins.io/mailing-lists/)
- **TODO:** - Add more resources relevant to the plugin and its development.



[code-coverage]: https://todothis.com
[code-style]: https://logging.apache.org/log4j-audit/latest/javastyle.html
[github-site]: https://github.com/signup/free
[jenkins-site]: http://jenkins.io/
[license]: https://opensource.org/licenses/MIT
[log4j-audit-site]: https://logging.apache.org/log4j-audit/latest/index.html
[maven-central]: https://todothis.com
[new-contributor-guide]: https://wiki.jenkins.io/display/JENKINS/Beginners+Guide+to+Contributing
[project-build]: https://travis-ci.org/jvz/audit-log-plugin
[project-gitter]: https://gitter.im/jenkinsci/outreachy
[project-issues]: https://github.com/jenkinsci/audit-log-plugin/issues
[project-repo]: https://github.com/jenkinsci/audit-log-plugin
[project-wiki]: https://github.com/jenkinsci/audit-log-plugin/wiki
[test-results]: https://todothis.com
