# Commit skip SCM filters

This repository contains a collection of traits for several branch-source Jenkins plugins.

It provides filters for both pull requests and/or branches on jobs created from these plugins
 - [GitHub Branch Source](https://github.com/jenkinsci/github-branch-source-plugin)
 - [Bitbucket Branch Source](https://github.com/jenkinsci/bitbucket-branch-source-plugin)

The filtering will be performed, applying it whether it:

- The last commit message contains `[skip ci]` or `[ci skip]`. The check is case-insensitive.
- The last commit message matches a pattern.
- The last commit author matches a pattern.

The commit message contains behavior is configurable to additional strings besides the
defaults provided by the plugin. The strings are treated as literal strings for the
contains option.
