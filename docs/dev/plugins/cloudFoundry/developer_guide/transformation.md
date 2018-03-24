# Cloud Foundry - Transformation Process

The purpose of this document is to show  the transformation logic and process of the CloudFoundry plugin for a developer.
This document does not mention all classes which are used in the java code but describes the method of the transformation so that the developer is able to understand the code and the transformation process.   
This is the texted based documentation, you can find a example transformation with some pictures in the same folder.

## Foreword
- the TOSCA model is a graph. So we are talking about some graph terms. For further information please read the documentation of the core/model.
- getting plugin properties to create connection to CloudFoundry instance
- plugin is using visitor patterns for nodes. Inside these the logic for the node types is implemented
- one manifest for all applications
- per application there is one CloudFoundry application object which contains all information of the application
- the output is called target artifact
- to start a transformation the credentials of the CloudFoundry instance is required

## Visitors
### Node visitor
The node visitor contains the logic for the supported node types. It fills the cf-app with all information out of the node.

### Prepare visitor
The prepare visitor prepares the model before the transformation. It sets some fixed placeholder to properties of some node types (e.g. Mysql-Database). This step is needed to enable the environment recognition.   
If a property of node points to a property to a node where the properties are replaced by placeholders, the plugin knows afterwards what the pointing variable want to get. The python scripts knows the placeholders as well and can check which environment variables have these values.   
If the wanted values are known, because the CloudFoundry container is started (most of the values like database_user are set from the CloudFoundry instance during deployment), the python script can read out these values and set it to the given environment variables (replace the placeholders).   
Code Example of the prepare visitor:
```java
@Override
   public void visit(MysqlDatabase node) {
       logger.debug("Prepare Model. Set placeholders for mysql database node");
       String nodeName = "_" + node.getEntityName();
       node.setUser(CF_PREPARE_DB_PLACEHOLDER_USER + nodeName);
       node.setDatabaseName(CF_PREPARE_DB_PLACEHOLDER_NAME + nodeName);
       node.setPassword(CF_PREPARE_DB_PLACEHOLDER_PW + nodeName);
       node.setPort(CF_PREPARE_DB_PLACEHOLDER_PORT);
   }
```

### OS Check
Checks that only supported operating systems are modeled. Only `Linux` is supported. If there is another OS the plugin will stop the transformation with a failure.

## General transformation process
Steps to be done:
- Split top nodes: create stacks (called application stacks) for each top node (node without children) and its parents
- Each stack will be a own application (without the node types which are implemented as service). Therefore the plugin creates a CloudFoundry application (called cf-app).
- Getting necessary information for each application stack: inputs, properties, services.... . This is done by the node visitor.
- If the visitor detects a node type which is declared as service, the whole cf-app (to which the stack of the node belongs to) will be declared as service. In this case it is not an own application but the service belongs to another cf-app. Therefore the visitor checks the connectsTo relationship and detects the parent application.
- copy files and create manifest and scripts

### Split in stacks
- Find all top nodes, Top nodes are nodes without incoming hostedOnConnections.
- Go through all hostedOnConnections of every top node to the compute node. All nodes on this way will be in one stack.
- Every top node is an individually cf-app, because on pushing the application with Cloud Foundry every top node gets its own container
- A service (node type which the plugin handles as service e.g. MYSQL database node) gets its own stack, because every service is a top node (only connectsTo relations as incoming edges).
- We share this logic with the Kubernetes plugin. So we are using the same methods to do this.

### Getting necessary information
In this step the plugin goes through every stack of nodes and calls the node visitor. The node visitor collects all necessary information like inputs, disk-size, paths and so on from the node and add the information to the cf-app.   
If there is a node type which is declared as service, the plugin checks the source node/s of the connects-to connection and adds the service with all belonging information to the source application/s. The already created cf-app of the stack from the service node, will be deleted.   
After this step all information of the nodes are in the cf-apps.

## File Creator
The File Creator is one of the most important class in the Cloud Foundry plugin. This class creates all files for the target artifact.   
The class gets all cf-apps and detects which files (python scripts) are needed. To make sure that all files are available at the CloudFoundry container, the FileCreator copies all files of the source application to the target artifact. Each application gets its own application folder. The folders are named with app+Number e.g. `app1`.   
If a application owns a service, all files of the service (which are modeled in the TOSCA model by the user) are copied to target artifact as well.

