############# TOSCAna Cloud Foundry Readme #############
Thank you for using the TOSCAna transformation tool!
This README should help you to handle the transformation output properly.

At first we want to give you a short overview about the output artifact.
There are two types of main folders:
  1. the application folders which contain all files of the applications
  2. the output folder which contains all scripts and also the manifest

---Application folders---
We created for each application a separate folder. The names are:
application_names
Each application will be pushed to a own container on the Cloud Foundry instance.
For php applications we added a .bp-config folder which contains some buildpack additions.
In these folders are all files which are stated in your Tosca-Template.
If an application is connected to a service, we added the service files to the application folder as well.

---Output folder---
There you see two files:
  - the manifest which contains all metadata for the applications. The manifest is mandatory for deployment.
  - a text file which contains all possible services with their plans.

Also there is the scripts folder. Inside are all scripts which are necessary for the deployment.
You could run the deploy_application(s).sh script and your application will be deployed.
The script needs the Cloud Foundry CLI and therefore a valid connection. Otherwise the script will break.

---Deployment---
Go to /output/scripts and run the deploy_application(s).sh scripts to deploy your applications.
If you application uses a service like a database, TOSCAna choose a suitable service for you. We took automatically a free-plan
which may not suitable for you. But you are able to change it manually.
  - Just look for a suitable service / plan in the /output/all_services.txt
  - change in the deploy_application(s).sh all occurrence of the old service / plan and change it to the new one.

---Additional information---
Please do not move or rename files or folders!
If you are pushing a web application you may add the subfolder to your URL.
