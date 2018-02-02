package org.opentosca.toscana.plugins.cloudformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.model.node.Compute;

import com.amazonaws.auth.AWSCredentials;
import com.scaleset.cfbuilder.cloudformation.Authentication;
import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Parameter;
import com.scaleset.cfbuilder.core.Resource;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.UserData;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.iam.InstanceProfile;
import com.scaleset.cfbuilder.iam.Policy;
import com.scaleset.cfbuilder.iam.PolicyDocument;
import com.scaleset.cfbuilder.iam.Principal;
import com.scaleset.cfbuilder.iam.Role;
import com.scaleset.cfbuilder.iam.Statement;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;

public class CloudFormationModule extends Module {

    public static final String CONFIG_SETS = "LifecycleOperations";
    public static final String CONFIG_CREATE = "Create";
    public static final String CONFIG_CONFIGURE = "Configure";
    public static final String CONFIG_START = "Start";
    public static final String SECURITY_GROUP = "SecurityGroup";
    public static final String ABSOLUTE_FILE_PATH = "/opt/";
    public static final String URL_HTTP = "http://";
    public static final String URL_S3_AMAZONAWS = ".s3.amazonaws.com";
    public static final String FILEPATH_TARGET = "output/files/";
    public static final String MODE_500 = "000500";
    public static final String MODE_644 = "000644";
    public static final String OWNER_GROUP_ROOT = "root";
    public static final String KEYNAME = "KeyName";

    // KeyName is a default input value
    private static final String KEYNAME_DESCRIPTION = "Name of an existing EC2 KeyPair to enable SSH access to the " +
        "instances";
    private static final String KEYNAME_TYPE = "AWS::EC2::KeyPair::KeyName";
    private static final String KEYNAME_CONSTRAINT_DESCRIPTION = "must be the name of an existing EC2 KeyPair.";
    private static final String USERDATA_NAME = "Join";
    private static final String USERDATA_DELIMITER = "";
    private static final String INSTANCE_PROFILE = "InstanceProfile";
    private static final String INSTANCE_ROLE = "InstanceRole";
    private static final String[] USERDATA_CONSTANT_PARAMS = {
        "#!/bin/bash -xe\n",
        "mkdir -p /tmp/aws-cfn-bootstrap-latest\n",
        "curl https://s3.amazonaws.com/cloudformation-examples/aws-cfn-bootstrap-latest.tar.gz | tar xz -C " +
            "/tmp/aws-cfn-bootstrap-latest --strip-components 1\n",
        "apt-get update\n",
        "DEBIAN_FRONTEND=noninteractive apt-get upgrade -yq\n",
        "apt-get -y install python-setuptools\n",
        "easy_install /tmp/aws-cfn-bootstrap-latest\n",
        "cp /tmp/aws-cfn-bootstrap-latest/init/ubuntu/cfn-hup /etc/init.d/cfn-hup\n",
        "chmod 755 /etc/init.d/cfn-hup\n",
        "update-rc.d cfn-hup defaults\n",
        "# Install the files and packages from the metadata\n",
        "/usr/local/bin/cfn-init -v ",
        "         --stack "};
    private static final String ARN_AWS_S3 = "arn:aws:s3:::";
    private static final String EC2_AMAZONAWS_COM = "ec2.amazonaws.com";

    private String awsRegion;
    private AWSCredentials awsCredentials;
    private Object keyNameVar;
    private Map<String, CFNInit> cfnInitMap;
    private Set<String> computeToEc2;
    private Map<String, Fn> fnSaver;
    private Set<String> authenticationSet;
    private List<String> filesToBeUploaded;
    private PluginFileAccess fileAccess;
    private String bucketName;
    private String stackName;

