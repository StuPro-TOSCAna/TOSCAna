# Use python scripts to provide the deployment of CloudFoundry applications

**User Story:** As an User, I want to deploy my application by one-click script.   
I want to do as few things as possible manually.

## Considered Alternatives

* Let the user set the deployment credentials manually
* Use Bash scripts
* Use python scripts

## Decision Outcome

* Chosen Alternative: python scripts

## Pros and Cons of the Alternatives

### Setting deployment credentials manually by the user

* `+` provides many options for the user
* `+` no effort for the plugin
* `-` not user friendly

### Bash scripts

* `+` no additional language
* `+` one click deployment is possible
* `-` hard to implement if using different data structures like lists

### Python scripts
* `+` well readable language
* `+` easy to implement
* `+` a lot of available libraries
* `+` one click deployment
* `-` user has to install python
