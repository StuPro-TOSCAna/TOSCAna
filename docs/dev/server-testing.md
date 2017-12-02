# Testing Guidelines for the Server module

This document provides some instructions and guidelines describing how tests should be written,
 the test environment can be setup if the `integration` folder is not recognised as a source folder

## Setting up the Test Environment

### Importing the Integration Test folders

Once the project is imported you might have to import the `resources` directory of the `integration` source folder using:

```
Right Click the Directory src/integration/resources -> "Mark Directory as" -> "Test Resource Root"
```

If the icon of the `src/integration/java` folder icon is not green, you might also have to add the folder as a Test source folder by doing:
```
Right Click the Directory src/integration/resources -> "Mark Directory as" -> "Test Sources Root"
```

### Using the Default Run Configurations

We provide some default Run Configurations for IntelliJ Idea to execute tests. These can be found [here](../config/test-run-configurations).

## Writing Tests for the Server module

### Common

The name of a test case (method) within a class should express the purpose of the test in camel-case as concise as possible.

When using Parameterized tests: Provide a name that contains a short description of the test case. Some examples:

- [API ErrorTest of the Retrofit-Wrapper](/retrofit-wrapper/src/test/java/org/opentosca/toscana/retrofit/api/ErrorTest.java)
- [MapperErrorTest of the BaseImageMapper (Server Module)](/server/src/test/java/org/opentosca/toscana/plugins/kubernetes/docker/mapper/MapperErrorTest.java)
- [MapperTest of the BaseImageMapper (server Module)](/server/src/test/java/org/opentosca/toscana/plugins/kubernetes/docker/mapper/MapperTest.java)

### Unit tests

Unit tests belong in the `src/test` directory. 
A unit test class should inherit from `org.opentosca.toscana.core.BaseUnitTest`. 
If a Spring context is needed use `org.opentosca.toscana.core.BaseSpringTest` instead.

When Writing a test for a Specific class please name the test like this `<Classname>Test`. 
For example if you test the `Transformer` class the corresponding test should be called `TransformerTest`. 
It should also be located within the same package (but in the test folder) as the class that gets tested.

Tests that do not belong to a specific class (or cover a part of a class) should still end with `Test`.


### Integration Tests

Integration tests (and their resources) should be located within the `src/integration` folder.
Please keep in mind that the classpath of the `test` and `ìntegration` folders are shared and names should
be unique even when the folders get combined.

To write integration tests please inherit from the classes
`org.opentosca.toscana.core.BaseSpringIntegrationTest` for spring related integration tests
and `org.opentosca.toscana.core.BaseIntegrationTest` for integration tests that do not need a Spring context.

If you have a integration test that inherits from a class that's not a child of the two classes described above,
use the following annotation on the class definition to make the test class a integration test
```java
import org.opentosca.toscana.IntegrationTest;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class MyIT extends BaseUnitTest {
  //Some Code here
}
```

The Naming of tests should follow similar guidelines as for Unit Tests.
Instead of `Test` the classes should end with `IT`.

## Running the Tests using Maven

To only run unit tests execute:
```bash
mvn test
```

To only run the tests of a specific module run:
```bash
mvn test -pl <module name>
```

To only run integration-tests execute:
```bash
mvn integration-test -P integration-test -pl server
```

or

```bash
mvn verify -P integration-test -pl server
```

To run all tests execute:

```bash
mvn verify -P all
```
