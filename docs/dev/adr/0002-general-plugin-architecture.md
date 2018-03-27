# Design a general plugin architecture

Should we design a general plugin architecture?


## Considered Alternatives

* Design a general plugin architecture
* Every plugin has its own architecture


## Decision Outcome

* Chosen Alternative: Design a general plugin architecture
* Designing a general plugin architecture allows us to extract similarities to a abstract level.


## Pros and Cons of the Alternatives

### Design a general plugin architecture

- `+` every plugin implements the same interface
- `+` similarities can be extracted to a more abstract level
- `+` reduction of information the plugins need, for example file access could be handled in the abstract layer
- `-` there may be things that are not required by every plugin

### Every plugin has its own architecture

* `+` no need for discussion with other plugin teams
* `-` every plugin does what it wants
* `-` "duplicated code", same things are implemented the same in every plugin
* `-` How to check whether every plugin supports the given csar?
