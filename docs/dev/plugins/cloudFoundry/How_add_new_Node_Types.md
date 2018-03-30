# Support new NodeTypes with the CloudFoundry plugin
The purpose of this document should show how you could add new NodeTypes to the CloudFoundry plugin that the plugin supports it afterwards.   
**IMPORTANT NOTES:**   
* It depends on the NodeType which actions have to be done, this document can only show a rough overview which steps could be helpful.   
To implement a new NodeType you should read following documents before:
  - [CloudFoundry basics](Basics.md)
  - [Transformation doc](Transformation_Process.md)
  - [Script overview](Python_Scripts_Overview.md)
  - [CloudFoundry supported NodeTypes](Supported_Node_Types.md)
* Please be sure that the new NodeType is supported by the TOSCAna model

## Visitors
Following visitors you can find [here](https://github.com/StuPro-TOSCAna/TOSCAna/tree/master/server/src/main/java/org/opentosca/toscana/plugins/cloudfoundry/visitor)
### `NodeTypeCheck`
The plugin will throw a exception if the model contains an unsupported NodeType. To avoid this exception the visitor `NodeTypeCheck` has to be adapted.
Just insert an overriding method with the new NodeType like this:
```java
@Override
   public void visit(NewNodeType node) {
       //no action here
   }
```

### `PrepareVisitor`
If your new NodeType needs some preparation like the `environment recognition` just insert an overriding method with the new NodeType.  
Maybe you have to add new placeholders for some properties of the new NodeType. If you add new ones you have to adapt the python script `readCredentials.py` (see in chapter "scripts").
To get more information about this visitor take a look [here](Transformation_Process.md).

### `NodeVisitor`
This visitor is responsible to fill the cf-app (CloudFoundry application) when visiting a special node type.   
You maybe have to call some methods of the current application to add some properties. If there are methods missing you have to add it in the `Application` (see next chapter).
Insert an overriding method with the new NodeType. Afterwards you could implement the logic what to do when visiting the new type.   
If your new NodeType is a service in the CloudFoundry world you have to add a new `ServiceType` in the enum class [ServiceTypes](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudfoundry/application/ServiceTypes.java). Consider that the name of the type is responsible for matching a suitable service from the CloudFoundry provider.   
Following methods you maybe have to call or adapt:
- `handleStandardLifecycle` contains the logic what to do with the life cycle operations (e.g. add paths to cf-app)
- `getScripts` gets the scripts out of the configure and create operation of the current node. The scripts will be executed by python scripts during the deployment

## CloudFoundry application
The class `Application` you can find [here](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudfoundry/application/Application.java) contains all information about the CloudFoundry application.   
These information are used by the `FileCreator` later (see next chapter).   
There are a lot of `setter` and `getter` inside to provide the possibility to add attributes, environment variables and so on.   
If your new NodeType needs additional information you have to add corresponding methods.   
Some important methods:
- `addExecuteFile` will enable the execution of a file on the warden container (where the application is running on the CloudFoundry instance after deployment) by a python script (see chapter "scripts"). This method needs the path to the file on the warden container. This path may differs depending on the type. In this case add an additional if clause like that:
```java
if (parentTopNode instanceof NewNodeType) {
           pathToFileOnContainer = "/path/to/file/on/container";
       }
```
- `setPathToApplication` sets the path to the main application which should be executed. It depends on the kind of application whether the CloudFoundry instance needs the information which file is the "main" file (e.g. JavaApplication). The method checks if the given file has a valid suffix as a main application file (`.sh` and `.sql` are not allowed).
- `isRealApplication` if the cf-app is a service, this is a "unreal" cf-app. For details look into the [transformation document](Transformation_Process.md)

## FileCreator
The class `FileCreator` you can find [here](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudfoundry/filecreator/FileCreator.java) creates all files which are in the target artifact. It gets the information out of the cf-apps.   
Order of executions:
1. `createFiles()` invoke all following methods.

2. `createManifest()` creates a manifest file and adds all necessary information like name, attributes or services. It creates for each cf-application an own section in the manifest. Probably you don't have to make changes here because all possible information types are provided.
3. `createDeployScript()` invokes all methods to create the deploy script with the necessary commands for all cf-apps. The class `Deployment` (you can find [here](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudfoundry/application/deployment/Deployment.java)) provides the addition of the python scripts. It is also looking for the suitable service. Therefore the class `ServiceHandler` (you can find [here](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudfoundry/application/deployment/ServiceHandler.java)) provides the functionality. At the moment the `ServiceHandler` is looking for a suitable service by looking for matching description. You maybe have to expand the logic.
4. `createBuildpackAdditionsFile()` calls a method named `detectBuildpackAdditions()` from class `BuildpackDetector` see [here](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudfoundry/application/buildpacks/BuildpackDetector.java) to detect if additional buildpacks are needed. If your new NodeType needs additional buildpacks you have to expand the code here. Example:
```java
if (applicationSuffix.equalsIgnoreCase("yourApplicationSuffix")) {
              // add your buildpacks
}
```
5. `insertFiles()` all files of the cf-apps will be copied to the corresponding output application folder. Probably you don't have to make changes here.
6. `createEnvironmentConfigFile()` creates an environment config file for each cf-app which contains all environment variables with their values (contains the placeholder value which are set in the `PrepareVisitor`). This file will be read by the python script `readCredentials.py`. Probably you don't have to make changes here.
7. `createReadme()` creates a readme text file. Maybe you want to adapt the readme therefore you just adapt the text [here](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/resources/cloudFoundry/readme.txt)

## Scripts
- if the new NodeType needs configure commands, you have to add a new python script. At the moment only `.sql` configure files for mysql databases are supported
- if you want to execute files with other suffixes than `.sh` you have to expand the `executeCommand.py` script
- if you adapted the `PrepareVisitor` with new placeholders, you have to adapt the `readCredentials.py` script. You have to define new strings and set them to the environment variables which matches to the placeholders. See here:
  ```python
  ...
  # env is the dictionary readed from the environment_config_file
  strEnvDatabaseUser = env["cf_database_user_placeholder_" + strServiceInstanceName]
  strEnvDatabaseName = env["cf_database_name_placeholder_" + strServiceInstanceName]

  # your new input:
  strNewEnvironmentVariable = env["your_placeholder_" + strServiceInstanceName]
  ...
  ```
  Afterwards you read the credentials out of the container:
  ```python
  ...
  # jsonEnv contains all environment variables of the warden container. So you could pick out what you need
  database_name = jsonEnv["VCAP_SERVICES"][serviceName][0]["credentials"]["name"]

  # your temporary save of the environment variable you need from the container
  new_tmp_name = jsonEnv["path_to_your_value"]

  # now you could provide your application the environment variable with the correct value
  # set environment variable in the CF container
  sub.call(["cf" ,"set-env",appName, strNewEnvironmentVariable, new_tmp_name])
  ...
  ```
- warden containers have a different file system than a typical linux system. Therefore there is the `replace.py` script to replace paths in files which should be executed on the warden container. To expand the list of paths add strings to the method `replaceStrings()` in the `FileCreator` class [here](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudfoundry/filecreator/FileCreator.java)

To get further information about the python scripts take a look into the [script documentation](Python_Scripts_Overview.md)
