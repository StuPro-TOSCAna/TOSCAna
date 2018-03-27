# *Use Springfox to automatically generate API Documentation*

**User Story:** *As a developer i want to know how to use the provided REST API*

The documentation of the API is very important, we therefore have to decide on a way to document our REST API 

## Considered Alternatives

* [Springfox](https://springfox.github.io/springfox/)
* Manually created [Swagger](https://swagger.io/) Documentation
* Markdown based API documentation


## Decision Outcome

* Chosen Alternative: *Springfox*
* Springfox allows us to keep the documentation of the API within the java code, we do not need to create a seperate document for it. The Autogeneration is also nice because we don't have to do anything manually once the implementaion is done.

## Pros and Cons of the Alternatives 

### *Springfox*

* `+` The generation of the API documentation can be fully automated `->` API docs can be always up to date
* `+` Commenting of the API docs is done with annotations
* `+` Allows access to the API using `swagger-ui` this results in a quite crude but usable "web ui"
* `-` Natively documenting everything (without the use of workarounds is not possible)
* `-` Annotations have to be learned
* `-` Code will have dead classes (used to model parts of the API)
* `-` Generates a JSON based swaggerfile (not really human readable)

### *Manual Swagger*

* `+` Decent readability
* `+` The resulting documentation can be complete (no workarounds needed)
* `-` Manuall updates of the file needed
* `-` A quite complex DSL (domain specific language) has to be learned
* `-` A major API change will result in a lot of work

### *Markdown*

* `+` Good readability
* `+` The resulting documentation can be complete (no workarounds needed)
* `-` Manuall updates of the file needed
* `-` A major API change will result in a lot of work
