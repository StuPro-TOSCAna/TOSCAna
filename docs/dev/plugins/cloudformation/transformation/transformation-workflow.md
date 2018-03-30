# Transformation Workflow

The CloudFormation plugin uses an extension of the standard `AbstractLifecycle` called `CloudFormationLifecycle`. The input is the `TransformationContext` and the output is the [target artifact](../deployment/target-artifact.md) for deployment on AWS CloudFormation.

The following section describes what the CloudFormation plugin does and what role each of its components play during each lifecycle phase in order to generate the target artifact. The lifecycle operations are executed in the order they are listed.

## CheckModel

Before the transformation begins, the plugin **checks** whether the `EffectiveModel` given through the `TransformationContext` can be fully transformed.

In order to ensure that, the `visit()` methods of the `CheckModelNodeVisitor` and `CheckModelRelationshipVisitor` are invoked on all nodes and relationships respectively. These visitor classes in essence only check whether the NodeTypes and RelationshipTypes are supported by the plugin. If an unsupported type is detected, an `UnsupportedTypeException` is thrown and the transformation subsequently aborted. For more information on which TOSCA types are currently supported by the plugin, please refer to the "[Transformation by TOSCA Type](supported-types.md)" section.

## Prepare

After ensuring that the transformation of the `EffectiveModel` is fully supported, the plugin **prepares** the model to be transformed for CloudFormation.

This preparation is done by the `PrepareModelNodeVisitor` and `PrepareModelRelationShipVistor` classes. Similar to the [checkModel phase](#checkmodel), the `visit()` methods of these visitor classes are invoked on all nodes and relationships in the `EffectiveModel`. The order in which the different nodes are visited is different since the Compute nodes are visited first and then all other nodes. This change is done because of the specific transformation behaviour of the MysqlDatabase and Compute types and their respective EC2 and RDS counterparts.

Currently, there are three NodeTypes that require additional preparation before the transformation:

### Compute

During the preparation, the plugin decides which Compute needs get transformed to an EC2 instance. These instances are marked by adding them to the `computeToEC2` set of the `CloudFormationmodule`. This means that later on during the [transform phase](#transform), they should be added to the template as CloudFormation EC2 instances.

In addition to that, the property values of the Compute node must be changed in order to be compatible with the CloudFormation EC2 instance. Specifically this means that the entity name must be converted to use only alphanumerical symbols and the values of the public and private IP addresses of the Compute node must be changed to the syntax used by CloudFormation. This means that the IP addresses are changed to reference the `PrivateIp` and `PublicIp` attributes of the respective EC2 instances (See []() for more info on the specific EC2 instance attributes).

### MysqlDatabase



### WebApplication

## Transform

For an in-depth explanation the actions of the `TransformModelVisitor` and `TransformRelationshipVisotr` classes and the general mapping of specific Node-/RelationshipTypes, please refer to the supported node-types section. For an explanation of the transformation of a full `EffectiveModel`, please refer to the transformation examples section.

    TODO: Add links to node-types and example sections.

## Cleanup

At this point, the CloudFormation plugin does not require any additional steps to be taken during the cleanup phase.