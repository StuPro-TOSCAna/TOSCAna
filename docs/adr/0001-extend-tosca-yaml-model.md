# Extend TOSCA YAML model to a full in-memory model

As a programmer, I want to access the complete YAML model and not be forced to traverse through all files.

For instance, `TNodeTemplate.java` offers `public QName getType() {`, but one wants `public TNodeType getType()`.

## Considered Alternatives

* Inherit from plain data model and add transparent resolution
* New data model
* Eclipse EMF

## Decision Outcome

* Chosen Alternative: Inherit from plain data model and add transparent resolution
* Comes out best (see below)


## Pros and Cons of the Alternatives <!-- optional -->

### Inherit from plain data model and add transparent resolution

A new model with all classes in a different package inheriting from all model classes.
Each class gets new methods.
For the type example:

- `public TNodeType getTypeResolved()`
- `public setType(TNodeType nodeType)`

This model is offered by a new repository implementation, which resolves the objects on demand.

* `+` Transparent for the user
* `+` `org.eclipse.winery.model.tosca.yaml` reused
* `-` EMF Resource Set handling is implemented manually

### New data model

* `+` Clean data model
* `-` Much code from `org.eclipse.winery.model.tosca.yaml` duplicated

### Eclipse EMF

* `+` [Resource Sets](http://download.eclipse.org/modeling/emf/emf/javadoc/2.5.0/org/eclipse/emf/ecore/resource/ResourceSet.html) handling does this transparent solution
* `-` Changing all the model to EMF causes huge overhead
* `-` Getting EMF into non-OSGi projects is hard
