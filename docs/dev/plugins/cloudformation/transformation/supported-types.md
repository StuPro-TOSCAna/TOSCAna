# Transformation by TOSCA Type

Currently, the CloudFormation plugin only supports certain Node- and RelationshipTypes defined in the [TOSCA Simple Profile](http://docs.oasis-open.org/tosca/TOSCA-Simple-Profile-YAML/v1.1/cos01/). The following sections shows which types are supported and how those are mapped to CloudFormation.

## Supported NodeTypes

The following table contains the NodeTypes currently supported by the CloudFormation plugin and how they're mapped to CloudFormation resources.

| TOSCA NodeType | CloudFormation |
| --- | --- |
| Apache | Setup through CloudFormation Init on EC2 host |
| Compute | EC2 resource and corresponding SecurityGroup |
| Database | Setup through CloudFormation Init on EC2 host |
| Dbms | Setup through CloudFormation Init on EC2 host |
| MysqlDatabase | RDS resource with a mysql engine |
| MysqlDbms | No specific resource is created |
| WebApplication | Setup through CloudFormation Init on EC2 host |
| Nodejs | Setup through CloudFormation Init on EC2 host |
| JavaRuntime | No specific resource is created |
| JavaApplication | Beanstalk Application and Environment |

## NodeType Transformation Details

These transformations take place in the `TransformationModelNodeVisitor`. During the [transform phase](transformation-workflow.md#transform) of the `TransformationLifeCycle`, the `visit()` of the various NodeTypes are executed in order to fill the `CloudFormationModule` with the necessary information to generate the CloudFormation template and the files needed to deploy the CloudFormation stack. The following section offers a detailed explanation of the transformation behaviour for each of these NodeTypes.

### Compute

Each Compute node that was previously marked in the [prepare phase](transformation-workflow.md#prepare) now gets transformed into an EC2 `Instance` resource with a corresponding `SecurityGroup` resource.

#### 1. Security Group

The `SecurityGroup` is created. The security group allows the opening of ports for the corresponding EC2 instance. If present, optional endpoint ports are opened on this security group.

#### 2. Capability Mapping

Both the OsCapability and ComputeCapablity of the Compute node are mapped to properties of the EC2 instance by the `CapabilityMapper`.

First the `CapabilityMapper` is used to figure out the ID of the Amazon Machine Image (AMI) corresponding to the OsCapability. To do this, we build an [`AmazonEC2`](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/AmazonEC2.html) client with the AWS SDK for Java to get the latest image IDs from AWS. In order to get the right image ID, a [`DescribeImagesRequest`](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/DescribeImagesRequest.html) is built with the properties of the OsCapability. When the image request is completed, the [`describeImages()`](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/AmazonEC2.html#describeImages-com.amazonaws.services.ec2.model.DescribeImagesRequest-) method of the EC2 client is used to get the images fullfilling the requirements of the request. From these images, the capability mapper takes the the latest image ID available which in turn is used by the visitor as the `ImageId` property of the EC2 instance.

Then the `CapabilityMapper` is used to figure out the right `InstanceType` for the EC2 instance corresponding to the ComputeCapability. The right instance type is determined based on the `numCpus` and `memSize` properties of the ComputeCapability. Once the right instance type has been found, it is used by the visitor as the `InstanceType` property of the EC2 instance. If no instance type fitting the requirements the ComputeCapability can be found, the transformation ends with a `TransformationFailureException`.

#### 3. Cloudformation Init

We use the [CloudFormation Init](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-init.html) and the [cfn-init](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-init.html) script to bootstrap our EC2 instances. It allows us to call scripts, commands, install packages and download files on our EC2 instances. Specifically the `CFNinit` is created and added to the `CloudFormationModule` but not yet to the EC2 instance, so it can still be manipulated by the visitor.

#### 4. Creating the instance

At this point, all the necessary information in order to create the EC2 instance resource corresponding to the Compute node has been gathered. The resource gets added to the `CloudFormationModule` using the `resource()` method with the `SecurityGroupId`, `ImageId` and `instanceType`.

Then the `CapabilityMapper` uses the ComputeCapability to get the right disk size for the EC2 instance. If the disk size exceeds the standard of 8 Gb for EC2 instances, an additional [`EC2BlockDeviceMapping`](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-blockdev-mapping.html) is added to the EC2 instance resource containing an Amazon Elastic Store (EBS) Block Device with the `VolumeSize` corresponding to the disk size of the ComputeCapability.

Finally, if the `keyPair` property of the `CloudFormationModule` was set to true, the `KeyName` referencing the `KeyName` String parameter, which is later set when the `build()` method of the `CloudFormationModule` called, is added to the EC2 instance resource and the port `22` is opened on its corresponding SecurityGroup. This allows the user to access the EC2 instance via SSH by authenticating with the right AWS Keypair.

#### 5. During build time

After all nodes have been visited and before the template gets created, the `build()` method of the `CloudFormationModule` gets called.
Here the previously built `CFNInit`s belonging to EC2 instances and a userdata section that executes cfn-init are added to the EC2 instances.

If EC2 instances need additional files from the S3 bucket during the deployment, an `Authentication` and `IamInstanceProfile` are added to those instances to allow them to access the S3 bucket.

If the `keyPair` property of the `CloudFormationModule` is set to true, a `KeyName` [CloudFormation parameter](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/parameters-section-structure.html) is added to the `CloudFormationModule`. This parameter must be set in the `create-stack.sh` script before deploying the target artifact as mentioned in the [platform properties](../architecture.md#platform-properties) section.

    TODO: Add link to architecture platform properties section.

### MysqlDatabase

Each MysqlDatabase gets transformed to a DBInstance resource.

### MysqlDBMS

### JavaApplication

### JavaRuntime

Because we transform JavaApplications to Beanstalk resources we do not require a JavaRuntime on an EC2 instance. All the information needed from the **JavaRuntime** in order to build those Beanstalk resources is taken during the `visit()` method of the JavaApplication.

### Generic Transformation of Nodes hosted on Compute

Contrary to the NodeTypes discussed above, there are various NodeTypes which don't get mapped to a specific CloudFormation resource but are rather added to the EC2 instance that they're meant to be hosted on. Specifically, this is done by the `OperationHandler` and `EnvironmentHandler` classes.

The `Operationhandler` takes the create, start and configure operations and adds them as files and commands to the CFNInit of the underlying EC2 instance. Specifically, this means that the dependencies and artifacts are marked as files to be uploaded to the S3 Bucket and marking the EC2 instance as an instance in need of Authentication. The authentication mentioned here is needed for the EC2 instance to access the S3 Bucket containing the files. Both artifacts and dependencies are also added as CFNFiles to the CFNInit, meaning that the cfn-init script will download them during the initial bootstrapping of these EC2 instance. Lastly, the path to the artifact on the EC2 instance and the inputs for the operation are added as a CFNCommand to the CFNInit, meaning that it will be executed by the cfn-init script during the bootstrapping.

In order to allow the nodes to access the input variables of the start operations during runtime, the inputs are also added to the `environmentMap` of the `CloudFormationModule`. Later during the [transform phase](transformation-workflow.md), these variables are get written to `etc/environment` on the EC2 instance through `setEnv` scripts.

That being said, most of these NodeTypes require additional or alternative steps unique to them in order to be properly transformed. The following are explanation of how the transformation of each of these NodeTypes differs from the generic transformation behaviour.

### Apache

Instead of using the create operation of the Apache node, we add the `apache2` package to the CFNInit of the underlying EC2 instance. The start and configure operations are added normally as described in [Generic Transformation of Nodes hosted on Compute](#generic-transformation-of-nodes-hosted-on-compute).

Environment variables that are used during runtime by an application hosted on the Apache node must be imported from `etc/environment` to `etc/apache/envvars`. This is done through an additional command added to the CFNInit. These environment variables are added to `etc/environment` during the transformation of the application as described in [Generic Transformation of Nodes hosted on Compute](#generic-transformation-of-nodes-hosted-on-compute).

If the start or configure operations were present in the Apache node, an additional `service apache2 restart` restart command is added to the CFNInit to ensure that the configuration modifications are properly loaded.

### Dbms

In addition to the generic hosted node transformation, the port contained in the port property of the Dbms node must be opened on the underlying EC2 instance. This means that the port is opened in the SecurityGroup corresponding to the EC2 instance of the Compute host of the hostedOn relationship connected to the Database.

### Database

Similar to the Dbms node, in addition to the generic hosted node transformation, the port contained in the port property of the Database node are opened on the underlying EC2 instance by opening it in the corresponding SecurityGroup.

### WebApplication

The ports from the AppEndPoint of the WebApplication node are opened in the SecurityGroup of the underlying EC2 instance.

### Nodejs

Instead of using the create operation of the Nodejs node, a generic `create-nodejs.sh` script is added to the CFNInit of the underlying EC2 instance. This script installs the latest version of Node.js 8. The start and configure operations are added normally as described in the [Generic Transformation of Nodes hosted on Compute](#generic-transformation-of-nodes-hosted-on-compute) section.

The ports from the EndpointCapability of the Nodejs node are opened in the SecurityGroup of the underlying EC2 instance.

## Supported RelationshipTypes

| TOSCA RelationshipType |
| --- |
| **hostedOn** |
| **connectsTo**: WebApplication -> Database |
| **connectsTo**: JavaApplication -> Database |

Currently, the CloudFormation plugin does not require additional steps to be taken after preparing the model with the `PrepareModelRelationshipVisitor` during the [prepare phase](transformation-workflow.md#prepare) and transforming it with the `TransformModelNodeVisitor` during the [transform phase](transformation-workflow.md#transform) in order to facilitate these relationships between the resources on AWS.
