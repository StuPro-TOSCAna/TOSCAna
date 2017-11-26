# AWS CloudFormation
How to transform a TOSCA model to an Amazon Web Services CloudFormation template.

## Mapping
see [this](mapping.md)

## Transformation Lyfecycle

### validate
- nothing to do here so far

### prepare
- Add missing nodes to fulfill requirements.
- Split the graph, each part representing a AWS Resource

### transform
- transform everything into a CloudFormation JSON/YAML template

### cleanup
- nothing to do here so far

## Deployment / Readme
- target artifact should be a single template file (JSON/YAML)
- deployment is handled by using the AWS Command Line Interface, see [this adr](../../../adr/0007-plugin-cloudformation-cli.md)

## Next steps

- Manually creating a template for the test LAMP-CSAR

- Transforming the test LAMP-CSAR with following node-types and relationships:
    - tosca.nodes.WebApplication
    - tosca.nodes.WebServer.Apache
    - tosca.nodes.Database.MySQL
    - tosca.nodes.DBMS.MySQL
    - tosca.nodes.Compute
    - tosca.relationships.ConnectsTo
    - tosca.relationships.HostedOn
