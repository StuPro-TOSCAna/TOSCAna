# Plugin Develoment Guidelines

Transformation Plugins for the TOSCAna transformator have to implement the `TransformationPlugin` interface. However it is recomended to extend the abstract class `AbstractPlugin`.

## Plugin Loading

We use Springs Annotation based dependency injection in order to load the Plugins. A class that implements or extends the interface / class listed above also needs a `@Component` annotation to let Spring autoconstruct a instance. This also means that the class needs a public constructor with no arguments, Autowireing other dependencies is possible but it is not required for any operations between plugin and core. For any Interactions between these the given operation context (`TransformationContext` for transformations)  

Currently Spring Boot is setup to only look for `@Component` annotations on classes in the package `org.opentosca.toscana` and it's child packages. Therefore the main plugin class has to be located within this package hierarchy.

## The `AbstractPlugin` class

The following section describes the implementation of a plugin using the abstract class `AbstractPlugin`. 

### Required Implementations

The following two methods have to be implemented in order to contruct a corresponding `Platform` object.
```Java
public abstract String getName();
public abstract String getIdentifier();
```
The `getName()` method returns the Display name of the plugin and the `getIdentifier()` method returns the unique identifier (matching the regular expression `[a-z_-]+`). If a plugin with the same identifier is already registered launching of the application will not continue and the application will stop (throwing a `IllegalArgumentException`)

### Addititional Implementations

#### Initialisation method

The implementation of the initialisation methos (`init()`) is optional. It gets executed during the construction of the object. 

#### Setting Platform specific properties

In order to perform a transformation, you might need to be able to get some properties from the user (for example you might need to define what Docker Registry to use). if something like that is necessary you can overwrite the `getPluginSpecificProperties()` returning a set of properties that you need in order to perform any transformation (and later: deployments). The method always has to return a Set, if no properties get definid the set is empty

### Performing the transformation

When starting a transformation for a platform the `TransformationService` launches a asynchronous task that calls the `transform(TransformationContext)` method of the corresponding plugin. Therefore this method will perform the transformation. 

#### TransformationContext

The `TransformationContext` is provided by the core component and contains everything needed to perform the Transformation. Including the `PropertyInstance`, containing all properties and the values set by the user. As well as a special logger factory method that should be used in order to ensure transformation specific logging (see [#Logging](Logging))

#### Logging