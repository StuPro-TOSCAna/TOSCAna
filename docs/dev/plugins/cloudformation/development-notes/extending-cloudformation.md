# Extending the CloudFormation Plugin

The following is a list of limitations and hints on how the CloudFormation plugin can be extended in order to support them. It also details known issues with the plugin that haven't been fixed.

## Limitations and Extension Hints

---

> **Limitation**: The CloudFormation only supports certain TOSCA NodeTypes.

**How to extend**: The support for additional NodeTypes is generally done by implementing the corresponding `visit()` method in the `TransformationNodeVisitor` depending on the NodeType, this it would be transformed into an AWS resource or added to the EC2 instance through CloudFormation Init. The NodeType must also be marked as a supported type by adding a `visit()` to the `CheckModelNodeVisitor` class. Finally, if any additional steps must be taken in order to prepare the node in the `EffectiveModel` before transforming, an additional `visit()` method for the NodeType should be added to the `PrepareModelNodeVisitor` class.

---

> **Limitation**: The CloudFormation plugin only supports the MySQL RDS database engine.

**How to extend**: The easiest way to support other database engines such as Amazon Aurora, PostgreSQL, MariaDB etc. other database types similar to the existing normative MysqlDatabase and MysqlDbms NodeTypes could be defined. These could then be easily transformed by mirroring the transformation of the MySQL normative NodeTypes.

---

> **Limitation**: The CloudFormation only supports Compute nodes with Linux Ubuntu as the OS and distribution.

**How to extend**: To support other Linux distributions, the `CapabilityMapper` must be extended in order to get the right AMI. Also, the `CheckModelNodeVisitor` must be extended to show that the new distribution is supported. Depending on the distribution, there might be different steps required in order to ensure a correct transformation. This means that depending on the distribution, certain parts of the `visit()` methods of the visitors must be changed for certain NodeTypes. Since there is no general rule on how these changes might look like it might even be wise to implement an entirely seperate visitor per distribution, depending on how much the specific implementations differ from one another.

In order to support an entirely different OS such as Windows, the `CapabilityMapper`, `CheckModelNodeVisitor` and `TransformModelNodeVisitor` must be updated similar to the implementation for different Linux distributions. Here, the changes required will probably be much larger than adapting the transformation process between Linux distributions, so designing and entirely seperate transform visitor might be the right choice. Windows stacks can also be bootstrapped with CloudFormation Init, for more Information take a look at [this section](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-windows-stacks-bootstrapping.html) of the AWS CloudFormation documentation.