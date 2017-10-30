# Sprint planing: Sprint 4

>start: 26.10.2017
end: 13.11.2017

## Sprint goal
> As a user, I want to transform CSARs into target artifacts for kubernetes, Amazon Web Services and CloudFoundry.
> These CSARs are supposed to only contain supported NodeTypes (at least one NodeType will be supported)
## GENERAL
- build CSAR that only contains supported NodeTypes
  - for testing purposes
  - for demo purposes

## PLUGINS
> Assigned:
> - Amazon Cloud Formation: @jmuell
> - Kubernetes: @nfode, @mmunozba
> - CloudFoundry: @mklopp
>
>  Joins later: @oferch
- [ ] Design a common plugin architecture
- [ ] Agree on Target Artifact conventions, e.g. file naming
- [ ] Create NodeType support document, which shows current NodeType support status of the plugins
- [ ] Prioritize NodeTypes (which establishes the implementation order)
- [ ] Agree on the first NodeTypes which shall get implemented
- [ ] Implement first NodeType

## CORE 
> Assigned: @hnicke, @c-mueller
- [ ] Refactor
- [x] Logging Persistency
- ~~Property persistency~~  < `next sprint`
- [ ] Fix read transformations from disk
- [x] Redesign PluginFileAccess
- [ ] TServiceTemplate processor
   ~~-  Merge Templates~~ < `next sprint`
    - [ ] Inject Types
    - [ ] Build Graph (with JGraphT)
    - [ ] Write interface for plugins to offer graph
        - this has high priority

## API 
> Assigned: @c-mueller
- [ ] Rework properties
- [ ] Generate API documentation
- [ ] Exchange custom status API with Spring Actuator

## CLI
> Assigned: @oferch
- [ ] Unified look on different operating systems
- [ ] Implement proper debugging
- [ ] Implement properties 
- [ ] Rework Help/Usage texts
