# No deployment while transformation

**User Story:** As an orchestrator, I want to fill the target artifact with all information needed for deployment afterwards.

## Problem
Most of the credentials/information of the environment (like a service address) are only available as soon the application is deployed.

## Considered Alternatives

* Deploy the application during transformation to get the credentials
* Create scripts to read out the credentials during deployment (after transformation)

## Decision Outcome

* Chosen Alternative: creating scripts

## Pros and Cons of the Alternatives

### Deployment during transformation

* `+` plugin is able to get the credentials
* `+` target artifact contains all information for deployment
* `-` user maybe just want to transform and not to deploy
* `-` target artifact is not portable
* `-` if service on provider is changed, the user has to transform again

### Create scripts

* `+` separation of transformation and deployment
* `+` target artifact is portable
* `+` target artifact is reusable
* `-` more effort to implement
* `-` scripts has to be created in a generalized way
