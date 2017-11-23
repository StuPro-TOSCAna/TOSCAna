# Cloud Foundry(CF) - Planning
This document show the planning and the next steps for the CF-plugin.

## Playground
To test some CF specific functionalities and to determine how the different node-types could be implemented it is useful to have a CF environment.
Therefore we have an "test-account" on pivotal.
For automatical testing it is probably useful to checkout how to implement CF on vSphere.

### Tests
To create end-to-end tests the java library [CF-JavaClient](https://github.com/cloudfoundry/cf-java-client) might be useful. Using it in the plugin-implementation is not a option, because the plugin only creates the target artifact and does not connect to the cf instance. (But this decision is not finally. Has to be discussed!)

## Next Steps

### Open Issues
- create a table with all supported node types (normative and non-normative) and a description how to handle the deployment of them
- create a [test-CSAR](https://github.com/StuPro-TOSCAna/TOSCAna/pull/211)
- investigate how to write the manifest.yaml
- determine how to get the information about the need of additional buildpacks (e.g. a LAMP app needs a additional buildpacks for supporting php-mysql)
- determine how to use services
  - e.g. suggest a service name and the user has to create a service with this name --> write it down in the readme
  - e.g. interact with user while transformation and plugin creates service by itself
- creating a CF-transformation-flow
- investigate how to test the CF-plugin
- how to handle not supported services? Write it down.
  - e.g. OS, in CF the applications run in Linux based container
  - e.g. Loadbalancer, in CF there is a Loadbalancer
- investigate how to get the input variables; how to set the environment-variables in CF (cf set-env ...)

### Roadmap
The next step is to test the LAMP-csar. Therefore the plugin have to know following node-types:
- tosca.nodes.Compute
- tosca.nodes.DBMS.MySQL
- tosca.nodes.Database.MySQL
- tosca.nodes.WebServer.Apache
- tosca.nodes.WebApplication

For this the `connects-to` and `hosted-on` relationships are sufficient.
