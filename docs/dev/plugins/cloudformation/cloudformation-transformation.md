# AWS CloudFormation - Transformation of single NodeTypes

## Summary table of NodeTypes

| NodeType | Short summary |
| --- | --- |
| Apache | no specific resource is created |
| Compute | an EC2 Resource and belonging SecurityGroup are created |
| Database | no specific resource is created |
| Dbms | no specific resource is created |
| MysqlDatabase | a RDS with a mysql engine is created |
| MysqlDbms | no specific resource is created |
| WebApplication | no specific resource is created |
| Nodejs | no specific resource is created |
| JavaRuntime | no specific resource is created |
| JavaApplication | a Beanstalk Application and Environment are created |

## Transformation implementation details

These transformations take place in the TransformationModelNodeVisitor: [GitHub link](https://github.com/StuPro-TOSCAna/TOSCAna/blob/master/server/src/main/java/org/opentosca/toscana/plugins/cloudformation/visitor/TransformModelNodeVisitor.java)

### Apache
1. cfnInit apt apache2 on underlying compute/ec2
2. handleConfigure/handleStart --> scripts and files get copied and executed on underlying compute/Ec2
3. global environment variables (start lifecycle of webapplication that is hosted on this apache) will be added to /etc/apache/envvars 
4. if modifications took place a "service apache2 restart" command is added

### Compute
Compute nodes correlate with a ec2 because its just an vm.
Following steps are taken in the transform step:
1. SecurityGroup is created
2. optional Enpoint ports are opened on this security group puplically
3. OsCapability and ComputeCapability are mapped to properties of EC2 (like what imageID(ami-...) to take what instance type to take(t2.micro))
4. CFNinit is created but not yet added so it can be manipulated (cfninit is used to call scripts, commands, add files or install packages)
5. EC2 linked with securitygroup is created
6. disk size is mapped from tosca --> block device mapping
7. if keypair is activated --> add keyname to instance + open ingress on security group to port 22(SSH)
at buildtime: cfninit gets put on ec2 + userdata that executes cfninit + authentication + instancepofile gets added(permission for s3 bucket)

### Database
