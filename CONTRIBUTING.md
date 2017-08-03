# Contributing

**Table of Contents**
<!-- TOC depthFrom:2 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Versioning](#versioning)
- [Pull Requests](#pull-requests)
	- [Working on a Pull Request](#working-on-a-pull-request)
	- [Reviewing a Pull Request](#reviewing-a-pull-request)
	- [Merging a Pull Request](#merging-a-pull-request)
- [Zenhub Issue Board](#zenhub-issue-board)
	- [Pipelines](#pipelines)
- [Reporting A Bug](#reporting-a-bug)
	- [Before Submitting A Bug Report](#before-submitting-a-bug-report)
	- [How Do I Submit A Bug Report?](#how-do-i-submit-a-bug-report)

<!-- /TOC -->
## Versioning

This project uses [Semantic Versioning](http://semver.org/) or _SemVer_ for short. If you do not know what SemVer is, a detailed explanation is available on their website.
Any new releases must adhere to this versioning scheme.

## Pull Requests
Any major changes to existing or the addition of new features or artifacts should be done through pull requests or PRs.

### Working on a Pull Request
PRs with unfinished features should have a [WIP] tag at the beginning of their title. This shows that this feature is still in progress and not ready to be merged.

### Reviewing a Pull Request
When a feature is considered finished it should be **reviewed** before merging the corresponding PR. During this stage the [WIP] label in the title should be removed.

### Merging a Pull Request
Before a pull request can be merged, it must fullfill the criteria specified in the [Definition of Done](/docs/dev/dod.md).
Merging should be done through the **squash and merge** option in GitHub. This allows all to be combined into one commit on the master branch in order to not clutter up the commit history.

## Zenhub Issue Board
### Pipelines
Zenhub allows the usage of pipelines. These are the pipelines used in this project:

| Pipeline | Contains |
| --- | --- |
| New Issues | New issues. |
| Icebox | Issues that are currently not important. |
| Backlog | All issues selected for the upcoming sprint. |
| In Progress | Issues someone is currently working on. |
| Review/QA | Issues/Pull Requests that are ready to be reviewed. |
| Done | Issues finished in this sprint. |
| Closed | Issues that are done and approved in the sprint review. |

## Reporting a Bug

We use the GitHub Issues to track bugs. If you discovered a bug you can report it there.

**Important:** Before reporting a bug check if it already appeared by searching the issues and read this list on how to submit a bug report.

### Before submitting a Bug Report
- Check if your program setup was correctly installed and configured.
- Check if the bug has something to do with your current OS.
- Search the issues for the bug, if there is an existing open issue create a comment and do not open a new one.

### How do I submit a Bug Report?

- **Use a clear and descriptive title** for the issue to identify the problem.
- **Include which OS you where using.** It is important to know on which os the CLI or Transformator are running.
- **Identify which component produces the bug.**
- **Describe exactly all the steps needed to reproduce** this bug in as many details as possible.
  This makes it easier for others to test if they can reproduce it.
- **Describe what you have observed after doing this steps.**
- **Describe what behavior you expected instead.**
- **Provide the CSAR - Archive.** If the bug appeared in combination to a specific CSAR - archive, provide this archive.
- **Include screenshots.**


For bugs appearing in the web app:
- **Include the Browser and its version.**
