# Contributing

**Table of Contents**
<!-- TOC depthFrom:2 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Versioning](#versioning)
- [Checkstyle](#checkstyle)
	- [Set up](#set-up)
- [Writing commit messages](#commit-messages)
- [Branches](#branches)
	- [Branch naming guidelines](#branch-naming-guidelines)
- [Pull Requests](#pull-requests)
	- [Working on a Pull Request](#working-on-a-pull-request)
	- [Reviewing a Pull Request](#reviewing-a-pull-request)
	- [Prepare a Pull Request](#prepare-a-pull-request)
- [Zenhub Issue Board](#zenhub-issue-board)
	- [Pipelines](#pipelines)
- [Reporting a Bug](#reporting-a-bug)
	- [Before submitting a Bug Report](#before-submitting-a-bug-report)
	- [How do I submit a Bug Report?](#how-do-i-submit-a-bug-report)

<!-- /TOC -->


## Checkstyle

Our project uses checkstyle to ensure coding standards. If you want to read more about checkstyle visit [http://checkstyle.sourceforge.net](http://checkstyle.sourceforge.net).

### Set up
- for **Eclipse** visit: [Eclipse configuration from the eclipse/winery repo](https://github.com/eclipse/winery/tree/master/docs/dev/config/Eclipse)
- for **IntelliJ** visit: [IntelliJ configuration from the eclipse/winery repo](https://github.com/eclipse/winery/tree/master/docs/dev/config/IntelliJ%20IDEA)

## Writing commit messages 
Make sure your commits have [a good commit message](https://chris.beams.io/posts/git-commit/).

## Branches
Our project currently consists of a _master_ and several _feature_ branches.
The master branch always contains a stable version of the project. Only commits with small, insignificant changes should be done directly on the master branch.

Any other changes should first be implemented and tested on a feature branch. The name of this feature branch should closely describe the added changes. Also take a look at our [Branch naming guidelines](branch-naming-guidelines)

If you want to add your changes to the main branch you can do so by proposing a [pull request](#pull-requests).

### Branch naming guidelines

To organize we have naming conventions for our branches, each branch should start with a `tag` to indicate in which category the branch belongs.

Here is a list of tags that should be used:

- `docs/<name>` - Represents a Branch that covers the documentation of something
- `feature/<name>` - Repesents a branch that will implement a feature (size is irrelevant, the whole CLI or a new Request Mapping for the API are both features)
- `bugfix/<name>` - A branch used to fix a bug
- `misc/<name>` - A branch containing something thats not covered in the above categories

## Pull Requests
Any major changes to existing or the addition of new features or artifacts should be done through pull requests.

### Working on a Pull Request
PRs with unfinished features should have a **[WIP]** tag at the beginning of their title. This shows that this feature is still in progress and not ready to be merged.

### Reviewing a Pull Request
When a feature is considered finished it is necessary to get a **review**.
To get a review, remove the **[WIP]** tag and assign reviewers, also move the pull request to the ZenHub `Review/QA` pipeline.  

Also make sure you're PR is up to date with the master branch.
When the reviewers submitted their review, react to their comments and update your PR. A pull request is ready to merge when the reviewers approved it.

### Prepare a Pull Request
Before a pull request can be merged, it must fulfill the criteria specified in the [Definition of Done](/docs/dev/dod.md).

The goal of the following steps is to get a single commit, containing all differences between the `master` branch and the branch of the pull request.

Steps to prepare the pull request (reference [Winery - Prepare a Pull Request](https://eclipse.github.io/winery/dev/ToolChain#github---prepare-pull-request)):
> Note: If you forked our repository you have to replace `origin` with `upstream`

1. `git fetch origin` - fetches all updates from origin.
2. `git merge origin/master` - merges all the updates from the origin into the local branch.
3. If there are any merge conflicts then resolve them.
4. Commit & Push to ensure there is a back up if something in the following steps goes wrong.
5. `git reset origin/master` - this prepares that all commits can be squashed togheter: The local checkout (“working tree”) is left untouched, but the “pointer” of the current branch is reset to `origin/master`.
6. Check changes in your favourite git tool:
	- Is each of your changes recognized?
	- Are there to much changed files? - Do not stage things you did not intend to change.
	- Check if your Code follows our style guidelines.
7. `git add .` - stage your changes for the commit.
8. `git commit` - commit your changes with a meaningfull title and description.
9. Force push your changes with `git push -f` to overwrite the remote commits.

You dont have to use the `git reset` - Method you also can use `git rebase -i <commit-id>` (see [stackoverflow](https://stackoverflow.com/questions/5189560/squash-my-last-x-commits-together-using-git)) to squash your commits. Rebasing instead of resetting eases writing the resulting commit message, as you can simply rephrase all commit messages of the PR into one final commit message.


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
- **Include which OS you where using.** It is important to know on which OS the CLI or transformer are running.
- **Identify which component produces the bug.**
- **Describe exactly all the steps needed to reproduce** this bug in as many details as possible.
  This makes it easier for others to test if they can reproduce it.
- **Describe what you have observed after doing this steps.**
- **Describe what behavior you expected instead.**
- **Provide the CSAR - Archive.** If the bug appeared in combination to a specific CSAR - archive, provide this archive.
- **Include screenshots.**


For bugs appearing in the web app:
- **Include the Browser and its version.**

## Versioning

This project uses [Semantic Versioning](http://semver.org/) or _SemVer_ for short. If you do not know what SemVer is, a detailed explanation is available on their website.
Any new releases must adhere to this versioning scheme.
