# AWS CloudFormation Plugin

This is the developer documentation for the CloudFormation Plugin.

Following topics will be discussed:

- Requirements
- AWS CloudFormation
  - The cloudformation-builder
  - Example
- Architecture
- Plugin Lifecycle Operations
  - Prepare
  - Transformation (with mapping)
  - Example(LAMP, JAVA)
- Deployment (see readme)
  - Deployment Workflow
  - Target artifact
    - Deployment scripts
- Expanding the plugin
- Known issues

## Overview

The CloudFormation Plugin is using the lifecycle to check the model, prepare it and finally transform it to a CloudFormation template.
Input to the plugin is no longer the original TOSCA model but the TOSCAna [EffectiveModel](../../model/effective-model.md).
Checking the model is pretty basic since we only check which NodeTypes the plugin is able to transform.
Preparing and transforming the model is more complex.
It will be discussed in the [transformation](cloudformation-transformation.md) document.

But first we will discuss which requirements are set to use as well as develop the plugin.
Then we will give a quick overview over CloudFormation as well as the [cloudformation-builder](https://github.com/StuPro-TOSCAna/cloudformation-builder).
The cloudformation-builder is a library that was developed during the implementation of this project and simply addresses the building of CloudFormation Templates using Java Objects.
After that the architecture of the plugin will be addressed as well as the plugin lifecycle operations.
The transform part is the most important because its the core of the plugin.

For deploying the application the deployment part will be interesting.
At the end we will explain how **you** can expand the plugin. For example implementing the support of more NodeTypes.
Also we will list known issues.
