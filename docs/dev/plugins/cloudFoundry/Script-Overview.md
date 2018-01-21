# Scripts overview
To transform without pushing anything we need to extract some logic out of the transformation and add it to deploy scripts.
We add all commands and execution of python scripts into the `deploy.sh`. This should be done while the transformation process.
Therefore we have to do following things:

## Create the Services needed for the App
to create a service, use: `cf create service`

## Push the App to Cloud Foundry
to push it use: `cf push myapp`

## Python readCredentials ["cleardb"]
read credentials out of the services of the app.
Set the credentials as environment variables of the container
Lamp Example: `cf set-env $appName database_user = $database_user`
Afterwards restage the application to ensure that the changes take effect.
Twelve Factor: only the URI is needed and have to be modeled. So the script just have to replace a environment variable which contains "URL" or something like that.

## Python replace paths in file
In some scripts could be paths which are not available on the warden container. So this paths has to be replaced with existing paths.
Twelve Factor: There are no paths which depends on the environment.

## Python configureMysql ["pathToSQLscript"]
connect to the mysql service and execute the sql script.
Needs a additional python mysql package
Lamp Example: `mysqlCursor.execute(dbInitCommand)`
Twelve Factor: there have to be a init database script. So we just have to execute this script on the container.

## Python execute commands
to execute scripts on the container it is needed to create a ssh connection to the container and execute the script. Unfortunately is the cf command `cf run-task ...` not suitable because it has to enabled from the CF instance provider.

## Order
1. `cf create service $servicename $serviceplan $serviceInstanceName`
2. `cf push $appName -f $pathToManifest`
3. `python readCredentials.py $appName $serviceName $serviceType`
4. `python replace.py $file $findString $replaceString`
5. `python executeScript.py $pathInWardenContainter $appName`
6. `python configureMysql.py $sqlFile` or 12Factor: `python executeScript.py $appName $dbInitScript`

## Summary
the AWS Cloudformation CLI needs python anyways so we could use it too. We think for "bigger" script implementation python is the best way to do it.
