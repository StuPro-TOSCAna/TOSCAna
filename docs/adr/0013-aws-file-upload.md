# Use *** to upload files to Amazon S3

**User Story:**

AWS Cloudformation only uses a template YAML file for deployment, other files needed for deployment must be supplied by other means. To do this, AWS CloudFormation allows the usage of file URLs in the template.

As a plugin developer I want to upload files to Amazon S3 in order to use their URLs in the Cloudformation template.


## Considered Alternatives

* Interact directly with the AWS API
* Use the AWS CLI
* Use the AWS SDK for Java

## Decision Outcome

* Chosen Alternative: *[alternative 1]*
* *[justification. e.g., only alternative, which meets k.o. criterion decision driver | which resolves force force | ... | comes out best (see below)]*
* *[consequences. e.g., negative impact on quality attribute, follow-up decisions required, ...]* <!-- optional -->

## Pros and Cons of the Alternatives <!-- optional -->

### Interact directly with the AWS API
* `+` Can be built from the ground up to support our needs
* `-` The AWS SDK for Java fulfills this purpose already

### Use the AWS CLI

* `+` Target artifact is "self-contained". Deployment only requires 
* `+` Credentials are only handled by user himself
* `+` Transformation is purely model to model
* `-` More effort required by the user
* `-` Target artifact becomes more complex (includes files, scripts, and the template)

### Use the AWS SDK for Java

* `+` Minimal effort requred by the user
* `+` Target artifact is only the template
* `-` System must handle credentials to gain access to the users Amazon S3
* `-` Transformation moves more towards model to instance from model to model
