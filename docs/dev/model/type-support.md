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
Custom types which are not part of the TOSCA specifications are listed below.
##### Node Types
| TOSCA type URI                | java class      |
|-------------------------------|-----------------|
| toscana.nodes.JavaApplication | JavaApplication |
| toscana.nodes.JavaRuntime     | JavaRuntime     |
