# Transformation by TOSCA Type

During the the transformation, 

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

## Transformation implementation details

These transformations take place in the TransformationModelNodeVisitor: [GitHub link](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudformation/visitor/TransformModelNodeVisitor.java)

    TODO: Figure out if we need the part above.
    TODO: Add NodeType transformation explanations.

### Compute

Compute nodes correlate with a ec2 because its just an vm.
Following steps are taken in the transform step:

1. SecurityGroup is created
2. optional Enpoint ports are opened on this security group puplically
3. OsCapability and ComputeCapability are mapped to properties of EC2 (like what imageID(ami-...) to take what instance type to take(t2.micro))
4. CFNinit is created but not yet added so it can be manipulated (cfninit is used to call scripts, commands, add files or install packages)
5. EC2 linked with securitygroup is created
6. disk size is mapped from tosca --> block device mapping
7. if keypair is activated --> add keyname to instance + open ingress on security group to port 22(SSH)

at buildtime: cfninit gets put on ec2 + userdata that executes cfninit + authentication + instancepofile gets added(permission for s3 bucket)

### Mysql Database

### Mysql DBMS

### JavaApplication

### Generic Transformation Behaviour for Nodes hosted on Compute

In contrast to the NodeTypes discussed above, there are various NodeTypes which don't get mapped to a specific CloudFormation resource but are rather added to the EC2 instance that they're meant to be hosted on. Specifically, this is done by the `OperationHandler` and `EnvironmentHandler` classes.

We use the [CloudFormation Init](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-init.html) and the [cfn-init](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-init.html) script to bootstrap our EC2 instances.

That being said, most of these NodeTypes require additional or alternative steps unique to them in order to be properly transformed. The following are explanation of how the transformation of each of these NodeTypes differs from the generic transformation behaviour.

### Apache

1. cfnInit apt apache2 on underlying compute/ec2
2. handleConfigure/handleStart --> scripts and files get copied and executed on underlying compute/Ec2
3. global environment variables (start lifecycle of webapplication that is hosted on this apache) will be added to /etc/apache/envvars 
4. if modifications took place a "service apache2 restart" command is added

### Database

## Supported RelationshipTypes

| TOSCA RelationshipType |
| --- |
| **hostedOn** |
| **connectsTo**: WebApplication -> Database |
| **connectsTo**: JavaApplication -> Database |

Currently, the CloudFormation plugin does not require additional steps to be taken after preparing the model with the `PrepareModelRelationshipVisitor` during the [prepare phase](transformation-workflow.md#prepare) and transforming it with the `TransformModelNodeVisitor` during the [transform phase](transformation-workflow.md#transform) in order to facilitate these relationships between the resources on AWS.
