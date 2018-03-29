# Cloud Foundry - supported NodeTypes

## Overview
|Node Type | Doing | Description |Implemented |
|----------|------------|-----------------|------|
|tosca.nodes.Compute| add attributes to manifest  | OS could not be considered. CF uses automatically linux container. Storage-size and RAM size are supported |Yes |
|tosca.nodes.Database.MySQL | in CF databases are services. So the plugin adds an service to the application | User provided credentials could not be considered  |Yes |
|tosca.nodes.DBMS.MySQL| add artifacts and environment variables to application  | User provided credentials could not be considered  |Yes |
|tosca.nodes.Apache   | CF installs automatically an apache WebServer on the container if it detects a WebApplication | No implementation logic in the visitor  |Yes |
|tosca.nodes.WebApplication  | add artifacts and environment variables to application. Name of the node is the name of the application.   | -  |Yes (maybe has to be expanded to other WebApplications than PHP) |
|toscana.nodes.JavaRuntime | CF detects automatically the need of a JavaRuntime and installs it on the container  | -  |Yes |
|toscana.nodes.JavaApplication  | add artifacts and environment variables to application. Some special attributes are set  | Worker application  |Yes |

## Implementation details
The logic for each NodeType is implemented in the `NodeVisitor`. This visitor gets always an CloudFoundry application (cf-app) as parameter to which the nodes which should be visited belongs to.   
The cf-app is an object in the plugin which represents an application in the CloudFoundry world. For the transformation logic please read the [transformation document](Transformation_Process.md).   
In this chapter is described what happens in the `NodeVisitor`.
Please note following things:
- all inputs of the Tosca model will be treated as environment variables which will be set in the container of the application on the CloudFoundry instance

### `Compute`
The visitor just add some attributes (disk size, domain and memory) to the cf-app which it read out of the node.   
Example for the memory attribute:   
```java
...
if (node.getHost().getMemSizeInMb().isPresent()) {
            myApp.addAttribute(MEMORY, node.getHost().getMemSizeInMb().get() + "MB");
        }
...
```
To check if there is an unsupported operating system like `Windows or Mac` modeled, the plugin uses a separate visitor `OSCheck` in front of the transformation.

### `Database.MySQL`
This node type is declared as service. That means with CloudFoundry the plugin creates a MySQL database service. The stack in which contains this node, is not a cf-app (to have further information about creating the stacks please have a look into the [transformation document](Transformation_Process.md)).   
A database node always have a connects to relationship, so there is a source node which want to use this database. The visitor checks all source nodes of this database and marks these as `parent application`. Also it adds to the parent application a `MYSQL Service`.   
The current application will be marked as `dummy application`. Later in the transformation all data of dummy applications will be copied to their parent applications.   
All artifacts will be added to the cf-app to make sure that the cf-app has all files of the node. If there is an `.sql` file the visitor assumes that this must be a config file and add this information to the cf-app.   
```java
...
if (path.endsWith("sql")) {
                myApp.addConfigMysql(node.getEntityName(), path);
                logger.info("Found a SQL script in artifact paths. Will execute it with python script in deployment phase");
            }
...
```
User provided data like database_name can not be considered because the CloudFoundry instance creates their own credentials. The plugin detects the semantic behind the input variables through an environment detection. For further information please see the [transformation document](Transformation_Process.md).   
>Note: The database node has to contain a variable called `database_host` which must be used for the host value.

This is needed because the plugin has no chance to get the semantic of a random variable which should contains the host information. The problem is that there is no official node host property. So the plugin team decides in coordination with the supervisors to make a convention.

### `DBMS.MySQL`
The visitor just adds all files and environment variables to the cf-app. There is nothing more to do because CloudFoundry creates automatically a `MYSQL DatabaseManagementSystem` when creating a service.

### `Apache`
Nothing to do, because the CloudFoundry instance creates automatically a apache WebServer if needed.

### `WebApplication`
The name of the WebApplication node will be the name of the cf-app because it is a top node and contains probably the main application logic.   
Also the visitor checks if there are configure or create scripts existing. If yes the plugin adds the information to the cf-app. These scripts will be executed by a python script during the deployment. To get further information about the scripts have a look [here](Python_Scripts_Overview.md).   
All files and environment variables will be added to the cf-app as well.   
Here you see a example of how the visitor is getting the configure script of a node:
```java
...
Optional<Operation> configureOptional = lifecycle.getConfigure();
        //get configure script
        if (configureOptional.isPresent()) {
            Optional<Artifact> configureArtifact = configureOptional.get().getArtifact();
            configureArtifact.ifPresent(artifact -> application.addExecuteFile(artifact.getFilePath(), node));
        }
...
```

### `JavaRuntime`
Nothing to do, because the CloudFoundry instance detects automatically which runtime is needed and installs it.

### `JavaApplication`
The name of the WebApplication node will be the name of the cf-app because it is a top node and contains probably the main application logic.  
The visitor adds a `no-route: true` attribute to the manifest because this node is a worker application (it is not a WebApplication) so there is no direct route to the application available.
```java
myApp.addAttribute(ManifestAttributes.NO_ROUTE, "true");
```
Also the visitor adds a `healthcheck-type: process` to the manifest because a normal healthcheck is done by a http-request but this is not working with worker application without a webserver. See the official CloudFoundry explanation for healthcheck type `process`:
> For a process health check, Diego ensures that any process declared for the app stays running. If the process exits, Diego stops and deletes the app instance.

See the java code in the visitor:
```java
myApp.addAttribute(ManifestAttributes.HEALTHCHECK_TYPE, "process");
```
All files and environment variables will be added to the cf-app as well.   
Also the visitor checks if there are configure or create scripts existing. If yes the plugin adds the information to the cf-app. These scripts will be executed by a python script during the deployment. To get further information about the scripts have a look [here](Python_Scripts_Overview.md).  
