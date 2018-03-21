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

See the official [installation guide](https://docs.aws.amazon.com/cli/latest/userguide/installing.html) for more information.

#### Configure

You need a working AWS Account to use the CLI successfully.
Configure the AWS CLI to use your Account credentials.

Use the configure command
```bash
aws configure
```
to set up your CLI. You will be prompted for your account credentials.

See the official [configuration guide](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html).

## Deployment

This section shows how you deploy your application.

### Move into scripts folder

Use
```bash
cd output/scripts/
```
to change your working directory to the scripts folder.

### Make the deploy script executable

Use
```bash
chmod +x deploy.sh
```
to set the deploy script executable.
You only need to do this for the deploy script.

### Execute the deploy script

Use
```bash
./deploy.sh
```
to deploy your application. The script now automatically deploys your application on AWS. Enjoy!

### Execute the cleanup script (opt.)

If you want to remove your application you can use
```bash
chmod +x cleanup.sh
./cleanup.sh
```

## Artifact output

This section tries to explain, what the transformation artifact contains, and how the automatic deployment works.
Every file is located in the `output` folder.

### Template file

The `template.yaml` file is the cloudformation template. It is constructed from your model. It may depend on files which means you can't just deploy this template.
 

### Files folder

The `files` folder just contains your files. The files that were in the original TOSCA model and need to be copied to a AWS Resource.

### Scripts folder

#### Util folder

The util folder contains util scripts that are used by the `deploy.sh` and `cleanup.sh`.

#### Deploy script

The deploy script first checks whether the AWS CLI is installed.
If files need to be uploaded to any resource the optional `files-upload.sh` is executed.
After that the `create-stack.sh` script is executed.

#### Cleanup script

The cleanup script deletes a possible created AWS S3 Bucket and deletes the CloudFormation Stack.

#### Create-stack script

This script just deploys the `template.yaml` file using the AWS CLI.

#### File-upload script (opt.)

This script creates an AWS S3 Bucket and uploads various files to this bucket.

## TOSCAna
- [TOSCAna on GitHub](https://github.com/StuPro-TOSCAna/TOSCAna)
- [TOSCAna Documentation on ReadTheDocs](https://toscana.readthedocs.io/en/latest/)
