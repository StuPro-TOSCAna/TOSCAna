# Use AWS Command Line Interface for deployment

For deploying an AWS CloudFormation template it has to be uploaded to AWS and the deployment has to be started.

## Considered Alternatives

* [AWS Command Line Interface](https://aws.amazon.com/de/cli/)
* Manually with the AWS Management Console
* Using API calls

## Decision Outcome

* Chosen Alternative: AWS Command Line Interface
* Easiest and straight forward solution
* AWS Command Line Interface has to be installed locally

## Pros and Cons of the Alternatives <!-- optional -->

### *AWS Command Line Interface*

* `+` Easy to use
* `+` Has to be configured once and then can be used without credentials
* `-` Has to be installed and configured with credentials


### *Manually with the AWS Management Console*

* `+` Easy to use
* `-` User has to interact
* `-` CloudFormation(CFN) template cannot be deployed fully automated

### *Using API calls*

* `+` Can be used out of the box
* `-` complex solution
