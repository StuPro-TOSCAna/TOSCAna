# AWS CloudFormation
How to transform a TOSCA model to an Amazon Web Services(AWS) CloudFormation template.

## Mapping
Mappings of TOSCA node types to AWS Resources are shown in [this table](mapping.md).

## Transformation Lifecycle

### validate
- Environment check:
    - AWS CLI installed
- Return list of supported node types
- Model check (properties):
    - Will be addressed during implementation.

### prepare
- Add missing nodes to the graph to fulfill the requirements.
- Split the graph, each part should representing a AWS Resource (multiple nodes can be put together to a single AWS Resource)

### transform
- Transform the graph/the combined nodes into a CloudFormation template

### cleanup
- Will be addressed during implementation.


## Deployment / Readme
- The target artifact should be a single template file (YAML)
- Deployment is handled by using the AWS Command Line Interface decided in [this adr](../../../adr/0007-plugin-cloudformation-cli.md)

## Next steps

- Manually create a CloudFormation template for the LAMP-CSAR

- Transform the LAMP-CSAR with the following node types and relationships:
    - tosca.nodes.WebApplication
    - tosca.nodes.WebServer.Apache
    - tosca.nodes.Database.MySQL
    - tosca.nodes.DBMS.MySQL
    - tosca.nodes.Compute
    - tosca.relationships.ConnectsTo
    - tosca.relationships.HostedOn
