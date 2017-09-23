# TOSCAna Architecture

## Component Diagram

The following component diagram describes the architecture of the TOSCAna Transformator.

![Component Diagram](img/components_v2.png)

***Note:*** This diagram was created using LucidChart. The source file can be found [here](https://www.lucidchart.com/invitations/accept/65f6e322-d8a2-4645-850f-e8f2893fc408).

### Core Package

The core package contains all core components of the **TOSCAna** application.

#### Controller

The controller represents the main component in the core package.
It connects the Rest-API with the persistent storage (archive) and the transform component.

#### API-Server

The API-Server hosts a REST-based API. The CLI and Web Frontend will connect to the Core Application using the provided REST-API.

### Archive

The Archive component abstracts file access. It can store all kind of artifacts (CSARs and TargetArtifacts) and other relevant data to disc and offers access to these in a simple manner. 
Its public interface contains means to store, receive and delete data in a context sensual way (e.g. storeCSAR(..) etc).  
The Archive uses the structure of the REST-api for storing data.   
Example: The resulting target artifact of the transformation from app 'simpleapp' to the platform 'aws' would reside in file `'$ARCHIVE\_ROOT/apps/simpleapp/transformations/aws/artifact`.  
This makes the file database explorable even with a normal file explorer.

##### Open Questions

- What framework will be used to implement the REST-API?

#### Open Questions and Ideas

### CLI

### WebUI

### AWS Plugin
