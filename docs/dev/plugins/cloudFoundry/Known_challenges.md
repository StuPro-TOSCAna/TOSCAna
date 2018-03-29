# Known Issues and Requirements in the CloudFoundry plugin
The purpose of this document is to collect all known issues in the CloudFoundry plugin. (This document is not exhaustive)

## Conventions
- if using a database the existing of an environment variable named `database_host`
- for each transformation a valid connection to a CloudFoundry instance is needed to get information about available services
- all environment variables will be set globally

## General problems
- most of the user provided data cannot be accounted because CloudFoundry will create automatically their own data.
- it depends on the used framework and services of the application if and which additional buildpacks have to be used. Each framework is using their own logic how to add additional buildpacks.
- direct connections (relationships) between applications are not supported yet
- always select the free plan of a service, not regarding the properties of the service like size
- a suitable service is chosen by comparing the service description of the service type in the plugin and the service description of the CloudFoundry instance

## Requirements
- a valid CloudFoundry instance
- CloudFoundry CLI is installed
- Python 2.7 is installed
- if using a `MYSQL` database the python package `mysql.connector` is needed
