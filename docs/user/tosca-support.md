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
- *\*_types*: Not supported.
I.e., you must get by with the supported node, capability and relationship types.
This sounds really limitating. 
However, *TOSCAna*'s plugins wouldn't be able to trigger meaningful semantics upon encountering your custom `AwesomeType`, so it shouldn't hurt too much

### TopologyTemplate
- *groups*: Not supported
- *policies*: Not supported



---
#### TODO
- tosca functions
    - get_property:
        - keywords SOURCE, TARGET, HOST
    - get_artifact
    - concat
    - get_nodes_of_type
    - get_operation_output
- short notation of Compute Capability
