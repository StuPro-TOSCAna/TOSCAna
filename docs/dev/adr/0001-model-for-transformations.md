# *Model for transformations: Build own*

The computation result of the winery yaml parser is a `TServiceTemplate.java`. Should plugins deal with this model directly or is there a more convenient way for plugin developers to access the TOSCA topology?


## Considered Alternatives

* Inherit from `TServiceTemplate` and extends it in some way
* Build own data model
* Describe metamodel with Eclipse EMF and generate instance model

## Decision Outcome

* Chosen Alternative: Build own data model
* Comes out best (see below)


## Pros and Cons of the Alternatives

### Inherit from `TServiceTemplate` and extend it in some way

A new model with all classes in a different package inheriting from all model classes.
Each class gets new methods.

* `+` `org.eclipse.winery.model.tosca.yaml` reused
* `-` EMF Resource Set handling is implemented manually
* `-` Plugin developers have to deal with the model in an inconvenient way:
    - e.g. types are not expressed via java classes -> OO design is ugly
    - each plugin would have to define own classes of the supported node types, and map the contained elements of `TServiceTemplate` individually to proper java instances


### Build own data model

Build an own data model instead of reusing the TServiceTemplate. While doing this, be more object oriented. I.e., the normative Node Type `WebServer` is modelled as a java class, and an instance of that class is a NodeTemplate of that Node Type.

The new model should be accessible via a graph (using jgrapht), whereas modeled NodeTemplates are vertices and relationships are edges.

* `+` Clean data model
* `+` cleaner OO design
* `+` As plugin developer: Convenient usage of the model
* `+` As plugin developer: Type safety
* `+` less code duplication in plugins
* `+` central definition of specific supported node types
* `+` easier to troubleshoot due to strong typing
* `-` both UML diagram and java classes need to be taken care for

### Describe metamodel with Eclipse EMF and generate instance model

* `+` [Resource Sets](http://download.eclipse.org/modeling/emf/emf/javadoc/2.5.0/org/eclipse/emf/ecore/resource/ResourceSet.html) handling does this transparent solution
* `+` UML and code stays synchronized
* `-` Changing all the model to EMF causes huge overhead
* `-` Getting EMF into non-OSGi projects is hard
* `-` Team doesn't know technology -> hard to tell if its suitable for our needs -> high risk

