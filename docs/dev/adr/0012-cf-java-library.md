# Use a java CloudFoundry Library

**User Story:** As a Plugin developer I want to access the CloudFoundry provider to get information about services and further details to set deployment stuff like services automatically.

## Considered Alternatives

* Setting the deployment stuff manually
* Send http-request by ourselves
* Java-cf-library: [see here](https://github.com/cloudfoundry/cf-java-client)


## Decision Outcome

* Chosen Alternative: Java-cf-library

## Pros and Cons of the Alternatives <!-- optional -->

### Java-cf-library

* `+` provides many options
* `+` easy authentication process
* `-` new datatypes like `flux`

### Own Http-calls

* `+` only implemented what is useful for our purposes
* `-` Hard to maintain
* `-` need a lot of time to implement


### Setting infos manually

* `+` easy to implement
* `-` not user friendly
