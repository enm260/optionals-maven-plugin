[![Codeship Status for enm260/optionals-maven-plugin](https://app.codeship.com/projects/ef323120-19be-0137-b7bf-16c0539076f9/status?branch=master)](https://app.codeship.com/projects/328483)

# Optionals maven plugin
## Overview
Optional dependencies, while useful for enabling/disabling features which may not be needed in all cases, are easy to misuse.  Since the effect of marking a dependency as optional is similar to changing the scope of a dependency there is the possibility of a dependency being marked optional when a scope of provided or even test may be a better solution.  The problem with this is that optional dependencies do not show up in the dependency tree as a transitive dependency so it can be difficult to discover these optional dependencies unless you are already familiar with the project where it was marked optional.  To make matters worse, there is no simple way to restrict optional dependencies (aside from those that that are not specific to optional dependencies such as the enforcer plugin) or even list them from the project which marked them optional.  The goal of this project is to allow optional dependencies to be restricted and to simplify working with optional dependencies in general.

## List optional dependencies
To list the optional dependencies of a project, run:<br/>
`mvn optionals:list`

## Verify optional dependencies
To execute this goal, run:<br/>
`mvn optionals:verify`

This goal relies on a file called `optionals.yml` located in the root of the project.  Any optional dependencies that are found are required to have an entry in `optionals.yml`.  If any are missing, the build will fail with a message indicating which dependencies are missing from the file.  If no optional dependencies are found, then `optionals.yml` is not required and the build will pass.

The purpose of this file is to provide a justification for the dependency being marked optional.  If the dependency is required by an optional feature of the project, the justification will likely be a description of the optional feature which requires the dependency.  Otherwise, the description would likely be the reason why it was marked optional and why alternate solutions such as modifying the scope of the dependency are not appropriate.  This justification can be provided either for a specific artifact or for all artifacts in a certain group.  Example:<br/>

```yml
# Individual artifact entries
org.apache.commons:
  commons-lang3:
    description: 'justification goes here'
  commons-io:
    description: 'also here'

# Group entry
org.apache.commons:
  description: 'this justification is for both commons-lang3 and commons-io'
```

If a description field exists at either level but is blank or contains only whitespace, it will be ignored.  Optionally, a description may be provided for both the artifact AND the group if a summary description for the group is desired in addition to more specific descriptions for the artifacts.

## Generate optionals.yml
To automatically generate optionals.yml based on the optional dependencies that are found, run:<br/>
`mvn optionals:generate`

This will create optionals.yml in the root of your project.  The file will contain an entry for every optional artifact found with a blank description field which you can then fill in with an actual justification for the optional dependency.  If this goal is executed when an optionals.yml file already exists in the project root, any artifacts that are missing from the file will be added (with blank descriptions) and all original entries will be retained.