# What is the Retrofit Wrapper?

The Retrofit Wrapper is Java library that allows communication with a running instance of the TOSCAna server by calling the methods provided by the library.

Without messing with the HTTP API, the only thing needed is the HTTP API endpoint to connect to. Everything else involving HTTP calls is implemented in the wrapper.

We currently use the wrapper in our command line interface and in some integration tests.

The wrapper is mostly undocumented, that is because it is not widely used within the project, however the functionality should be pretty self explaining.

# Blocking vs non-blocking

In some case you might want to block a thread, as long as the transformation is running.
That is considered blocking in this context.

There are two classes that the developer can choose from to interact with the server:

- `ToscanaApi` - Provides synchronous calls for every operation supported by the REST API.
All of these methods will return once the Response from the server has been received.
- `BlockingToscanaApi` - Provides the same calls as `ToscanaApi` (it is a child class) with the addition of a blocking call to launch a transformation.

# Usage example

The following example shows how to initialize the wrapper and how to get all available CSARs on the server.

```java
public static void main(String[] args) {
  // initialize the connection
  ToscanaApi api = new ToscanaApi("http://localhost:8084");

  // Retrieve the CSARs
  CsarResources csars = api.getCsars();

  // iterate over the CSARs
  csars.getEmbeddedResources().forEach((k, v) -> {
    System.out.println("CSAR Name: " + v.getName());
  });
}
```
