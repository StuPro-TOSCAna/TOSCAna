# TOSCA Type Support

## Normative TOSCA Types
Supported normative TOSCA types defined in ch. 5 of TOSCA Simple Profile v1.1 are listed below.

##### Node Types
| TOSCA type URI                    | java class           |
|-----------------------------------|----------------------|
| tosca.nodes.Apache                | Apache               |
| tosca.nodes.Compute               | Compute              |
| tosca.nodes.Container.Application | ContainerApplication |
| tosca.nodes.Container.Runtime     | ContainerRuntime     |
| tosca.nodes.Database              | Database             |
| tosca.nodes.DBMS                  | Dbms                 |
| tosca.nodes.LoadBalancer          | LoadBalancer         |
| tosca.nodes.Root                  | RootNode             |
| tosca.nodes.SoftwareComponent     | SoftwareComponent    |
| tosca.nodes.Storage.BlockStorage  | BlockStorage         |
| tosca.nodes.Storage.ObjectStorage | ObjectStorage        |
| tosca.nodes.WebApplication        | WebApplication       |
| tosca.nodes.WebServer             | WebServer            |

##### Capability Types
| TOSCA type URI                       | java class                 |
|--------------------------------------|----------------------------|
| tosca.capabilities.Attachment        | AttachmentCapability       |
| tosca.capabilities.Compute           | ComputeCapability          |
| tosca.capabilities.Container         | ContainerCapability        |
| tosca.capabilities.Endpoint          | EndpointCapability         |
| tosca.capabilities.Endpoint.Admin    | AdminEndpointCapability    |
| tosca.capabilities.Endpoint.Database | DatabaseEndpointCapability |
| tosca.capabilities.Endpoint.Public   | PublicEndpointCapability   |
| tosca.capabilities.Network           | NetworkCapability          |
| tosca.capabilities.network.Bindable  | BindableCapability         |
| tosca.capabilities.Node              | NodeCapability             |
| tosca.capabilities.OperatingSystem   | OsCapability               |
| tosca.capabilities.Root              | Capability                 |
| tosca.capabilities.Scalable          | ScalableCapability         |
| tosca.capabilities.Storage           | StorageCapability          |

##### Relationship Types
| TOSCA type URI                 | java class       |
|--------------------------------|------------------|
| tosca.relationships.AttachesTo | AttachesTo       |
| tosca.relationships.ConnectsTo | ConnectsTo       |
| tosca.relationships.DependsOn  | DependsOn        |
| tosca.relationships.HostedOn   | HostedOn         |
| tosca.relationships.Root       | RootRelationship |
| tosca.relationships.RoutesTo   | RoutesTo         |

## Non-normative TOSCA Types
Supported non-normative TOSCA types defined in ch. 9 of TOSCA Simple Profile v1.1 are listed below.

##### Node Types
| TOSCA type URI                           | java class        |
|------------------------------------------|-------------------|
| tosca.nodes.Container.Application.Docker | DockerApplication |
| tosca.nodes.Database.MySQL               | MysqlDatabase     |
| tosca.nodes.DBMS.MySQL                   | MysqlDbms         |
| tosca.nodes.WebApplication.WordPress     | WordPress         |
| tosca.nodes.WebServer.Nodejs             | Nodejs            |

##### Capability Types
| TOSCA type URI                       | java class                 |
|--------------------------------------|----------------------------|
| tosca.capabilities.Container.Docker  | DockerContainerCapability  |

## Custom Types
Custom types which are not part of the TOSCA specifications, but nevertheless got natively integrated into TOSCAna, are listed below.
##### Node Types
| TOSCA type URI                | java class      |
|-------------------------------|-----------------|
| toscana.nodes.JavaApplication | JavaApplication |
| toscana.nodes.JavaRuntime     | JavaRuntime     |

# Add new types
In order to add a new type, following steps are necessary:

- Implement the type class
    - the class must, either directly or indirectly, inherit from the appropriate base class `RootNode` / `Capability` / `Relationship`
    - remember to use [ToscaKeys](tosca-elements.md#toscakeys) instead of directly using data fields. Pay special attention to specifying the necessary type information correctly.
    - generate getters and setters with the supplied [code templates](code-templates.md). Make use of the *Optional getter* template if a value is not required.
    - write a routine for initialization of complex values. This is the place where you can specify default values, as well. Look into existing classes if you're not sure how to do this. 
    - override the `accept` method. If this is omitted, the type can't be visited with one if the [visitors](effective-model.md#iterate-elements).
- add an appropriate `visit` method to either `NodeVisitor`, `CapabilityVisitor` or `RelationshipVisitor` and also update its corresponding strict version.

## Add custom types
Additionally to above steps, following steps have to be taken:

- put its TOSCA type definition into *server/src/main/resources/tosca_definitions*
- modify the `CustomTypeInjector` class to also load the new definition. This will inject the type definition into any service template that makes use of the new type.
