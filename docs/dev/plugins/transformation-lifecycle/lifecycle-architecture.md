# Transformation Lifecycle Architecture

## Introduction

This document and the corresponding diagrams represent a potential architecture for the lifecycle operations of a transformation described in Pull Request [#159](https://github.com/StuPro-TOSCAna/TOSCAna/pull/159). It was expected, that a transformation has to support the following operations (descriptions have been taken from [here](transformation-lifecycle.md)):

1. `validate()` - The validation phase performs several checks to ensure that the transformation can even be executed (for details see [Validation Phase](#validation-phase))
3. `prepare()` - in this step the plugin processes the graph to transform it in the following step. For example the kubernetes plugin needs to split the graph into container and pods.
4. `transform()` - this is where the real transformation is happening.
5. `clean()` - in this phase the plugin cleans up remaining leftovers from the previos steps. This can be for example files generated during the transformation that are not part of the target artifact.

The lifecyle approach is resolving several general issues:

- Splitting the transformation into tasks (lifecycle operations) allows a very simple progress calculation. (More information will follow below)
- Simplifies the plugin implementation in terms of code size because common parts can get externalized (removes boilerplate code)
- The plugins structure gets way clearer.

The TransformationLifecycleInterface (short: TLI) has methods that represent each task these methods get called in the sequence that was defined in PR Pull Request [#159](https://github.com/StuPro-TOSCAna/TOSCAna/pull/159) (or above).

The creation is done using a factory method that has to be implemented by all subclasses of the `BasePlugin` (currently called `AbstractPlugin`) to produce a Plugin Specific instance of the TLI interface.

## Validation Phase

The validation phase can be split into three different validation phases:
- `environment check` - in this phase the plugin should check if it has everything available to perform the transformation. This means that all required CLI applications have been installed and are accessible. For example: The kubernetes plugin needs a connection to a running docker daemon to perform the transformation (when following the point that docker images get built automatically)
- `supported node type check` - this represents the original `analyze` phase. The plugin will return a list of supported node types, this list then gets compared with a list conatining all node types used by this platform. If not all node types of the model are contained in the supported list the transformation will stop at this point.
- `model check` (property check) - this phase checks the model for invalid properties. For example the kubernetes plugin will reject the transformation if a node is based on windows. 

### Execution order

The execution of these phases should look like this:

`env-check -> node-type-check -> model-check`

This order is chosen because the first task is probably the "simplest" one to execute and the last one (model check) is the modt complex because we have to iterate over the whole graph to look for those problems. 

### Implementation approaches

While thinking about this i came across two implementation approaches, the following part will discuss both of them. The implementation approaches only cover the node type check and the model check. The environment has to be checked anyways. With some more efforts the env check could also get done before the transformation even launches. However this will require some modifications in the `core` part of the application. 

#### Seperate checks

This approach clearly splits the node type check (short `type check`) and the model check into two seperate phases. 

The interfaces and classes defined by using this approach will look like this (just to showcase):

TOSCAna Plugin (just for illustration purposes, class diagrams will follow):
```java
public abstract class LifecycleAwarePlugin<T extends TransformationLifecycle>
    implements TransformationPlugin {
    protected abstract Set<Class<? extends RootNode>> getSupportedNodeTypes();
    protected abstract boolean checkEnvironment();
    protected abstract T getInstance(TransformationContext ctx);

    public void transform(TransformationContext ctx) {
        //This method will implement the mechanism to execute the seperate phases
    }

} 
```
**Method descriptions**
- `getSupportedNodeTypes()` - Returns a set of classes for the supported node types
- `checkEnvironment()` - Performs ths `env-check` part of the validation phase
- `getInstance()` - Factory method, to produce a instance of the plugin specific Lifecycle interface with the given transformation Context

```java
public interface TransformationLifecycle {
    boolean validateNodeTypes();
    boolean validateModel();
    void prepare();
    void transform();
    void cleanup();
}
```
**Method descriptions**
- `validateNodeType()` - This method will be implemented by a common base class. It gets or builds a set of all node types used in the model. and then compares them to the set of supported nodes (`LifecycleAwarePlugin.getSupportedNodeTypes()`) if all Node types of the modell are in the set of the supported ones the transformation will proceed (the method will return `true`)
- `validateModel()` - This method should be used to check the model (probably by iterating over it). If a property is not supported (i.e. Windows on Kubernetes) the check should return false.
- `prepare()`, `transform()` and `cleanup()` - Perform the corresponding phase described in the [Introduction](#introduction)  

##### Sequence diagram

The order in which these methods get called is shown in the following sequence diagram:

![](diagrams/seperate-validation-sequence-diagram.png)

#### Combined model and type check

## Progress caluclation

Putting tasks in seperate methods allows a very simple, but also very inacurate way to calculate the progress. (at least when looking at execution time):

- Once a step gets completetd, the progress gets incremented by `(<Current Step Number> / <Total Step count>) * 100` per cent.
    - This option is very simple, but inacurate when looking at time based measurements. For example: If we decide to build Docker images while Transforming. This step will take way longer than just validating the Csar.

