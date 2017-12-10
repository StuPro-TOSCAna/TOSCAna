# AWS CloudFormation Plugin LAMP-Demo (Windows)
## Setup
1. Start CMD.
2. Make sure you are in the `demo/` folder containing the JAR snapshot `cli-1.0-SNAPSHOT-jar-with-dependencies.jar` and the CSAR `lamp-demo-aws.csar`.
3. Make sure the TOSCAna server is running.

You are now ready to start the TOSCAna CLI and run the AWS CloudFormation Plugin LAMP-Demo.

## Demo
1. Run the TOSCAna CLI with
`doskey toscana=java -jar cli-1.0-SNAPSHOT-jar-with-dependencies.jar $*`

2. Upload the CSAR With
`toscana csar upload -f=lamp-demo-aws.csar`

3. Start the transformation to AWS CloudFormation with
`toscana transformation start -c=lamp-demo-aws -p=cloudformation`

4. Download the target artifact with
`toscana transformation download -c=lamp-demo-aws -p=cloudformation -o=target-artifact.zip`

5. Unpack the `target-artifact.zip` and deploy the `output/template.yaml` on AWS.
