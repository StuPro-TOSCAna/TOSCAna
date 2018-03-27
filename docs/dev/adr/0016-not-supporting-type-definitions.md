# Do not support type definitions in TOSCA service template

**User Story:** 

Should *TOSCAna* support node type definitions in the TOSCA service template?

## Considered Alternatives

* Support node type definitions
* Don't support node type definitions

## Decision Outcome

* Chosen Alternative: Don't support node types

Supporting node type definitions would be nice, but we can not afford the effort (limited resource).
The benefits are too small.

## Pros and Cons of the Alternatives <!-- optional -->

### Support node type definitions

* `+` more complete implementation of TOSCA
* `-` needs to be developed; is quite complicated

### Don't support node type definitions

* `-` According to the TOSCA spec, it's not possible to add additional requirements to node templates. They must be added to it's node type. Without custom type definitions, there is no way for the modeller to specify additional requirements.
