# CF-Plugin Scripts overview
To transform without pushing anything we need to extract some logic out of the transformation and add it to deploy scripts.   
All python scripts you will find in the resource folder see [here](https://github.com/StuPro-TOSCAna/TOSCAna/tree/master/server/src/main/resources/cloudFoundry/deployment_scripts).   
We add all commands and execution of python scripts into the `deploy_application.sh`. This should be done while the transformation process.   
Therefore the script have to do following things:

## Create the Services needed for the App
To create a service, use: `cf create service`.   
The services has to be created at first because the application will be bound to it.

## Push the App to Cloud Foundry
To push it use: `cf push myapp`.   
With the addition `--no-start` the application is just pushed and not started.   
That is necessary to set the environment variables (e.g. database_user) afterwards without failures.

## Python readCredentials
The plugin has a kind of environment-variable-semantic recognition. The plugin detects the environment variables and recognizes the semantic behind them.   
Therefore in the TOSCA model the variable has to point to the destination property. For example the variable `database_user` has to point to the user property of the database node.   
A requirement is if there is a database in the model, the model has to contain a variable called `database_host` which must be used for the host value.   
The plugin creates a file `appName_environment_config.txt` in which the semantic of the variables will be described.   
It looks like this:
```
{
'cf_database_user_placeholder_my_db':'database_user',
'cf_database_name_placeholder_my_db':'database_name',  
'3306':'database_port',  
'cf_database_password_placeholder_my_db':'database_password'
 }
```
The python script reads this file and knows the meaning of each variable. The placeholder names are fixed values in the java code and in the python script as well. It is important that these values are the same.   
Afterwards the script is able to read the credentials of the service and set the values to the environment variable.   
Also this script creates a config file with all service credentials for later use.   
Lamp Example: `cf set-env $appName database_user = $database_user`

## Python replace paths in file
In some scripts could be paths which are not available on the warden container. So this paths has to be replaced with existing paths.   
Twelve Factor: There are no paths which depends on the environment.

## Python configureMysql
The readCredentials script creates a config file with all needed service credentials which are necessary to create a connection.
The configureMysql script creates a connection to the service with this config file. If there is a valid connection the script will execute the .sql file on the database.   
Requirement is that there is a .sql script. An alternative solution is to insert the database init script into the application code.   
Needs a additional python mysql-python package `mysql.connector`.
Lamp Example: `mysqlCursor.execute(dbInitCommand)`
Twelve Factor: there have to be a init database script. So we just have to execute this script on the container.

## Python execute commands
To execute scripts on the container it is needed to create a ssh connection to the container and execute the script.   
Therefore the script enables ssh for the application. Afterwards the script creates a ssh-connection and executes the file.   
Unfortunately the cf command `cf run-task ...` is not suitable because it has to enabled from the CF instance provider.

## Order
1. `cf create service $servicename $serviceplan $serviceInstanceName`
2. `python replace.py $pathToFile $stringToReplace $newString`
3. `cf push $appName -f $pathToManifest --no-start`
4. `python readCredentials.py $appName $serviceName $serviceType $serviceInstanceName`
5. `python configureMysql.py $sqlFile`
6. `cf start $appName`
7. `python executeCommand.py $appName $pathInWardenContainter`
8. `cf routes` (just show the route to the application)
