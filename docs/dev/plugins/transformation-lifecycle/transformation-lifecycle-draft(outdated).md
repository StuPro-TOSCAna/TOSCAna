# Transformation Lifecycle draft

The basic idea of the TransformationLifecycle is to create a interface that brings all the plugins on a same level.
The gist of the TransformationLifecycle is to provide the same lifecycle for every transformation, the idea came from the Maven lifecycle. As in the Maven lifecycle we have different phases, in our case so called transformation phases.
With the lifecycle in mind we can build the TransformationLifecycleInterface containing a method for every build phase.
The interface is then implemented by a abstract BasePlugin.
Every specific plugin has to extend the BasePlugin, so the specific plugin is forced to provide a method for every transformation phase.

**Transformation phases:**
- `validate` - in this phase the plugin provides a list of supported TOSCA types, then the graph that is enqued for transformation will be checked if it contains types the plugin can not handle.
If the result is positive the transformation continues else it will abort.

- `analyze` - in this phase the plugin analyzes the graph to find out how to handle it.
For example the Kubernetes plugin in this case analyzes if there is for example a NodeTemplate that requires windows.
- `prepare` - in this step the plugin processes the graph to transform it in the following step. For example the kubernetes plugin needs to split the graph into container and pods.
- `transform` - this is where the real transformation is happening.
- `clean` - in this phase the plugin cleans up remaining leftovers from the previos steps. This can be for example files generated during the transformation that are not part of the target artifact.

In some cases a plugin might not need a specific transformation phase. The transformation method will remain empty in such cases.

**Pro:**
- every plugin implements the same interface
- similarities can be extracted to a more abstract level
- reduction of information the plugins need, for example file acces could be handled in the abstract layer
