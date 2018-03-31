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

During the preparation, the plugin decides which **Compute** node gets transformed to an EC2 instance. These instances are marked by adding them to the `computeToEC2` set of the `CloudFormationModule`. This means that later on during the [transform phase](#transform), they should be added to the template as CloudFormation EC2 instances.

In addition to that, the property values of the Compute node must be changed in order to be compatible with the CloudFormation EC2 instance. Specifically this means that the entity name must be converted to use only alphanumerical symbols and the values of the public and private IP addresses of the Compute node must be changed to the syntax used by CloudFormation. This means that the IP addresses are changed to reference the `PrivateIp` and `PublicIp` attributes of the respective EC2 instances.

### MysqlDatabase

The **MysqlDatabase** node requires additional preparation due to the fact that it will be transformed to an RDS resource. In order to create a RDS resource with CloudFormation, password, user and port values must be set. If the MysqlDatabase does not have the port and user values set, the plugin adds the default values `root` and `3306` instead. The password must be of a minimum length of **8**, if that is not the case or the password isn't set at all, the plugin replaces or adds a new random String as a password.

Also, if the visitor detects that the MysqlDatabase is the only node hosted on a specific Compute node, it automatically removes said Compute node from the list of Compute nodes to be transformed to EC2 instances. This is done because the RDS resource does not require an additional EC2 instance to be specified for it to run. If this is the case, the visitor also replaces any references to said instance with references to the RDS resource instead (e.g. IP addresses).

### JavaApplication

The **JavaApplication** node requires additional preparation due to the fact that it will be transformed to Beanstalk. Similar to the MysqlDatabase, if the visitor detects that the JavaApplication is the only node hosted on a specific Compute node, it automatically removes said Compute node from the list of Compute nodes to be transformed to EC2 instances. This is done because Beanstalk does not require an additional EC2 instance to be specified for it to run.

### WebApplication

If the **WebApplication** node does not have the port value set, the visitor adds the default web application port **80** to said node.

Currently, only the connectsTo relationship requires additional preparation before transformation:

### ConnectsTo

The **connectsTo** relationship requires additional preparation only if its target as a MysqlDatabase node. This is again due to the fact that the MysqlDatabase node gets transformed to an RDS resource. If the WebApplication or JavaApplication are hosted on the same Compute node as the MysqlDatabase, the public and private IP addresses of said compute node are set to reference the MysqlDatabase. This is done to make sure that applications that use the IP of the Compute node to connect to the database, instead can use the database endpoint of the RDS resource representing the MysqlDatabase. This is necessary if the application expects the MysqlDatabase to be hosted on the Compute node and its IP to match that of said node.

## Transform

When all preparations are complete, the actual **transformation** from the `EffectiveModel` to the target artifact takes place.

### Mapping TOSCA to CloudFormation

Initially, the transform phase starts like the other phases by iterating over the `EffectiveModel` via the `TransformationNodeVisitor`. Like in the [prepare phase](#prepare), the `visit()` methods are invoked first on the Compute nodes, then on all other nodes in the `EffectiveModel`.

For an in-depth explanation the actions of the `TransformModelNodeVisitor` and the general mapping of specific NodeTypes, please refer to the [Transformation by TOSCA type](supported-types.md) section. For an explanation of the transformation of a full `EffectiveModel`, please refer to the [Transformation by example](transformation-examples.md) section.

After all the nodes have been visited, the `EnvironmentHandler` adds scripts that set the environment variables required on specific EC2 instances during runtime. These environment variables and their respective EC2 insances were added during the iteration with the `TransformModelNodeVisitor` to the `environmentMap` of the `CloudformationModule` and are taken from the inputs of the start operation of a node, given that said operation is present.

### Create Template

Once the transformation mapping is complete, all the information needed to create the **CloudFormation template** is contained in the `CloudFormationModule`. The template is then generated by using the `toString()` method of the `CloudFormationModule` and written to a `template.yaml` file using the `FileAccess`.

### Write Scripts

In order to deploy the target artifact created by the CloudFormation plugin, additional **scripts** are needed. These are created dynamically and depend on the results of the previous transformation.

As explained in the [Transformation by TOSCA types](supported-types.md) section, some resources require additional files to be present on the EC2 instances in order to function properly. Because CloudFormation only allows for templates to be the single starting point of a stack creation, additional files needed in order to properly deploy the EC2 instances must be supplied remotely. If this is the case, the `CloudFormationFileCreator` adds a **`file-upload.sh`** script. This script invokes the the `createBucket ()` function of the `create-bucket.sh` utility script with the `bucketName` set in the `CloudFormationModule` which in turn invokes the [`aws s3api create-bucket`](https://docs.aws.amazon.com/cli/latest/reference/s3api/create-bucket.html) command of the AWS CLI. In addition to creating a bucket, the script adds file upload commands for each `FileUpload` in the `fileUploadList` of the `CloudFormationModule`. These commands use the `uploadFile ()` function of the `upload-file.sh` utility script which in turn uses the [`aws s3api put-object`](https://docs.aws.amazon.com/cli/latest/reference/s3api/put-object.html) command of the AWS CLI in order to upload files to the bucket.

The **`create-stack.sh`** script uses the [`aws cloudformation deploy`](https://docs.aws.amazon.com/cli/latest/reference/cloudformation/deploy/index.html) command of the AWS CLI in order to create the CloudFormation stack from the template. It references the `template.yaml` CloudFormation template and uses the `stackName` set in the `CloudFormationModule`. If needed, the `--capabilities CAPABILITY_IAM` flag is also added to the deploy command, allowing CloudFormation to modify AWS IAM. This is done in order to give EC2 instances authenticated access to the S3 bucket.

The **`cleanup.sh`** script uses the [`aws s3 rb`](https://docs.aws.amazon.com/cli/latest/reference/s3/rb.html) and [`aws cloudformation delete-stack`](https://docs.aws.amazon.com/cli/latest/reference/cloudformation/delete-stack.html) commands of the AWS CLI for deleting the S3 bucket and CloudFormation stack, respectively. Again, the `stackName` and `bucketName` are taken from the `CloudFormationModule`.

At last, the `CloudFormationFileCreator` adds a **`deploy.sh`** script, which first checks that the "aws" commands can be run in the environment by invoking the `check ()` function of the `environment-check.sh` utility script. Then it runs the other scripts in order of `file-upload.sh` (if present) then `create-stack.sh`. This script can be seen as an "all-in-one" script for convenience.

### Copy Files

After writing and adding all the scripts, the `CloudFormationFileCreator` copies any other **files** needed for deployment to the target artifact. This includes copying the utility scripts mentioned the previous [Write Scripts](#write-scripts) section, utility files (e.g. generic create scripts) and files supplied by the TOSCA CSAR which need to be uploaded to the S3 bucket.

### Write Readme

Finally, the **readme** explaining the usage of the target artifact is added.

For additional information about the target artifact and its deployment, visit the [Deployment Workflow](../deployment/deployment-workflow.md) and [Target Artifact](../deployment/target-artifact.md) sections.

## Cleanup

At this point, the CloudFormation plugin does not require any additional steps to be taken during the cleanup phase.
