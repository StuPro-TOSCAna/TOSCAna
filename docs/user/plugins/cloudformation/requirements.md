# Requirements

This section describes what you already have to set up before deploying your application.

## Bash

You need to have bash installed.

Supported platforms:
- Linux
- Windows (git bash)
- MacOs (might work, not tested)

## AWS CLI

The deployment script uses the AmazonWebServices Command Line Interface. 
The AWS CLI depends on Python. You need to have the CLI installed.

See [AWS CLI](https://aws.amazon.com/cli/) for further information.

### Installation

If you have Python and `pip`, a package manager for Python, already installed, you can use
```bash
pip install awscli --upgrade --user
```

See the official [installation guide](https://docs.aws.amazon.com/cli/latest/userguide/installing.html) from AWS for more information.

### Configuration

You need a working AWS Account to use the CLI successfully.
Configure the AWS CLI to use your Account credentials.

Use the following configure command
```bash
aws configure
```
You will be prompted for your account credentials. Enter them and you are ready to go.

See the official [configuration guide](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html) from AWS for more information.
