# Use the AWS CLI to upload files to Amazon S3

**User Story:**

AWS Cloudformation only uses a template YAML file for deployment, other files needed for deployment must be supplied by other means.
To do this, AWS CloudFormation allows the usage of file URLs in the template.

As a plugin developer, I want to upload files to Amazon S3 in order to use their URLs in the Cloudformation template.


## Considered Alternatives

* Interact directly with the AWS API
* Use scripts with the AWS CLI
* Use the AWS SDK for Java
* Use the AWS CLI directly

## Decision Outcome

* Chosen Alternative: Use scripts with the AWS CLI
* The main deciding factor for using the AWS CLI was that the target artifact would be self-contained.
This allows the transformation and deployment to be completely separate.
The user can start the transformation at one point and deploy the artifact at any other point in time.
* Additionally, this means that the target artifact is not dependent on the AWS account the person performing the transformation but can instead be deployed by anyone who has a valid AWS account and the AWS CLI setup correctly.
* As a consequence, the transformation process will require the creation of additional bash scripts to allow the successful file upload and subsequent deployment of the CloudFormation template.

## Pros and Cons of the Alternatives <!-- optional -->

### Interact directly with the AWS API

* `+` Can be built from the ground up to support our needs
* `-` The AWS SDK for Java fulfils this purpose already

### Use scripts with the AWS CLI

* `+` Target artifact is "self-contained".
Deployment only requires the files inside of the target artifact and no remotely hosted resources.
* `+` Target artifact is user-independent
* `+` Credentials are only handled by user himself
* `+` Transformation is pure model to model
* `-` More effort required by the user
* `-` Target artifact becomes more complex (includes files, scripts, and the template)

### Use the AWS SDK for Java

* `+` Minimal effort required by the user
* `+` Target artifact is only the template
* `-` System must handle credentials to gain access to the users Amazon S3
* `-` Transformation moves more towards model to instance from model to model

### Use the AWS CLI directly

* `+` Same benefits as using the AWS SDK for Java
* `-` Same disadvantages as using the AWS SDK for Java
* `-` Implementation is more complex than just using the AWS SDK for Java
