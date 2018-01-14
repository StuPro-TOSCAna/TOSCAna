# Use the AWS SDK for Java to upload files to Amazon S3

**User Story:**

AWS Cloudformation only uses a template YAML file for deployment, other files needed for deployment must be supplied by other means. To do this, AWS CloudFormation allows the usage of file URLs in the template.

As a plugin developer, I want to upload files to Amazon S3 in order to use their URLs in the Cloudformation template.


## Considered Alternatives

* Interact directly with the AWS API
* Use scripts with the AWS CLI
* Use the AWS SDK for Java
* Use the AWS CLI directly

## Decision Outcome

* Chosen Alternative: Use the AWS SDK for Java
* The main deciding factor for using the AWS Java SDK was the amount of effort required by the user. With this option, the user simply has to enter his AWS credentials and the complete transformation can follow without any user input.
* Another aspect that went into consideration was whether the target artifact has to be self-contained to be deployable regardless of any files stored remotely. This was assessed to be negligible since the S3Bucket used to store the files should not become inaccessible until the time of deployment and if that actually was the case the user can just redo the transformation process in order to create a new S3Bucket.
* As a consequence, this decision will require the user to input his AWS credentials in order to start a transformation to AWS Cloudformation.
  * In order to mitigate the negative influences of this decision, the option of supplying file content directly in the AWS template will also be offered. This option will only work with systems that only require non-binary files for deployment.

## Pros and Cons of the Alternatives <!-- optional -->

### Interact directly with the AWS API
* `+` Can be built from the ground up to support our needs
* `-` The AWS SDK for Java fulfils this purpose already

### Use scripts with the AWS CLI

* `+` Target artifact is "self-contained". Deployment only requires the files inside of the target artifact and no remotely hosted resources.
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