### Manifest
The manifest contains the meta data of the CloudFoundry deployment which is needed for deployment.
It is a yaml file and contains following things:
- attributes like name, path, disk_size
- environment variables like `database_user`
- services which should be bound to the application like a database
- additional settings like `random-route`. This setting creates a random route to the application. This setting is a default of the CloudFoundry plugin to avoid failures while pushing caused by double routes. If the property `domain` is set in the TOSCA model the value of this property overwrites the default.
All cf-apps are in one manifest in different application sections.   
The FileCreator reads out of the cf-app all necessary information to create the manifest.   
The paths to the application folders are relative so you can move the whole artifact easily.
The manifest will be copied in the output folder.
A example of a manifest from the plugin:
```yaml
---
applications:
- name: my-app
  path: ../app1
  disk_quota: 4000MB
  mem_size: 1024MB
  random-route: true
  env:
    database_user: cf_database_user_placeholder_my_db
    database_name: cf_database_name_placeholder_my_db
    database_port: 3306
    database_password: cf_database_password_placeholder_my_db
  services:
    - my_db
```

### Services
If the cf-apps contains a service the FileCreator looks for a suitable service of the provide. Therefore the plugin creates a connection to the provider with the given credentials (with credentials given from the user). It receives all services which are offered of the provider. To do that the plugin implements a (java cf client)[https://github.com/cloudfoundry/cf-java-client].    
A CloudFoundry service consists of a service name and a service plan. The service name can differ from provider to provider, that the cause why the plugin needs a connection to the CloudFoundry provider. The service plan specifies the settings of the services e.g. size of a database service. The plugin is looking for a key word (for a mysql service the keyword is `MySQL`) in the service descriptions and select a suitable service name. Also it selects the free plan as default.   
All services will be printed in a extra file called `all_services.txt` in the output folder, so that the user could select another service or change the plan.

### deploy bash script
To provide an easy deployment for the user, the plugin creates a bash deploy script called `deploy_application.sh`. This consists of different commands in a fixed order.   
There are two types of commands:
1. CloudFoundry CLI commands to create services, push the app, start the app, among other things.
2. python scripts, please see section `python scripts` for further information.
The FileCreator checks each cf-app and fills the deploy script with the needed commands.   
Here you can see a neckline of a `deploy_application.sh`:
```bash
...
check "cf"
cf create-service cleardb spark my_db
check python
python replace.py ../../app1/my_app/create_myphpapp.sh /var/www/html/ /home/vcap/app/htdocs/
cf push my-app -f ../manifest.yml --no-start
python readCredentials.py my-app cleardb mysql my_db
python configureMysql.py ../../app1/my_db/createtable.sql
cf start my-app
python executeCommand.py my-app /home/vcap/app/htdocs/my_app/create_myphpapp.sh
...
```

### python scripts
For the easy deployment some python scripts are necessary. The FileCreator detects which scripts are needed and add the files from the resource folder to the target artifact and inserts a command in the `deploy_application.sh`.   
For further information about the scripts see (here)[TODO: insert Link to Script overview]

### additional buildpacks
CloudFoundry has a buildpack detection which detects which languages and libraries are needed. Sometimes there are some additional libraries needed which are not detected. Therefore the plugin has to add the information about the additional buildpacks to the target artifact. The plugin just look for the application suffix and compare it with the needed service and thereby knows which buildpacks are needed. At the moment it is implemented for `php` and a `mysql service`. For this case the plugin creates a additional file `.bp-config` in the app folder which contains a `options.json` file. This workflow may differ with other languages.
Example of buildpack additions file:
```json
{
    "PHP_EXTENSIONS": [
        "mysql",
        "mysqli",
        "bz2",
        "zlib",
        "curl",
        "mcrypt"
    ]
}
```

### environment recognition
In the prepare visitor the plugin already set some placeholders for some node types. Please read this section before continue.   
The plugin writes all environment variables with the values in an extra file called `$appName_environment_config.txt` in the output folder. This file is structured for the python use as dictionary.   
Example:
```
{
'cf_database_user_placeholder_my_db':'database_user',
'cf_database_name_placeholder_my_db':'database_name',
'3306':'database_port',
'cf_database_password_placeholder_my_db':'database_password'
}
```
So the python script `read_credentials.py` knows the environment variables and which values they expect.
