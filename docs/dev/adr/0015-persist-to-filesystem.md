# Persist to filesystem

**User Story:** 

The application needs to store state (created csars, transformations etc).
How should this state be persisted?

## Considered Alternatives

* Use relational database
* Store in filesystem 

## Decision Outcome

* Chosen Alternative: Store in filesystem.

Most of the data is unstructured data (the csar).
Filesystems excel in handling this kind of data.

## Pros and Cons of the Alternatives <!-- optional -->

### Use relational database

* `+` scales with ease (e.g., adding new information to existing structure)
* `-` handling binary / unstructured data is unconventient

### Store in filesystem

* `+` interdependencies between the stored data (csar and transformation) is simple enough to allow storing in a filesystem
* `+` structure of the rest api can be reflected in the file system storage
* `+` debugging is simple: just use a file explorer
* `+` adding a csar or receiving a transformation result can be achieved with the file explorer
* `-` doesn't scale well, in case more complicated, structured data will need to get persisted in the future
* `-` can not easily get deployed in a PAAS environment
