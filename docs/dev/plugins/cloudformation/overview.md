# Overview

The following is a quick overview of the chapters of the developer documentation for the CloudFormation plugin.

- [Overview](overview.md)
- [Architecture](architecture.md)
- AWS CloudFormation
    - [CloudFormation Basics](basics/cloudformation-basics.md)
    - [Cloudformation Builder](basics/cloudformation-builder.md)
- Transformation
    - [Transformation Workflow](transformation/transformation-workflow.md)
    - [Transformation by TOSCA type](transformation/supported-types.md)
    - [Transformation by Example](transformation/transformation-examples.md)
- Deployment
    - [Deployment Workflow](deployment/deployment-workflow.md)
    - [Target artifact](deployment/target-artifact.md)
- Development notes
    - [Expanding the CloudFormation plugin](development-notes/extending-cloudformation.md)
    - [Limitations](development-notes/limitations.md)

## Summary

First, we'll take a look at the underlying class architecture of the plugin.

Then, we'll give a quick introduction to AWS CloudFormation as well as the [cloudformation-builder](https://github.com/StuPro-TOSCAna/cloudformation-builder), a library for building CloudFormation templates in Java.

Next up is the core part of the documentation, a detailed explanation of the transformation process and mapping of TOSCA types to CloudFormation resources.

The next chapter explains the deployment of the CloudFormation template created by the transformation with our plugin.

At last, we'll discuss which prerequisites should be met before development for and/or subsequent testing of the CloudFormation Plugin and we'll explain what **you** should know if you want to expand the plugin. Finally, the limitations and known issues with the current version of the plugin will be addressed.
