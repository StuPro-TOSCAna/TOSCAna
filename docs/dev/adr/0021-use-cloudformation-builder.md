# Use Cloudformation Builder to build CloudFormation templates

**User Story:**

As a plugin developer, to be able to create CloudFormation templates with Java programmatically.


## Considered Alternatives

* Use the [AWS SDK for Java](https://github.com/aws/aws-sdk-java)
* Use the [CloudFormation Builder](https://github.com/scaleset/cloudformation-builder)
* Build our own Template Creator

## Decision Outcome

* Chosen Alternative: Use the CloudFormation Builder
* Instead of building our own template creator from scratch, we decided to use an existing solution, the CloudFormation Builder.
Because the project was never completed, we decided fork the project which we will maintain during development. Our fork is available [here](https://github.com/StuPro-TOSCAna/cloudformation-builder).
* As a consequence, we need to update the CloudFormation Builder during the development of our plugin in order to allow the transformation.

## Pros and Cons of the Alternatives <!-- optional -->

### Use the AWS SDK for Java

* `+` Well tested and supports the latest CloudFormation specification
* `+` No effort required to build our own model
* `-` Used to manipulate CloudFormation stacks, not creating templates

### Use the CloudFormation Builder

* `+` Less effort required compared to building our own
* `-` Doesn't support the full CloudFormation specification
* `-` Has to be adjusted to fit our needs

### Build our own Template Creator

* `+` Can be built from the ground up to suit our needs
* `-` Requires a lot of additional effort to get the same result
