# *Use the Spring Framework for the `server` component*

**User Story:** *As a user i want to access the features of the transformer using a HTTP based REST API*

Because we need to implement a REST API (Defined in the requirements document) we have to find a Library or framework that allows us to implement a REST API

## Considered Alternatives

* [Spring](https://spring.io) Framework (Spring Web)
* [Spark Java](http://sparkjava.com/) (short: Spark)

## Decision Outcome

* Chosen Alternative: *Spring Framework*
* The Spring Framework is used because it can be used to stitch together the whole application, whereas Spark just provides HTTP Mapping functionality. Many of the Linking operations done by the Spring Framework would have to be done manually, resulting in a lot of unecessary "boilerplate" code.

## Pros and Cons of the Alternatives <!-- optional -->

### *Spring Framework*

* `+` Provides a huge featureset (Dependency Injection, Logging, Serialization...)
* `+` Widely used (quite popular)
* `+` Provides methods to test API methods (`MockMvc`)
* `+` (almost) no boilerplate code
* `+` There are lots of guides and docs about Spring out there
* `-` quite complex
* `-` Packaged applications get very large (40 MB and more)
* `-` Annotation based (quite a lot of domain specific annotations have to be understood)
* `-` Long loading times

### *Spark Java*

* `+` Quite lightweight (about 10 MB with the integrated [Jetty](https://www.eclipse.org/jetty/) server)
* `+` Faster loading times (compared to Spring)
* `+` Method based mapping of HTTP calls (using lambdas)
* `-` No more features `->` This library just handles HTTP mappings
* `-` Seperate JSON mapper (serializer) needed
* `-` Can be considered (almost) bare metal
* `-` Seperate HTTP Testing library needed
* `-` High chance of having loads of boilerplate code
* `-` Documentation needs improvement (leaves lots of open questions)
