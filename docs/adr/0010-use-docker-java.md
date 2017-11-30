# Use Docker-Client Library to interact with a Docker Daemon

**User Story:** As a Plugin developer I want to access the Docker Deamon for building dockerfiles or starting containers

When trying to build Dockerfiles you have to access a running instance of the Docker Daemon. This decides how this will be done in the code.

## Considered Alternatives

* *Docker-Client Library*: [See Here](https://github.com/spotify/docker-client)
* *Docker CLI Calls*
* *Manually Connect to the Docker Daemon*

## Decision Outcome

* Chosen Alternative: Docker-Client Library
* The Library is quite simple to use, and does allow the mocking of some parts. The maintainabiliy is also better, compared to the other options.

## Pros and Cons of the Alternatives <!-- optional -->

### Docker-Client Library

* `+` Allows Access to the Docker Daemon using method calls
* `+` Can be mocked (easier than the other options)
* '+' Allows the connection to other Docker daemons by changing the configuration of a client object
* `-` Not the best Documentation

### Docker CLI Calls

* `+` The Quality of the Documentation depends on us.
* `-` Hard to maintain
* `-` Possible issues with platform independency
* `-` Connection to other Docker Daemons is hard


### Manually Connect to the Docker Daemon

* `-` Highly Complex
* `-` Even harder to maintain
