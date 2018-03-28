# Introduction

This is the developer documentation for the CloudFormation Plugin.

    TODO: Add a small introduction.

## Overview

Following topics will be discussed:

### TODO Add links to chapters

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

First, we'll give a quick overview over CloudFormation as well as the [cloudformation-builder](https://github.com/StuPro-TOSCAna/cloudformation-builder).
The cloudformation-builder is a library that was developed during the implementation of this project and simply addresses the building of CloudFormation Templates using Java Objects.
After that the architecture of the plugin will be addressed as well as the plugin lifecycle operations.
The transform part is the most important because its the core of the plugin.
The CloudFormation Plugin is using the lifecycle to check the model, prepare it and finally transform it to a CloudFormation template.
Input to the plugin is no longer the original TOSCA model but the TOSCAna [EffectiveModel](../../model/effective-model.md).
Checking the model is pretty basic since we only check which NodeTypes the plugin is able to transform.
Preparing and transforming the model is more complex.
It will be discussed in the [transformation](cloudformation-transformation.md) document.

For deploying the application the deployment part will be interesting.

Then we'll discuss which prerequisites should be met before development for and/or subsequent testing of the CloudFormation Plugin.
At the end we will explain how **you** can expand the plugin. For example implementing the support of more NodeTypes.

Also we will list known issues.

    TODO: Rewrite this explanation.
