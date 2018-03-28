# Cloud Foundry Plugin

## Introduction
The Cloud Foundry Plugin transforms a CSAR to an Artifact, that can be deployed on the Cloud Foundry Platform. If the Application contains databases, there needs to be an environment variable with the name `database_host`.
Before the Transformation can start the Cloud Foundry Properties have to be set in the GUI:

- Username
- Api Url
- Organization
- Password
- Space

They are used to search for Services which can be used for the Deployment.
The plugin currently supports [the following NodesTypes.](https://github.com/StuPro-TOSCAna/TOSCAna/blob/581faba857848a1e6002bfbf40a29de79fac15b4/docs/dev/plugins/cloudFoundry/developer_guide/CloudFoundry_NodeTypes.md) For more details on the Transformation and created Scripts, see [Transformation](https://github.com/StuPro-TOSCAna/TOSCAna/blob/581faba857848a1e6002bfbf40a29de79fac15b4/docs/dev/plugins/cloudFoundry/developer_guide/transformation.md) and [Script Overview.](https://github.com/StuPro-TOSCAna/TOSCAna/blob/581faba857848a1e6002bfbf40a29de79fac15b4/docs/dev/plugins/cloudFoundry/developer_guide/Script-Overview.md)

## Artifact
The Artifact contains all necessary files which are needed to deploy an Application on Cloud Foundry. After you open the Artifact you can see the Log of the Transformation, the app folder(s) which contain your Application, the output folder which contains the files for Deployment. The output folder contains a `scripts folder` and the files `all_services.txt`, `manifest.yml`, `README.txt`. 

The Plugin tries to find free Services available on the Cloud Foundry Platform to provide support for your defined NodeTypes. They can be changed to paid plans, to see all available services you can open `all_services.txt` in the `/output/` folder.

For example if you want to change a Database plan for the Service cleardb (available on Pivotal Cloud Foundry), open `deploy_application.sh` in `/output/scripts/` and change `cf create-service cleardb spark my_db` from the free spark plan to boost, amp or shock: `cf create-service cleardb shock my_db`. For more information about the output folder see README.txt.

### Requirements for Deployment
Before you can use the Artifact to deploy, there are some requirements:
- Cloud Foundry is running on target infrastructure
- the CloudFoundry CLI is installed on the local machine
Linux Installation (other Systems: [Install CF CLI](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)):

```
wget -q -O - https://packages.cloudfoundry.org/debian/cli.cloudfoundry.org.key | sudo apt-key add -
echo "deb https://packages.cloudfoundry.org/debian stable main" | sudo tee /etc/apt/sources.list.d/cloudfoundry-cli.list
sudo apt-get update
sudo apt-get install cf-cli
```

- python 2.7 or newer is installed on the local machine

## Deployment
To deploy an application, log into Cloud Foundry with the Cloud Foundry CLI (more information [Here](https://docs.cloudfoundry.org/cf-cli/getting-started.html)).

```
cf login -a https://api.example.com -u username@example.com
```

- Upload the Artifact to the Destination folder and unzip it.

- Move into the /output/scripts/ folder and execute `deploy_applications.sh`:

```
chmod +x deploy_applications.sh
./deploy_applications.sh
```
