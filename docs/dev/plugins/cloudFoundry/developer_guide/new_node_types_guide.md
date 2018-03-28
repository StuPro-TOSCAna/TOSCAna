# Support new NodeTypes with the CloudFoundry plugin
The purpose of this document should show how you could add new NodeTypes to the CloudFoundry plugin that the plugin supports it afterwards.   
**IMPORTANT NOTES:**   
* It depends on the NodeType which actions have to be done, this document can only show a rough overview which steps could be helpful.   
To implement a new NodeType you should read following documents before:
  - [CloudFoundry basics](../CloudFoundry_Basics.md)
  - [Transformation doc](transformation.md)
  - [Script overview](Script-Overview.md)
  - [CloudFoundry supported NodeTypes](CloudFoundry_NodeTypes.md)
* Please be sure that the new NodeType is supported by the TOSCAna model

## Visitors
###`NodeTypeCheck`
The plugin will throw a exception if the model contains an unsupported NodeType. To avoid this exception the visitor `NodeTypeCheck` has to be adapted.
Just insert a overriding method with the new NodeType like this:
```java
@Override
   public void visit(NewNodeType node) {
       //no action here
   }
```

###`PrepareVisitor`
If your new NodeType need some preparation like the `environment recognition` just insert a overriding method with the new NodeType.   
To get more information about this visitor take a look [here](transformation.md).

###`NodeVisitor`
This visitor are responsible to fill the cf-app (CloudFoundry application) when visiting a special node type.   
You maybe have to call some methods of the current application to add some properties. If there are methods missing you have to add it in the `Application` (see next chapter).
Insert a overriding method with the new NodeType. Afterwards you could implement the logic what to do when visiting the new type.   
Following methods you maybe have to call or adapt:
- `handleStandardLifecycle` contains the logic what to do with the lifecycle operations (e.g. add paths to cf-app)
- `getScripts` gets the scripts out of the configure and create operation of the current node. The scripts will be executed by python scripts during the deployment

## CF-application

## FileCreator

## scripts
