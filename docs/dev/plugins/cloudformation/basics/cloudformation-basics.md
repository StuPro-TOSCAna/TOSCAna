# CloudFormation Basics

This chapter offers a quick explanation of AWS CloudFormation and its basic concepts.

## CloudFormation Templates

AWS CloudFormation is a framework for creating, managing and provisioning AWS resources through the use of templates developed by Amazon. Such a template can be written using the CloudFormation specification in either JSON or YAML.

For example, a CloudFormation template for creating an EC2 instance using the `ami-79873901` image and `t2.micro` instance type would look like this:

```YAML
AWSTemplateFormatVersion: "2010-09-09"
Resources:
  instanceName:
    Type: "AWS::EC2::Instance"
    Properties:
      ImageId: "ami-79873901"
      InstanceType: "t2.micro"
```

For the full capabilities and more information about the specification of CloudFormation templates, please refer to the latest CloudFormation [User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html).

## CloudFormation Stacks

In addition to provisioning Amazon resources with CloudFormation, existing resources created through CloudFormation can be changed by updating the template after deployment.

Each deployment of a CloudFormation template results in a so-called stack. This CloudFormation stack is the representation of the CloudFormation template and its corresponding resources on the AWS platform. Changes to the template of a created stack are directly reflected in the resources corresponding to that stack and updated accordingly. This allows the user to manage his CloudFormation stacks even after deployment.

## Additional Info

Additional information about AWS CloudFormation can be found here:

- [User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html)
- [API Reference](https://docs.aws.amazon.com/AWSCloudFormation/latest/APIReference/Welcome.html)
- [Product Page](http://aws.amazon.com/cloudformation/)
