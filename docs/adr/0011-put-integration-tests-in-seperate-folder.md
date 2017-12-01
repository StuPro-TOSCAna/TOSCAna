# Put Integration Tests in seperate source directory

In order to get a better structure in the `server` module we want to reorganize the tests (Seperate Integration and Unit tests)

## Considered Alternatives

* Use JUnit Categories and the same `test` directory to seperate between Integration and Unit Tests
* Store Integration and Unit Tests in seperate directories and use JUnit Categories to categorize them
* Store Integration and Unit Tests in seperate directories and use a required Suffix on Integration Test Classes
* Migrate project to gradle

## Decision Outcome

* Chosen Alternative: Store Integration and Unit Tests in seperate directories and use JUnit Categories to categorize them
* While still Clearly seperating Unit and Integration tests this method still allows a fine grained categorisation of tests.
* Consequences: The resource directory of the `ìntegration` folder has to be manually set within the IDE

## Pros and Cons of the Alternatives <!-- optional -->

### Use JUnit Categories and the same `test` directory to seperate between Integration and Unit Tests

* `+` Easy to setup
* `+` Allows even finer Categories
* `-` Hard to differentiate between Integration and Unit tests

### Store Integration and Unit Tests in seperate directories and use JUnit Categories to categorize them

* `+` Allows even finer Categories
* `+` Clear differentiation between integration and unit Tests
* `-` When compiling everything is thrown together

### Store Integration and Unit Tests in seperate directories and use a required Suffix on Integration Test Classes

* `+` Clear differentiation between integration and unit Tests
* `-` integration tests have to contain a certain string like a prefix
* `-` only allows two categories
* `-` When compiling everything is thrown together

### Migrate project to gradle

* `+` Allows the definition of source sets to completely seperate integration and unit tests
* `+` Can be scripted using Groovy or Kotlin
* `-` Requires complete rewrite of the build mechanism
* `-` Requires training
