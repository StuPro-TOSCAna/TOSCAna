# Known Issues and Requirements in the CloudFoundry plugin
The purpose of this document is to collect all known issues in the CloudFoundry plugin. (This document is not exhaustive)

## Conventions
- If using a database the existing of an environment variable named `database_host`
- For each transformation a valid connection to a CloudFoundry instance is needed to get information about available services
- All environment variables will be set globally

## General problems
- Most of the user provided data cannot be accounted because CloudFoundry will create automatically their own data.
- It depends on the used framework and services of the application if and which additional buildpacks have to be used. Each framework is using their own logic how to add additional buildpacks.
- Direct connections (relationships) between applications are not supported yet
- Always select the free plan of a service, not regarding the properties of the service like size
- A suitable service is chosen by comparing the service description of the service type in the plugin and the service description of the CloudFoundry instance
- When pushing a webapplication like `/my-app/myphpapp.php` the route to the app is `https://the_created_route.io/my-app/myphpapp.php`
- There was no clear definition about the `Twelve Factor` method found in our project

## Requirements
- Valid CloudFoundry instance
- CloudFoundry CLI is installed
- Python 2.7 is installed
- If using a `MYSQL` database the python package `mysql.connector` is needed
