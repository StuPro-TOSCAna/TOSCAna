# Product Backlog

## Epic *Core* (must have)

> Description

### Feature *REST-API* (must have)

> As a developer I want to control the application via a REST-api, in order to create a unified access for our application. 

- Cost estimate: 20

### Feature *CSAR validation* (must have)
> As a user I want to get feedback whether my CSAR file is correct.

- Cost estimate: 20

#### Implementable Story *Syntax validation* (must have)
> As a user I want to get feedback whether my CSAR file is correct in terms of syntax.

#### Implementable Story *Semantic validation* (could have)
> As a user I want to get feedback whether my CSAR file is correct in terms of semantics. 

### Feature *Monitoring*
> As a user, I want to get informed about the current status of my deployment.

#### Implementable Story *Endpoint of deployment* (must have)
> As a user, I want to get information about the endpoint of the application.

#### Implementable Story *Error reporting* (must have)
> As a user, I want to get informed about any issues regarding my deployment and see its current status.

#### Implementable Story *Status of Deployment* (must have)
> As a user, I want go get current status information of my deployed app.

### Feature *Plugin-Interface* (must have)
> As a developer I want to extend the core with plugins.

### Feature *Request additional data* (must have)
> As the user, I want the application to ask me about any data (e.g. credentials) which are required for deployment, but not specified in the TOSCA model.

## Epic *Web-Application: UI* (must have)
> As a user I want to control the application via web-UI.

## Epic *CLI* (must have)
> As a user I want to control the application via command line interface.

## Epic *Plugin: AWS Cloud Formation* (should have)
> As a user I want to transform my TOSCA model into an equivalent artifact for AWS Cloud Formation.

## Epic *Plugin: Cloud Foundry* (should have)
> As a user I want to transform my TOSCA model into an equivalent artifact for Cloud Foundry.

## Epic *Plugin: Kubernetes* (should have)
> As a user I want to transform my TOSCA model into an equivalent artifact for Kubernetes.

## Epic *Topology Optimization Mode* (could have)
As a user I want the possibility to optimize the given topology for the target platform.
