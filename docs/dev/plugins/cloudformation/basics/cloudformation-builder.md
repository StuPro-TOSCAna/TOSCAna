# CloudFormation Builder

While CloudFormation templates can be created by hand, in our project we needed a way to programmatically create CloudFormation templates in Java.

The [Cloudformation Builder](https://github.com/StuPro-TOSCAna/cloudformation-builder) provides us with the ability to create a java model of a CloudFormation template and the subsequent generation of a valid YAML template corresponding to that java model. It is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

Since CloudFormation Builder project was abandoned in 2015 without full support for the CloudFormation specification, we created our own fork of the repository where we extended the existing functionality to support the parts of the specification we needed in order to facilitate our transformation process. The full source code can be found [here](https://github.com/StuPro-TOSCAna/cloudformation-builder).

## Usage

The following passage is taken from the readme of the CloudFormation Builder available in [its repo](https://github.com/StuPro-TOSCAna/cloudformation-builder).

CloudFormation templates are built within a so-called `Module`. This module gets filled with all the CloudFormation resources needed to build the template.

The following is a quick example on how a CloudFormation template is built with the CloudFormation Builder:

```java
class Ec2withEbsModule extends Module {
    public void build() {
        this.template.setDescription("Ec2 block device mapping");

        EC2EBSBlockDevice ec2EBSBlockDeviceA = new EC2EBSBlockDevice()
                .volumeType("io1")
                .iops(200)
                .deleteOnTermination(false)
                .volumeSize("20");
        EC2BlockDeviceMapping ec2BlockDeviceMappingA = new EC2BlockDeviceMapping()
                .deviceName("/dev/sdm")
                .ebs(ec2EBSBlockDeviceA);

        EC2BlockDeviceMapping ec2BlockDeviceMappingB = new EC2BlockDeviceMapping()
                .deviceName("/dev/sdk")
                .noDevice(false);

        resource(Instance.class, "MyEC2Instance")
                .imageId("ami-79fd7eee")
                .keyName("testkey")
                .blockDeviceMappings(ec2BlockDeviceMappingA, ec2BlockDeviceMappingB);
    }
}
```

> **Note**: The example is taken from the [`InstanceTest`](/src/test/java/com/scaleset/cfbuilder/InstanceTest.java). See `/src/test/java/com/scaleset/cfbuilder/` for more tests containing examples that you can use.

This `Ec2withEbsModule` results in the following CloudFormation template:

```yaml
AWSTemplateFormatVersion: "2010-09-09"
Description: "Ec2 block device mapping"
Resources:
  MyEC2Instance:
    Type: "AWS::EC2::Instance"
    Properties:
      ImageId: "ami-79fd7eee"
      KeyName: "testkey"
      BlockDeviceMappings:
      - DeviceName: "/dev/sdm"
        Ebs:
          DeleteOnTermination: false
          Iops: 200
          VolumeSize: "20"
          VolumeType: "io1"
      - DeviceName: "/dev/sdk"
        NoDevice: false
```