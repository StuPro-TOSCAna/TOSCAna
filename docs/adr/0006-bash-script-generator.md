# Build a bash script generator 

In the transformation step we have to create scripts to build and deploy the generated artifacts.

## Considered Alternatives

* Build a general bash script generator
* let the plugins build bash scripts by themselves

## Decision Outcome

* Chosen Alternative: *Build a general bash script generator*
* *Comes out best (see below)*

## Pros and Cons of the Alternatives

### *Build a general bash script generator*

* `+` provides components everyone can use
* `+` plugins do not implement the same thing multiple times
* `+` script components managed and tested in one place
* `+` scripts are working the same for every platforms target artifact
* `-` there are plugin specific components

### *let the plugins build bash scripts by themselves*

* `+` plugin specific script components are "private"
* `-` duplicated code
* `-` every plugin has its own *script style*
