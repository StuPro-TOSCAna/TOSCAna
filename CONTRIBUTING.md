# Contributing

**Table Of Contents**
<!-- TOC depthFrom:2 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Versioning](#versioning)
- [Pull Requests](#pull-requests)
	- [[WIP] - Tag](#wip-tag)
- [Zenhub Issue Board](#zenhub-issue-board)
	- [Pipelines](#pipelines)
- [Reporting A Bug](#reporting-a-bug)
	- [Before Submitting A Bug Report](#before-submitting-a-bug-report)
	- [How Do I Submit A Bug Report?](#how-do-i-submit-a-bug-report)

<!-- /TOC -->
## Versioning

Our project uses [SemVer](http://semver.org/) for versioning. If you do not know what _SemVer_ is, please read the docs on their page.

## Pull Requests

### [WIP] - Tag
The [WIP]-tag in a pull request shows that this feature is still in progress and not ready to merge. This has the advance that you can create a PR to use the CI-tools or the PR-review feature but is clearly visible that this PR is not ready to be merged.

**tl;dr** If a pull request got the [WIP]-tag in the title it is not allowed to merge it.

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

## Reporting A Bug

We use the GitHub Issues to track bugs. If you discovered a bug you can report it there.

**Important:** Before reporting a bug check if it already appeared by searching the issues and read this list on how to submit a bug report.

### Before Submitting A Bug Report
- Check if your program setup was correctly installed and configured.
- Check if the bug has something to do with your current OS.
- Search the issues for the bug, if there is an existing open issue create a comment and do not open a new one.

### How Do I Submit A Bug Report?

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
