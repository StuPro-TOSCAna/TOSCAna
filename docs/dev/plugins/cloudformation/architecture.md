# Architecture

    TODO: Add a class diagram.
    TODO: Add a small explanation/overview of the classes

`CloudFormationModule` is an extension of the `Module` class of the cloudformation-builder. Originally, it was used in order to represent the java model of a CloudFormation template, but in order to fulfill the needs of our plugin, we extended the it with additional fields and methods needed to facilitate the deployment of our template.

`CloudFormationPlugin`
These credentials are needed during the `check` phase of the transformation lifecycle.

`CloudFormationLifecycle`
When the user starts the transformation of a TOSCA CSAR, the CloudFormation plugin starts working goes through the different phases of the for further information about the actual transformation procedure, please refer to the transformation workflow.

    TODO: Add link to transformation workflow