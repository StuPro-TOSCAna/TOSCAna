# Use CloudFoundry-CLI commands

The CloudFoundry-Plugin needs to communicate with the cloud foundry platform instance.

## Considered Alternatives

* [CloudFoundry-CommandLine](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)
* [HTTP Calls](https://apidocs.cloudfoundry.org/272/)

## Decision Outcome

* Chosen Alternative: CloudFoundry-CLI
* The CF-CLI has to be installed on the local machine
* Therefore the user has to follow the [installation guide](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)

## Pros and Cons of the Alternatives
### CloudFoundry-CLI
`+` well documented  
`+` well readable   
`-` user has to install the CF-CLI

### HTTP Calls
`+` well documented  
`-` complicated to read   
`-` more files are needed, e.g. headers
