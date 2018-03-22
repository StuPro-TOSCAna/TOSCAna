# Scripts overview
To transform without pushing anything we need to extract some logic out of the transformation and add it to deploy scripts.
We add all commands and execution of python scripts into the `deploy.sh`. This should be done while the transformation process.
Therefore the script have to do following things:

## Create the Services needed for the App
to create a service, use: `cf create service`

## Push the App to Cloud Foundry
to push it use: `cf push myapp`

## Python readCredentials ["cleardb"]
The plugin has a kind of environment recognition. The plugin detects the environment variables and recognizes the semantic.
Therefore in the TOSCA model the variable has to point to the destination. For example the variable `database_user` has to point to the user property of the database node.
A requirement is if there is a database in the model, the model has to contain a variable called `database_host` which must be used for the host value.
So the python script is able to set the credentials as environment variables of the container after it read the credentials out of the services of the app.
Lamp Example: `cf set-env $appName database_user = $database_user`

## Python replace paths in file
In some scripts could be paths which are not available on the warden container. So this paths has to be replaced with existing paths.
Twelve Factor: There are no paths which depends on the environment.

## Python configureMysql ["pathToSQLscript"]
connect to the mysql service and execute the sql script.
Requirement is that there is a .sql script. An alternative solution is to insert the database init script into the application code.
Needs a additional python mysql package
Lamp Example: `mysqlCursor.execute(dbInitCommand)`
Twelve Factor: there have to be a init database script. So we just have to execute this script on the container.

## Python execute commands
to execute scripts on the container it is needed to create a ssh connection to the container and execute the script.
Unfortunately is the cf command `cf run-task ...` not suitable because it has to enabled from the CF instance provider.

## Order TODO: rework order
1. `cf create service $servicename $serviceplan $serviceInstanceName`
2. `cf push $appName -f $pathToManifest`
3. `python readCredentials.py $appName $serviceName $serviceType`
4. `python replace.py $file $findString $replaceString`
5. `python executeScript.py $pathInWardenContainter $appName`
6. `python configureMysql.py $sqlFile` or 12Factor: `python executeScript.py $appName $dbInitScript`
