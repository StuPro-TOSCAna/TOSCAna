# Cloud Foundry(CF) - Planning
This document show the planning and the next steps for the CF-plugin.

## Playground
To test some CF specific functionalities and to determine how the different node-types could be implemented it is useful to have a CF environment.
Therefore we have an "test-account" on pivotal.
For automatical testing it is probably useful to checkout how to implement CF on vSphere.

## Next Steps

### open issues
- create a table with all supported node-types (normative and non-normative) and a description how to handle these within CF
- create a [test-CSAR](https://github.com/StuPro-TOSCAna/TOSCAna/pull/211)
- investigate how to write the manifest.yaml
- determine how to handle with additional buildpacks (e.g. a LAMP app needs a additional buildpacks for supporting php-mysql)
- determine how to handle with services
  - e.g. suggest a service-name and the user has to create a service with this name --> write it down in the readme
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
