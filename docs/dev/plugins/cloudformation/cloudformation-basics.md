# AWS CloudFormation
How to transform a TOSCA model to an Amazon Web Services CloudFormation template.

## Transformation Lyfecycle

### validate

### prepare
- Add missing nodes to fulfill requirements.
- Split the graph, each part representing a AWS Resource

### transform
- transform everything into a CloudFormation JSON/YAML template

### cleanup


## Deployment / Readme
- target artifact should be a single template file (JSON/YAML)
- deployment is either handled by calling the AWS API, using the AWS CLI or manually uploading the template file on the AWS Console
