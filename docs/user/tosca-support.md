# TOSCA Support

*TOSCAna* targets the TOSCA specification '*TOSCA Simple Profile v1.1 cos01*'.
You can download the full specification here:  
>[**TOSCA Simple Profile in YAML v1.1 cos01**](http://docs.oasis-open.org/tosca/TOSCA-Simple-Profile-YAML/v1.1/cos01/) (04 October 2017)

Supporting the whole specification would have exceeded the scope of this project. 
Following main aspects of the TOSCA specification are **not supported**:

- Networking
- Node Filters
- Groups
- Policies
- Imperative Workflows
- Constraints

## Available Types
Most normative and non-normative TOSCA Types can be used in a service template.  
Additionally, *TOSCAna* provides its [own types](toscana-types.md).

All supported types are listed below.

##### Data Types
- tosca.datatypes.Credential

##### Node Types 
- tosca.nodes.Apache                
- tosca.nodes.Compute               
- tosca.nodes.Container.Application 
- tosca.nodes.Container.Application.Docker 
- tosca.nodes.Container.Runtime     
- tosca.nodes.Database              
- tosca.nodes.DBMS                  
- tosca.nodes.LoadBalancer          
- tosca.nodes.Root                  
- tosca.nodes.SoftwareComponent     
- tosca.nodes.Storage.BlockStorage  
- tosca.nodes.Storage.ObjectStorage 
- tosca.nodes.WebApplication        
- tosca.nodes.WebApplication.WordPress     
- tosca.nodes.WebServer             
- tosca.nodes.WebServer.Nodejs             
- tosca.nodes.Database.MySQL               
- tosca.nodes.DBMS.MySQL                   
- toscana.nodes.JavaApplication 
- toscana.nodes.JavaRuntime     
 
##### Capability Types 
- tosca.capabilities.Attachment        
- tosca.capabilities.Compute           
- tosca.capabilities.Container         
- tosca.capabilities.Container.Docker 
- tosca.capabilities.Endpoint          
- tosca.capabilities.Endpoint.Admin    
- tosca.capabilities.Endpoint.Database 
- tosca.capabilities.Endpoint.Public   
- tosca.capabilities.Network           
- tosca.capabilities.network.Bindable  
- tosca.capabilities.Node              
- tosca.capabilities.OperatingSystem   
- tosca.capabilities.Root              
- tosca.capabilities.Scalable          
- tosca.capabilities.Storage           
 
##### Relationship Types
- tosca.relationships.AttachesTo 
- tosca.relationships.ConnectsTo 
- tosca.relationships.DependsOn  
- tosca.relationships.HostedOn   
- tosca.relationships.Root       
- tosca.relationships.RoutesTo   

## Warning: `Accessing an unset attribute`
When plugins interact with the internal java representation of the TOSCA service template, it can happen that the warning `Accessing an unset attribute` appears in the transformation log. Whenever this happens, a plugin tried to access an attribute or property which was linked to another attribute or property (via a TOSCA intrinsic function) which was not (yet) set. If the linked field was intentionally not set (e.g., you modeled it that way), you can ignore this message. However, sometimes pointing to attributes which are set at runtime is necessary (e.g., `ip` of compute node). Whenever this message appears in that context, the plugin was not able to cope with the situation (it 'forgot' to set an attribute when it was responsible for setting it before interacting with the model).
 
## Limitations
What follows is a more or less accurate gathering of further limitations and discrepancies of otherwise supported features. 

>*Note: In the following, `ch. x.xx` refers to chapter x.xx in the above mentioned version of the TOSCA specification.*

### CSAR (Cloud Service Archive)
- a CSAR must contain **exactly one service template** in its archive root
- valid extensions for service templates are `yaml` and `yml`
- the `TOSCA-Metadata` directory is not evaluated; required metadata must be contained in the service template
 
I.e., features of `ch. 6.2` are not supported and the approach given in `ch 6.3` must be used.

### ServiceTemplate
- metadata: Not used
- *dsl_definitions*: Not supported
- *imports*: Not supported. 
I.e., you must get by with a single service template
- *\*_types*: Not supported.
I.e., you must get by with the built-in node, capability and relationship types.  
This sounds really limiting at first. 
But it's not: *TOSCAna*'s plugins wouldn't be able to trigger meaningful semantics upon encountering a custom type definitions, so not supporting custom type definitions shouldn't hurt too much.  

### TopologyTemplate
- *groups*: Not supported
- *policies*: Not supported
- *substitution_mappings*: Not supported
- *workflows*: Not supported

### TOSCA scalar-unit Type
- Only `scalar-unit.size` is supported.   
- Not supported: `scalar-unit.time`, `scalar-unit.frequency`. As a workaround, stick with the default unit size and omit the unit identifier. E.g., for assigning a value to a field of type `scalar-unit.frequency` with default unit `GHz`, use `4` instead of `4 GHz`.

### TOSCA type definitions
- Usage of the shorthand name `Compute` for `tosca.capabilities.Compute` is not supported; `Compute` is always interpreted as `tosca.nodes.Compute`
- All complex Data Types except for `tosca.datatypes.Credential` are unsupported. 
Node Types which make use of unsupported data types might not be handled properly: Handling (e.g., assignment, usage in functions) of affected attributes or properties might be incorrect.

### Node Templates
- *node_filter*: Not supported
- *copy*: Not supported

## Relationship Templates
- *copy*: Not supported


### Attributes and properties
`Ch. 2.16` explains how attributes shall get derived from properties at runtime.
However, there are following limitations:

- Attributes don't get derived from properties
- Properties can change at runtime (i.e., they behave exactly like attributes)

### Intrinsic functions
- *get_property/attribute*: keywords `SOURCE`, `TARGET`, `HOST` are not supported (but `SELF` is supported)
- *get_artifact*, *concat*, *get_nodes_of_type*, *get_operation_output*: Not supported

