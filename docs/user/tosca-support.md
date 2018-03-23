# TOSCA Support

*TOSCAna* targets the TOSCA specification '*TOSCA Simple Profile v1.1 cos01*'.
You can download the full specification here:  
>[**TOSCA Simple Profile in YAML v1.1 cos01**](http://docs.oasis-open.org/tosca/TOSCA-Simple-Profile-YAML/v1.1/cos01/) (04 October 2017)

Supporting the whole specification would have exceeded the scope of this project. Don't forget: *TOSCAna* is a prototype.

Following main aspects of the TOSCA specification are **not supported** by TOSCAna:

- Networking
- Node Filters
- Node Groups
- Policies
- Imperative Workflows

What follows is a more or less accurate gathering of further limitations and discrepancies of otherwise supported features. 

>*Note: In the following, `ch. x.xx` refers to chapter x.xx in the above mentioned version of the TOSCA specification.*

### CSAR (Cloud Service Archive)
- a CSAR must contain **exactly one service template** in it's archive root
- valid extensions for service templates are `yaml` and `yml`
- the TOSCA-Metadata directory is not evaluated; required metadata must be contained in the service template
 
I.e., features of `ch. 6.2` are not supported and the approach given in `ch 6.3` must be used.

### ServiceTemplate
- *dsl_definitions*: Not supported
- *imports*: Not supported. 
I.e., you must get by with a single service template
- *\*_types*: Not supported - defining types is not supported.
I.e., you must get by with the built-in node, capability and relationship types.
This sounds really limitating at first. But it's not: *TOSCAna*'s plugins wouldn't be able to trigger meaningful semantics upon encountering a custom type definitions, so ditching it shouldn't hurt too much.  

### TopologyTemplate
- *groups*: Not supported
- *policies*: Not supported
- 
### TOSCA scalar-unit Type
- Only `scalar-unit.size` is supported.   
Not supported: `scalar-unit.time`, `scalar-unit.frequency`

### Types

##### TOSCA normative type definitions
- Usage of the shorthand name `Compute` for `tosca.capabilities.Compute` is not supported; `Compute` is always interpreted as `tosca.nodes.Compute`

### Attributes and properties
`Ch. 2.16` explains how attributes shall be derived from properties at runtime.
However, there are following limitations:
- Attributes don't get derived from properties
- Properties can change at runtime (i.e., they behave exactly like attributes)

---
#### TODO
- tosca functions
    - get_property:
        - keywords SOURCE, TARGET, HOST
    - get_artifact
    - concat
    - get_nodes_of_type
    - get_operation_output
