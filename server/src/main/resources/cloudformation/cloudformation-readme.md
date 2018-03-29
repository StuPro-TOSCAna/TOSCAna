# TOSCAna - AWS CloudFormation Readme

Thank you for using the TOSCAna transformation tool.
You transformed your model to AmazonWebServices CloudFormation.
This readme will help you to handle the transformation artifact and deploy your application.

## Requirements

This section describes what you already have to set up before deploying your application.

### Bash

You need to have bash installed.

Supported platforms:
- Linux
- Windows (git bash)
- MacOs (might work, not tested)

### AWS CLI

The deployment script uses the AmazonWebServices Command Line Interface. 
The AWS CLI depends on Python. You need to have the CLI installed.

See [AWS CLI](https://aws.amazon.com/cli/) for further information.

#### Installation

If you have Python and `pip`, a package manager for Python, already installed, you can use
```bash
pip install awscli --upgrade --user
```

See the official [installation guide](https://docs.aws.amazon.com/cli/latest/userguide/installing.html) from AWS for more information.

#### Configure

You need a working AWS Account to use the CLI successfully.
Configure the AWS CLI to use your Account credentials.

Use the following configure command
```bash
aws configure
```
You will be prompted for your account credentials. Enter them and you are ready to go.

See the official [configuration guide](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html) from AWS for more information.

## Deployment

This section shows how you deploy your application. 
You should have already transformed your model as well as downloaded and unpacked the resulting artifact. 

### Move into scripts folder

Having unpacked the artifact in your current working directory, use
```bash
cd output/scripts/
```
to change your working directory to the scripts folder.

### Make the deploy script executable

You need to make the deploy script executable.
Use
```bash
chmod +x deploy.sh
```
to do so.
You only have to do this for the deploy script so far.

### Execute the deploy script

You are all set now.
Use
```bash
./deploy.sh
```
to deploy your application. The script now automatically deploys your application on AWS. Enjoy!

**Note:** If you set the option to use a KeyPair for your EC2 Instances to true, you need to do some more.
Either you set the environment variable `$KeyNameVar` to your preferred key name or you replace said `$KeyNameVar` string in the `create-stack.sh` with your key name.

### Execute the cleanup script (optional)

Executing the `cleanup.sh` script will delete your application. Use
```bash
chmod +x cleanup.sh
./cleanup.sh
```
to do so.

## Artifact output

This section tries to explain, what the transformation artifact contains, and how the automatic deployment works.
Every file, besides this, is located in the `output` folder.

### Template file

The `template.yaml` file is the CloudFormation template. It is constructed from your model.
Sadly in most cases it cannot be deployed on its own.
It may depend other files you need placed in the `files` folder. This means you can't just deploy this template but have to execute the deploy script.
 

### Files folder

The `files` folder contains all your files. All files that were in the original TOSCA model and are needed for any AWS Resource were copied here.

### Scripts folder

This folder mostly contains scripts that are used by the deploy script. And obviously the deploy script itself.

#### Util folder

The util folder contains util scripts that are used by the `deploy.sh` and `cleanup.sh` scripts. 
For example a script that uploads a file to a S3 Bucket or creates such S3 Bucket.

#### Deploy script

The deploy script is together with the `template.yaml` the main part of your artifact. 

It first checks whether the AWS CLI is installed.
If files need to be uploaded for any resource the optional `files-upload.sh` is executed.
After that the `create-stack.sh` script, which actually deploys your `template.yaml`, is executed.

#### Cleanup script

If a S3 Bucket was created this script will delete all files in it and the bucket itself.
It also shuts down and deletes the CloudFormation Stack.

#### Create-stack script

This script just deploys the `template.yaml` file using the AWS CLI. It is used by the deploy script.

#### File-upload script (optional)

This script creates an AWS S3 Bucket and uploads various files to this bucket. It is used by the deploy script.

## TOSCAna
- [TOSCAna on GitHub](https://github.com/StuPro-TOSCAna/TOSCAna)
- [TOSCAna Documentation on ReadTheDocs](https://toscana.readthedocs.io/en/latest/)
