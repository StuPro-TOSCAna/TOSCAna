package org.opentosca.toscana.plugins.cloudformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.plugins.cloudformation.util.FileToBeUploaded;

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
import com.scaleset.cfbuilder.iam.Role;

import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.OUTPUT_DIR;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.util.AuthenticationUtils.INSTANCE_PROFILE;
import static org.opentosca.toscana.plugins.cloudformation.util.AuthenticationUtils.getS3Authentication;
import static org.opentosca.toscana.plugins.cloudformation.util.AuthenticationUtils.getS3InstanceProfile;
import static org.opentosca.toscana.plugins.cloudformation.util.AuthenticationUtils.getS3InstanceRole;
import static org.opentosca.toscana.plugins.cloudformation.util.AuthenticationUtils.getS3Policy;
import static org.opentosca.toscana.plugins.cloudformation.util.StackUtils.getRandomBucketName;
import static org.opentosca.toscana.plugins.cloudformation.util.StackUtils.getRandomStackName;
import static org.opentosca.toscana.plugins.cloudformation.util.StackUtils.getUserDataFn;

public class CloudFormationModule extends Module {

    public static final String CONFIG_SETS = "LifecycleOperations";
    public static final String CONFIG_CREATE = "Create";
    public static final String CONFIG_CONFIGURE = "Configure";
    public static final String CONFIG_START = "Start";
    public static final String SECURITY_GROUP = "SecurityGroup";
    public static final String ABSOLUTE_FILE_PATH = "/opt/";
    public static final String URL_HTTP = "http://";
    public static final String URL_S3_AMAZONAWS = ".s3.amazonaws.com";
    public static final String FILEPATH_TARGET = OUTPUT_DIR + "files/";
    public static final String MODE_500 = "000500";
    public static final String MODE_644 = "000644";
    public static final String OWNER_GROUP_ROOT = "root";
    public static final String KEYNAME = "KeyName";
    public static final String FILEPATH_NODEJS_CREATE = "create-nodejs.sh";

    // KeyName is a default input value
    private static final String KEYNAME_DESCRIPTION = "Name of an existing EC2 KeyPair to enable SSH access to the " +
        "instances";
    private static final String KEYNAME_TYPE = "AWS::EC2::KeyPair::KeyName";
    private static final String KEYNAME_CONSTRAINT_DESCRIPTION = "must be the name of an existing EC2 KeyPair.";

    private String awsRegion;
    private AWSCredentials awsCredentials;
    private Object keyNameVar;
    private Map<String, CFNInit> cfnInitMap;
    private Set<String> computeToEc2;
    private Map<String, Fn> fnSaver;
    private Set<String> authenticationSet;
    private List<FileToBeUploaded> filesToBeUploaded;
    private PluginFileAccess fileAccess;
    private String bucketName;
    private String stackName;
    private boolean keyPair;
    private Map<String, Map<String, String>> environmentMap;

    /**
     Create a Module which uses the cloudformation-builder to build an AWS CloudFormation template

     @param fileAccess fileAccess to append the content of files to the template
     */
    public CloudFormationModule(PluginFileAccess fileAccess, String awsRegion, AWSCredentials awsCredentials) {
        this.id("").template(new Template());
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
        this.environmentMap = new HashMap<>();
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
     Get the CFNInit belonging to the given resource.

     @param resource String id of a resource
     */
    public CFNInit getCFNInit(String resource) {
        return this.cfnInitMap.get(resource);
    }

    /**
     Mark a compute node to be transformed to an EC2 instance.
     */
    public void addComputeToEc2(Compute compute) {
        computeToEc2.add(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Unmark a compute node from being transformed to an EC2 instance.
     */
    public void removeComputeToEc2(Compute compute) {
        computeToEc2.remove(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Check if this compute node is marked to be transformed to an EC2 instance.
     */
    public boolean checkComputeToEc2(Compute compute) {
        return computeToEc2.contains(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Put an Fn with its string representation into a map
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

    public List<FileToBeUploaded> getFilesToBeUploaded() {
        return filesToBeUploaded;
    }

    public void addFileToBeUploaded(FileToBeUploaded filePath) {
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

    public Object getKeyNameVar() {
        return this.keyNameVar;
    }

    public String getAWSRegion() {
        return this.awsRegion;
    }

    public AWSCredentials getAwsCredentials() {
        return this.awsCredentials;
    }

    public Template getTemplate() {
        return this.template;
    }

    public PluginFileAccess getFileAccess() {
        return fileAccess;
    }

    /**
     Returns the parameters of the template belonging to this module.

     @return map with the parameters of the template
     */
    public Map<String, Parameter> getParameters() {
        return this.template.getParameters();
    }

    public Map<String, Map<String, String>> getEnvironmentMap() {
        return environmentMap;
    }

    public void putEnvironmentMap(String instanceName, String key, String value) {
        this.environmentMap.computeIfAbsent(instanceName, k -> new HashMap<>());
        this.environmentMap.get(instanceName).put(key, value);
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

    public boolean hasKeyPair() {
        return keyPair;
    }

    public void setKeyPair(boolean keyPair) {
        this.keyPair = keyPair;
    }

    public CloudFormationModule withKeyPair(boolean keyPair) {
        this.keyPair = keyPair;
        return this;
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
                        .userData(new UserData(getUserDataFn(pair.getKey(), CONFIG_SETS, this)));
                }
            }
        }
        if (!filesToBeUploaded.isEmpty()) {
            Role instanceRole = getS3InstanceRole(this);
            getS3Policy(this).roles(instanceRole);
            getS3InstanceProfile(this).roles(instanceRole);
            Authentication s3authentication = getS3Authentication(bucketName);
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
        if (this.hasKeyPair()) {
            strParam(KEYNAME).type(KEYNAME_TYPE).description(KEYNAME_DESCRIPTION).constraintDescription
                (KEYNAME_CONSTRAINT_DESCRIPTION);
        }
    }
}
