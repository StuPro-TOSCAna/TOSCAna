# Product Backlog

## Epic *Core* (must have)

> As a developer I want that the application is controlled by the core.

### Feature *Logging* (must have)

- [ ] add logging framework to project
- [ ] make transformation-specific logs available for http client

### Feature *REST API* (must have)

> As a developer, I want to control the application via a REST API, in order to create a unified access for our application. 

- Cost estimate: 20

### Feature *CSAR Validation* (must have)
> As a user, I want to get feedback whether my CSAR file is correct.


#### Implementable Story *Syntax Validation* (must have)
> As a user, I want to get feedback whether my CSAR file is correct in terms of syntax.

- Cost estimate: 13

### Feature *Monitoring*
> As a user, I want to get informed about the current status of my deployment.

#### Implementable Story *Endpoint of Deployment* (must have)
> As a user, I want to get information about the endpoint of the application.

- Cost estimate: 2

#### Implementable Story *Error reporting* (must have)
> As a user, I want to get informed about any issues regarding my deployment and see its current status.

- Cost estimate: 8

#### Implementable Story *Status of Deployment* (must have)
> As a user, I want to get status information of my current deployment.

- Cost estimate: 3

### Feature *Plugin Interface* (must have)
> As a developer, I want to extend the core with plugins.

- Cost estimate: 20

### Feature *Request additional Data* (must have)
> As a user, I want the application to ask me about any data (e.g. credentials) which are required for deployment, but not specified in the TOSCA model.

- Cost estimate: 5

- [ ] add application-specific properties to transformation in data model

## Epic *Web Application* (must have)
> As a user, I want to control the application via web-UI.

- Cost estimate: 20

## Epic *CLI* (must have)
> As a user, I want to control the application via command line interface.

- Cost estimate: 5

## Epic *Plugin: AWS Cloud Formation* (should have)
> As a user, I want to transform my TOSCA model into an equivalent artifact for AWS Cloud Formation.

- Cost estimate: 40

## Epic *Plugin: Cloud Foundry* (should have)
> As a user, I want to transform my TOSCA model into an equivalent artifact for Cloud Foundry.

- Cost estimate: 40

## Epic *Plugin: Kubernetes* (should have)
> As a user, I want to transform my TOSCA model into an equivalent artifact for Kubernetes.

- Cost estimate: 100

## Epic *Topology Optimization Mode* (could have)
As a user, I want the possibility to optimize the given topology for the target platform.

- Cost estimate: 100
