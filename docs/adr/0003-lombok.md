# *Use Project Lombok in order to avoid writing boilerplate code*

**User Story:** 

The implemenation of the EffectiveModel must make use of the builder pattern (many optional parameters required for object construction).

Without the builder pattern, the model has approx. 2.5k loc. With the builder pattern, it grows to approx. 4k loc, due to the boilerplate nature of the pattern.

## Considered Alternatives

* *Use lombok*
* *Write boilerplate code*
* *Use kotlin instead of java*

## Decision Outcome

* Chosen Alternative: *Use Lombok*
* *Comes out best (see below)*
* *Consequences: Every developer has to install the lombok plugin in his IDE* <!-- optional -->

## Pros and Cons of the Alternatives <!-- optional -->

### *Use lombok*

##### What is lombok
[Project lombok](https://projectlombok.org/) is an annotation-based framework which strives to eliminate boilerplate code from the codebase.
It can tackle:
- [Builder pattern](https://projectlombok.org/features/Builder)
- [Getters & setters](https://projectlombok.org/features/GetterSetter)
- many more

#### Pro's and cons
* `+` *significantly smaller EffectiveModel code base*
* `+` *better maintainability due to smaller code base*
* `+` *better readability due to smaller code base*
* `+` *the `delombok` feature makes it possible to easily rip lombok out of the project if the need arises*
* `+` *No boilerplate that can get out of sync if class changes*
* `-` *introduces more "black magic" to the codebase*
* `-` *developers have to know what each annotation does*
* `-` *every developer has to install a plugin for his IDE. supported: intellij, eclipse*

### *Write boilerplate code*

* `+` *pure vanilla java*
* `-` *boilerplate code that might get out of sync when class changes*

### *Use kotlin instead of java*

* `+` *lean language design*
* `+` *would have similar positive effects than Lombok in terms of maintainability and readability*
* `-` *team (besides @c-mueller) doesn't know kotlin, and it's much harder to learn than learning Lombok*