    /**
     Create a Module which uses the cloudformation-builder to build an AWS CloudFormation template

     @param fileAccess fileAccess to append the content of files to the template
     */
    public CloudFormationModule(PluginFileAccess fileAccess, String awsRegion, AWSCredentials awsCredentials) {
        this.id("").template(new Template());
        strParam(KEYNAME).type(KEYNAME_TYPE).description(KEYNAME_DESCRIPTION).constraintDescription
            (KEYNAME_CONSTRAINT_DESCRIPTION);
        this.keyNameVar = template.ref(KEYNAME);
        this.cfnInitMap = new HashMap<>();
        this.computeToEc2 = new HashSet<>();
        this.fnSaver = new HashMap<>();
        this.authenticationSet = new HashSet<>();
        this.filesToBeUploaded = new ArrayList<>();
        this.fileAccess = fileAccess;
        this.bucketName = getRandomBucketName();
        this.stackName = getRandomStackName();
        this.awsRegion = awsRegion;
        this.awsCredentials = awsCredentials;
    }

    /**
     Put a CFNInit into a map which will be added to the resource at build time

     @param resource resource to add CFNInit to
     @param init     CNFInit to add
     */
    public void putCFNInit(String resource, CFNInit init) {
        cfnInitMap.put(resource, init);
    }

    /**
     Get the CFNInit which belongs to a specific resource

     @param resource String id of a resource
     */
    public CFNInit getCFNInit(String resource) {
        return this.cfnInitMap.get(resource);
    }

