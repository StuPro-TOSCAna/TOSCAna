# Cloud Foundry Plugin

## Basic information of Cloud Foundry (CF)
CF is a open-source Platform as a Service model (PaaS) which allows everyone to easily deploy an existing application on an infrastructure e.g. AWS, vSphere or OpenStack.   
CF installs everything that the application needs to compile and run like OS, buildpack with all languages, libraries, and services that the app uses.

For further information have a look at the official [CF Documentation](https://docs.cloudfoundry.org/concepts/overview.html).

## Transformation
The plugin transforms the given TOSCA-model into CloudFoundry deployment artifacts.   
To simplify the deployment of an application, the CF plugin will add several deployment scripts to the target artifact. Therefore the user needs a CloudFoundry CLI which is connected to a CloudFoundry instance.   
For further information have a look in the transformation documentation see [here](Transformation_Process.md).

## Node Types
The plugin supports some node types of the TOSCA specification.
A developer could adapt the code to provide additional node types.   
A list with all supported node types you find [here](Supported_Node_Types.md) and how to add new node types you find [here](How_add_new_Node_Types.md).

### Requirements/Issues
The Cloud Foundry Plugin has some requirements:
- using CloudFoundry Platform assumes that is already installed on the target infrastructure
- the CloudFoundry CLI has to be installed on the local machine
- python (min. version 2.7) has to be installed
- Databases need an environment variable with the name `database_host`. We decided this convention with our supervisors.
Further information you get [here](Known_challenges.md).

## Deployment
To deploy an application the user just execute the deploy script which will be provided by the plugin after transformation e.g. type `bash deploy.sh` in the terminal.   
The user is able to adapt some credentials (e.g. services). For further information see the User Guide.

## Additional information
You can find a well documented manual for Cloud Foundry on the [official doc-website](https://docs.cloudfoundry.org/).   
