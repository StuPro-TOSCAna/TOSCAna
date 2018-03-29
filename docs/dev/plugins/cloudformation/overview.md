# Overview

The following is a quick overview of the chapters of the developer documentation for the CloudFormation plugin.

    TODO Add links to chapters

- AWS CloudFormation
    - CloudFormation Basics
    - Cloudformation Builder
- Architecture
- Transformation
    - Transformation Workflow
    - Transformation by TOSCA type
    - Transformation by Example
- Deployment
    - Deployment Workflow
    - Target artifact
- Development notes
    - Setting up a test environment
    - Expanding the CloudFormation plugin
    - Limitations

First, we'll give a quick introduction to AWS CloudFormation as well as the [cloudformation-builder](https://github.com/StuPro-TOSCAna/cloudformation-builder), a library for building CloudFormation templates in Java.

Then, we'll take a look at the underlying class architecture of the plugin.

Next up is the core part of the documentation, a detailed explanation of the transformation process and mapping of TOSCA types to CloudFormation resources.

The next chapter explains the deployment of the CloudFormation template created by the transformation with our plugin.

At last we'll discuss which prerequisites should be met before development for and/or subsequent testing of the CloudFormation Plugin and we'll explain what **you** should know if you want to expand the plugin. Finally, the limitations and known issues with the current version of the plugin will be addressed.
