# Cloud Foundry Plugin

## Basic information of Cloud Foundry (CF)
CF is a open-source Platform as a Service model (PaaS) which allows everyone to easily deploy an existing application on an infrastructure e.g. AWS, vSphere or OpenStack.
CF installs everything what the application needs to compile and run like OS, buildpack with all languages, libraries, and services that the app uses.

For further information have a look at the official [CF Documentation](https://docs.cloudfoundry.org/concepts/overview.html).
## Transformation
The plugin shall transform the given TOSCA-model into CloudFoundry deployment artifacts.
### Workflow
This workflow describes the steps of the plugin

plugin...
- gets TOSCA data model from core
- reads the model
  - traverses given model
  - checks the NodeType of every NodeTemplate
  - compares the NodeType with a fix internal model
    - this fix internal model represents supported NodeTypes & Relationships
    - for every NodeType exists a rule which describes a suitable action for CloudFoundry. E.g. NodeType "mysql-database" which is connected to the application-node. That means for CloudFoundry that a mysql-service has to be created in the deployment-script (before the deployment of the application). Also there has to be an entry in the manifest which binds the service to the application.
- translate properties and attributes of the nodes (e.g. applicationname, size of storage, ...) into manifest entries
  - therefore the plugin has to know the different properties of the NodeTypes
  - for every property there is a rule what to do with
- creates a deployment script. This script uses CF-CLI commands
  - so the plugin has to know the order of creation.
- return the created artifacts (manifest.yml and optional a deployment-script) respectively the path to it

In summary the plugin needs a list of the supported nodes of the Tosca-Model and the properties of it. The plugin knows what to do with this information. Therefore it needs for every nodetype and every propertie a rule.
You can find the architectural decision for using CloudFoundry-CLI in the [MADR](https://github.com/StuPro-TOSCAna/TOSCAna/blob/cloudFoundry-basics/docs/dev/cloudFoundry/01-Plugin-CloudFoundry-CLI.md).   
The plugin creates at least two files:
- deployment-script which owns all CF-CLI commands (optional)
- manifest.yml which owns the properties of the application   

The deployment-commands uses the manifest file as argument.
Maybe it is useful to save the response of the server to check if the deployment was successful.

### Example of a NodeType
In this chapter there is a short example for a node-type and what the plugin has to return.

#### Docker
CF supports the deploment of Docker. It has to add the properties to the manifest-file also to the deployment script.

##### manifest.yml
The manifest.yml needs a run-command to start the application inside the docker-image.
<pre><code>
...
applications:
- name: myAppName
  memory: 512M
  instances: 2
  docker:
    image: docker-image-repo/docker-image-name
    username: docker-user-name
</code></pre>
The manifest attribute username is optional. If it is used, then the password must be provided in the environment variable CF_DOCKER_PASSWORD


##### deployment-script
The script has to enable the docker-support. Afterwards the applications can be pushed with the manifest-file as attribute.
<pre><code>
...
cf enable-feature-flag diego_docker
cf push myAppName manifest.yml
...
</code></pre>


### Requirements/Issues
For this transformation-workflow there are some requirements and possible issues:
- using CloudFoundry-Platform assumes that is already installed on the target infrastructure
- the CloudFoundry-CLI has to be installed on the local machine

## Deployment
If the user wants to deploy the application automatically, the system could simply execute the created deployment-script.


## Additional information
- You can find a well documented manual for Cloud Foundry on the [official doc-website](https://docs.cloudfoundry.org/).   
