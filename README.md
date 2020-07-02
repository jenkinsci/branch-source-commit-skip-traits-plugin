# Commit skip SCM filters

This repository contains a collection of traits for several branch-source Jenkins plugins.

It provides filters for both pull requests and/or branches on jobs created from these plugins
 - [GitHub Branch Source](https://github.com/jenkinsci/github-branch-source-plugin)
 - [Bitbucket Branch Source](https://github.com/jenkinsci/bitbucket-branch-source-plugin)

The filtering will be performed, applying it whether it:

- The last commit message contains "[skip ci]" or "[ci skip]". The check is case-insensitive.
- The last commit message matches a pattern.
- The last commit author matches a pattern.

## Environment

The following build environment is required to build this plugin

* `java-1.8` and `maven 3.5.0+`

## Build

To build the plugin locally:

```bash
mvn clean verify
```
