# Adding support for new node types

Assuming you have defined a type class in the model, you can extend the Kubernetes plugin to support new types as follows:

## Step 1: Passing the model check

You have to add a empty `visit()` method to the `NodeTypeCheckVisitor` for the corresponding type.
To ensure your new type passes the model check.

## Step 2: Mapping to a base image

We assume, that you know to what Docker image your new node type should be mapped to!

The nodes get put (for each node stack) into the `ImageMappingVisitor` in the following order: `Compute -> Child 1 -> Child 2...`
we consider the image in the field `baseImage` as the expected base image, after all nodes of the stack have been inserted into the visitor.

Add a `visit()` to the `ImageMappingVisitor` even if the does not modify the base image,
in that case add another empty method.
If you want to set the base image just set the `baseImage` variable.
It should be obvious that you can also implement some kind of logic to determine the base image, based on some conditions.

## Step 3: Extending the Dockerfile building phase

Using a specific Docker image is one part of transforming to Kubernetes,
the other part is the "configuration" of the node, this is done in the `DockerfileBuildingVisitor`. The configuration of a node gets set in the Dockerfile,

### Implementing the default behavior

To implement the default behavior that gets applied to abstract node types such as
`SoftwareComponent` just create a `visit()` method in the `DockerfileBuildingvisitor` that calls the
`handleDefault()` the behavior of this operation is described on the [Building Dockerfiles page](building-dockerfiles.md)

### Implementing custom behavior

You can also implement a custom behavior within the `visit()` method.
This might even be needed, depending on the Base Image that has been chosen.
For some examples on how to implement such behavior you can take a look at the `Apache` or `MySQlDatabase` visit methods.
To implement such methods it is recommended to be familiar with the `DockerfileBuilder` (documented in javadoc) and the [EffectiveModel](../../model/effective-model.md)