    /**
     Mark a compute node to be transformed to a ec2
     */
    public void addComputeToEc2(Compute compute) {
        computeToEc2.add(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Mark a compute node to be transformed to a ec2
     */
    public void removeComputeToEc2(Compute compute) {
        computeToEc2.remove(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Check if this compute is marked to be transformed to ec2
     */
    public boolean checkComputeToEc2(Compute compute) {
        return computeToEc2.contains(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Put a Fn with its string representation into a map
     */
    public void putFn(String key, Fn value) {
        this.fnSaver.put(key, value);
    }

    /**
     Check if the key is also saved as a Fn object
     */
    public boolean checkFn(String key) {
        return this.fnSaver.containsKey(key);
    }

    /**
     Get the Fn for this key
     */
    public Fn getFn(String key) {
        return this.fnSaver.get(key);
    }

    public List<String> getFilesToBeUploaded() {
        return filesToBeUploaded;
    }

    public void putFileToBeUploaded(String filePath) {
        this.filesToBeUploaded.add(filePath);
    }

    public Set<String> getAuthenticationSet() {
        return authenticationSet;
    }

    public void putAuthentication(String instanceName) {
        authenticationSet.add(instanceName);
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getStackName() {
        return stackName;
    }

    /**
     Get a ref to the KeyName of this template
     */
    public Object getKeyNameVar() {
        return this.keyNameVar;
    }

    /**
     Get the awsRegion set for this Module
     */
    public String getAWSRegion() {
        return this.awsRegion;
    }

    /**
     Get the awsCredentials for this Module
     */
    public AWSCredentials getAwsCredentials() {
        return this.awsCredentials;
    }

    private Fn getUserDataFn(String resource, String configsets) {
        // Initialise params that need refs
        Object[] userdataRefParams = {
            template.ref("AWS::StackName"),
            "         --resource " + resource + " ",
            "         --configsets " + configsets + " ",
            "         --region ",
            template.ref("AWS::Region"),
            "\n",
            "# Signal the status from cfn-init\n",
            "/usr/local/bin/cfn-signal -e $? ",
            "         --stack ",
            template.ref("AWS::StackName"),
            "         --resource " + resource + " ",
            "         --region ",
            template.ref("AWS::Region"),
            "\n"};

        // Combine constant params with ref params
        List<Object> params = new ArrayList<>();
        Collections.addAll(params, USERDATA_CONSTANT_PARAMS);
        Collections.addAll(params, userdataRefParams);

        return Fn.fnDelimiter(USERDATA_NAME, USERDATA_DELIMITER, params.toArray());
    }

    @Override
    public String toString() {
        try {
            this.build();
            return this.template.toString(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     Returns the paramaters of the template belonging to this module.

     @return map with the parameters of the template
     */
    public Map<String, Parameter> getParameters() {
        return this.template.getParameters();
    }

    /**
     Get the fileAccess of this module
     */
    public PluginFileAccess getFileAccess() {
        return fileAccess;
    }

    /**
     Returns a random DNS-compliant bucket name.

     @return random bucket name
     */
    private String getRandomBucketName() {
        return "toscana-bucket-" + UUID.randomUUID();
    }

    /**
     Returns a random DNS-compliant stack name.

     @return random stack name
     */
    private String getRandomStackName() {
        return "toscana-stack-" + UUID.randomUUID();
    }

    /**
     Returns the <tt>Authentication<tt> to access S3 for this module.

     @return authentication for S3
     */
    private Authentication getS3Authentication() {
        return new Authentication("S3Creds")
            .addBucket(bucketName)
            .roleName(ref(INSTANCE_ROLE))
            .type("S3");
    }

    /**
     Returns the <tt>Policy<tt> to access S3 for this module.
     Note: Roles must still be set.

     @return policy to access S3
     */
    private Policy getS3Policy() {
        Statement statement = new Statement().addAction("s3:GetObject").effect("Allow").addResource(ARN_AWS_S3 + bucketName + "/*");
        PolicyDocument policyDocument = new PolicyDocument().addStatement(statement);
        return resource(Policy.class, "RolePolicies")
            .policyName("S3Download")
            .policyDocument(policyDocument);
    }

    /**
     Returns the <tt>Role<tt> to access S3 for this module.

     @return Role to access S3
     */
    private Role getS3InstanceRole() {
        List<String> resourceList = new ArrayList<>();
        resourceList.add(EC2_AMAZONAWS_COM);
        Principal principal = new Principal().principal("Service", resourceList);
        Statement statement = new Statement().addAction("sts:AssumeRole").effect("Allow").principal(principal);
        PolicyDocument policyDocument = new PolicyDocument().addStatement(statement);
        return resource(Role.class, INSTANCE_ROLE)
            .path("/")
            .assumeRolePolicyDocument(policyDocument);
    }

    /**
     Returns the <tt>Instanceprofile<tt> to access S3 for this module.
     Note: Roles must still be set.

     @return instanceProfile to access S3
     */
    private InstanceProfile getS3InstanceProfile() {
        return resource(InstanceProfile.class, INSTANCE_PROFILE)
            .path("/");
    }

    /**
     Build the template
     1. Add CFNInit to corresponding instance resource
     2. Check if EC2 instances need access to S3. If yes, then
     2a. Add necessary IAM resources to the module
     2b. Add <tt>Authentication<tt> and <tt>IamInstanceProfile<tt> to corresponding instance resource
     */
    @Override
    public void build() {
        for (Map.Entry<String, CFNInit> pair : cfnInitMap.entrySet()) {
            Resource res = this.getResource(pair.getKey());
            if (res instanceof Instance) {
                Instance instance = (Instance) res;
                if (!pair.getValue().getConfigs().isEmpty()) {
                    instance
                        .addCFNInit(pair.getValue())
                        .userData(new UserData(getUserDataFn(pair.getKey(), CONFIG_SETS)));
                }
            }
        }
        if (!filesToBeUploaded.isEmpty()) {
            Role instanceRole = getS3InstanceRole();
            getS3Policy().roles(instanceRole);
            getS3InstanceProfile().roles(instanceRole);
            Authentication s3authentication = getS3Authentication();
            for (String instanceName : authenticationSet) {
                Resource res = this.getResource(instanceName);
                if (res instanceof Instance) {
                    Instance instance = (Instance) res;
                    instance
                        .authentication(s3authentication)
                        .iamInstanceProfile(ref(INSTANCE_PROFILE));
                }
            }
        }
    }
}
