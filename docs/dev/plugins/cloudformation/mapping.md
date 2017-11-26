# AWS CloudFormation

## Mapping

Mapping (non-) normative Types of TOSCA to AWS Resources.

| (non-) normative Type | AWS Resource | Comment |
| --- | --- | --- |
| Compute | [AWS EC2](https://aws.amazon.com/de/ec2/) | A AWS-SecurityGroup has to be created |
| SoftwareComponent | --- | Add scripts to the EC2 that is launched |
| WebServer | --- | see SoftwareComponent, but also [AWS Elastic Beanstalk](https://aws.amazon.com/de/elasticbeanstalk/) could be applied |
| Nodejs | --- | see WebServer, Beanstalk is a valid option |
| Apache | --- | see WebServer |
| DBMS | --- | see SoftwareComponent |
| MYSQL | --- | see SoftwareComponent |
| ContainerRuntime | [AWS EC2 Container Service](https://aws.amazon.com/de/ecs/) | Can't say much about containers here because I failed at launching ECS. A alternative is launching everthing on a EC2 |
| ObjectStorage | [Amazon S3](https://aws.amazon.com/de/s3/) |  |
| BlockStorage | -- | Block storage isn't created on its own but is defined by the instance type chosen for the EC2. So this Node only changes the properties of the EC2 |
| Database | [AWS RDS](https://aws.amazon.com/de/rds/) | The property `Engine` needs to be set depending on the underlying DBMS Node (e.g MySQL). Also an AWS-SecurityGroup is needed |
| ContainerApplication | --- | has to be run on a ContainerRuntime |
| DockerApplication | --- | see ContainerRuntime |
| WebApplication | --- | Add scripts to the EC2 that is launched |
| WordPress | --- | see WebApplication |
| LoadBalancer | [Elastic Load Balancing](https://aws.amazon.com/de/elasticloadbalancing/) | Things like an AutoScalingGroup, an TargetGroup, etc. need to be kept in mind |
