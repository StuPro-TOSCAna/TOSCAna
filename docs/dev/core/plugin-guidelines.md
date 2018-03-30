## Plugin Loading

We use Springs Annotation based dependency injection to load the Plugins.
A class that implements or extends the interface / class listed above also needs a `@Component` annotation to let Spring auto construct an instance.
This also means that the class needs a public constructor with no arguments, auto wiring other dependencies is possible but it is not required for any operations between plugin and core.
However you might need special components that get loaded using spring, using dependency injection with them works.
For any Interactions between the core and the transformation you should use the given operation context (`TransformationContext` for transformations).

Currently Spring Boot only looks for `@Component` annotations on classes in the package `org.opentosca.toscana` and it's child packages.

Therefore the main plugin class has to be located within this package hierarchy.

## The `ToscanaPlugin` class

The following section describes the implementation of a plugin using the abstract class `ToscanaPlugin`.

Since we use the TLI (TransformationLifecycleInterface) now for every plugin, the original possibility to build a low-level plugin is removed, you have to realize a Plugin based on the TLI for that you will have to implement the `ToscanaPlugin` class where T is your class name of your lifecycle implementation.

We use a factory method (`getInstance()`) to build an instance of your lifecycle implementation for every transformation that gets performed.

You have to create a platform object in the default constructor (the constructor should either be empty or just contain dependencies that will get loaded using springs dependency injection)
a simple solution for this problem is a private static method that will construct the platform object with the corresponding platform specific inputs.

### Additional Implementations

#### Initialisation method

The implementation of the initialisation methods (`init()`) is optional. It gets executed during construction of the object.

#### Setting Platform specific inputs (properties)

In order to perform a transformation, you might need to be able to get some inputs from the user.
The platform specific inputs get set in the `Platform` object that has to be passed to the `super()` constructor.

### Performing the transformation

When starting a transformation the `TransformationService` launches an asynchronous task (`ExecutionTask`) that calls the `transform(TransformationContext)` method of the corresponding plugin.
This results in the execution of the lifecycle phases in a separate thread, the current implementation only supports the executon of one transformation at the time, however this can easily be changed by exchanging the default executor in the `TransformationService` with a multi threaded version.
Transformations will get queued if the transformer is not idling and currently runs a transformation.

This method gets wrapped in the `ToscanaPlugin` class that is responsible for creating the lifecylce instance for the current transformation and execute each phase one after another.

Once the Transformation has been done the `PluginFileAccess` object provided by the `TransformationContext` can be used to get file paths to which you can write the resulting target artifacts.
These artifacts will be taken by the `server-component` in order to compress them and serve them using `HTTP` (the REST API). For more information about the `PluginFileAccess` please take a look at its javadoc.

#### TransformationContext

The `TransformationContext` is provided by the server component and supplies everything needed to perform the Transformation.
Including the `PropertyInstance`, containing all inputs and the values set by the user.

As well as a special logger factory method that should be used to ensure transformation specific logging (see [#Logging](Logging))

#### Logging

Because we want to be able to only log the events for a specific transformation in a special file and by the REST API a special logger factory has to be used if you want to log while the transformation is running.
Using this feature is highly recommended because we don't have any other option that tells the user whats going on with the transformation.

#### Handling exceptions

Because the phase methods of the TransformationLifecycle do not have a `throws` declaration every exception that is not a `RuntimeException` will have to get caught.
If you want the transformation to fail if an exception gets thrown you should rethrow the exception, wrapped in a `TransformationFailiureException` (extends `RuntimeException`)

#### Using multiple threads within a transformation

A transformation should be considered to be single threaded.
 You should therefore not launch any extra threads.
