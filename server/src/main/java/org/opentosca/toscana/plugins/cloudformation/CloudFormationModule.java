package org.opentosca.toscana.plugins.cloudformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.plugins.cloudformation.util.FileUpload;
import org.opentosca.toscana.plugins.cloudformation.util.StackUtils;

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

/**
 CloudFormationModule is an extension of the {@link Module} class. It represents an artifact of CloudFormation. It
 contains all AWS Resources and their properties added to this module but also holds information about files that need to
 be uploaded and environment variables that need to be pushed.

 @see <a href="https://github.com/StuPro-TOSCAna/cloudformation-builder">Github page of the cloudformation-builder</a>
 @see <a href="https://aws.amazon.com/cloudformation/">CloudFormation</a>
 @see <a href="https://aws.amazon.com/s3/">S3 Storage</a> */
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
    public static final String FILEPATH_NODEJS_CREATE = "create-nodejs.sh";

    // KeyName is a default input value
    private static final String KEYNAME = "KeyName";
    private static final String KEYNAME_DESCRIPTION = "Name of an existing EC2 KeyPair to enable SSH access to the " +
        "instances";
    private static final String KEYNAME_TYPE = "AWS::EC2::KeyPair::KeyName";
    private static final String KEYNAME_CONSTRAINT_DESCRIPTION = "must be the name of an existing EC2 KeyPair.";

    /**
     The {@link PluginFileAccess} where all files and the template will be stored.

     @see #getFileAccess()
     */
    private PluginFileAccess fileAccess;
    /**
     The random stack name.

     @see StackUtils#getRandomStackName()
     */
    private String stackName;
    /**
     The random bucket name.

     @see StackUtils#getRandomBucketName()
     */
    private String bucketName;
    /**
     The string representation of the AWS Region passed as input.

     @see #getAWSRegion()
     */
    private String awsRegion;
    /**
     The AWS Credentials passed as inputs.

     @see #getAwsCredentials()
     */
    private AWSCredentials awsCredentials;

    /**
     Reference to the default set input KeyName.

     @see #getKeyNameVar()
     @see #hasKeyPair()
     @see #setKeyPair(boolean)
     */
    private Object keyNameVar;
    /**
     Marks whether this CloudFormation has a key pair.

     @see #hasKeyPair()
     @see #setKeyPair(boolean)
     */
    private boolean keyPair;

    /**
     A {@link Set} of names of {@link Compute} nodes that should be transformed to EC2 Instances.

     @see #addComputeToEc2(Compute)
     @see #removeComputeToEc2(Compute)
     @see #checkComputeToEc2(Compute)
     */
    private Set<String> computeToEc2;
    /**
     A {@link Map} of resource names and a {@link CFNInit} that will be be added at build time.

     @see #putCFNInit(String, CFNInit)
     @see #getCFNInit(String)
     */
    private Map<String, CFNInit> cfnInitMap;
    /**
     A {@link Map} of string representations and their corresponding {@link Fn}.

     @see #putFn(String, Fn)
     @see #checkFn(String)
     @see #getFn(String)
     */
    private Map<String, Fn> fnSaver;
    /**
     A {@link List} of all file uploads.

     @see #getFileUploadList()
     @see #addFileUpload(FileUpload)
     */
    private List<FileUpload> fileUploadList;
    /**
     A {@link Set} of instance names that need authentication to the S3 Bucket.

     @see #putAuthentication(String)
     @see #getAuthenticationSet()
     */
    private Set<String> authenticationSet;
    /**
     A {@link Map} of instance names and their environment variables (also a {@link Map}.

     @see #putEnvironmentMap(String, String, String)
     @see #getEnvironmentMap()
     */
    private Map<String, Map<String, String>> environmentMap;

    /**
     Sets up the Module which uses the cloudformation-builder to build an AWS CloudFormation template

     @param fileAccess     fileAccess to append the content of files to the template
     @param awsRegion      the region the template will be deployed in
     @param awsCredentials the AWSCredentials for connecting with the AWS API
     */
    public CloudFormationModule(PluginFileAccess fileAccess, String awsRegion, AWSCredentials awsCredentials) {
        this.id("").template(new Template());
        this.fileAccess = fileAccess;
        this.stackName = getRandomStackName();
        this.bucketName = getRandomBucketName();
        this.awsRegion = awsRegion;
        this.awsCredentials = awsCredentials;
        this.keyNameVar = template.ref(KEYNAME);
        this.computeToEc2 = new HashSet<>();
        this.cfnInitMap = new HashMap<>();
        this.fnSaver = new HashMap<>();
        this.fileUploadList = new ArrayList<>();
        this.authenticationSet = new HashSet<>();
        this.environmentMap = new HashMap<>();
    }

    /**
     Gets the {@link PluginFileAccess} of this CloudFormationModule.

     @return the {@link PluginFileAccess} of this CloudFormationModule
     */
    public PluginFileAccess getFileAccess() {
        return fileAccess;
    }

    /**
     Gets the name of the CloudFormation Stack that will be created.

     @return the name of the CloudFormation Stack
     */
    public String getStackName() {
        return stackName;
    }

    /**
     Gets the name of the S3 Bucket that will be created to store files.

     @return the name of the S3 Bucket
     @see <a href="https://aws.amazon.com/s3/">S3 Storage</a>
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     Gets the AWS Region this CloudFormationModule is bound to.
     <br>The CloudFormationModule is bound to a region for following reasons:
     <ol>
     <li>Image ids for EC2 Instances are bound to a region.</li>
     </ol>

     @return the {@link String} representation of a AWS Region
     @see <a href="https://aws.amazon.com/about-aws/global-infrastructure/">AWS Regions</a>
     @see <a href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html#concepts-regions">AWS
     Regions usage</a>
     */
    public String getAWSRegion() {
        return this.awsRegion;
    }

    /**
     Gets the {@link AWSCredentials AWS Credentials} that are connected to the CloudFormationModule.
     <br>
     The CloudFormationModule needs credentials for:
     <ol>
     <li>Connecting to AWS using the AWS SDK to get image ids for EC2 Instances</li>
     </ol>

     @return the AWS Credentials of this CloudFormationModule
     */
    public AWSCredentials getAwsCredentials() {
        return this.awsCredentials;
    }

    /**
     Gets the key name variable of this CloudFormationModule.
     <br>
     If a CloudFormationModule has key pair enabled, every instance is set with this key name.

     @return the key name object
     */
    public Object getKeyNameVar() {
        return this.keyNameVar;
    }

    /**
     Checks whether this CloudFormationModule has a key pair.

     @return return true if it has a key pair
     */
    public boolean hasKeyPair() {
        return keyPair;
    }

    /**
     Sets whether this CloudFormationModule should have a key pair.

     @param keyPair true if it should have a key pair
     */
    public void setKeyPair(boolean keyPair) {
        this.keyPair = keyPair;
    }

    /**
     Builder function for {@link #setKeyPair(boolean)}.
     */
    public CloudFormationModule withKeyPair(boolean keyPair) {
        this.keyPair = keyPair;
        return this;
    }

    /**
     Marks a {@link Compute} node to be transformed to an EC2 instance.
     <br>
     The transformation is done by the {@link CloudFormationLifecycle#transform()} operation.

     @param compute the {@link Compute} node that should be marked
     */
    public void addComputeToEc2(Compute compute) {
        computeToEc2.add(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Cancels the mark for a {@link Compute} node from being transformed to an EC2 instance.

     @param compute the {@link Compute} node that should not be transformed
     */
    public void removeComputeToEc2(Compute compute) {
        computeToEc2.remove(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Checks  if this {@link Compute} node is marked to be transformed to an EC2 instance.

     @param compute the {@link Compute} node to check
     @return returns true if the {@link Compute} node is marked
     */
    public boolean checkComputeToEc2(Compute compute) {
        return computeToEc2.contains(toAlphanumerical(compute.getEntityName()));
    }

    /**
     Puts a {@link CFNInit} into a map.
     <br>
     The CFNInit is linked to the resource and will be added to it at build time. This is done so the CFNInit can be
     modified.

     @param resource the resource to add the CFNInit to
     @param init     the CNFInit to add
     */
    public void putCFNInit(String resource, CFNInit init) {
        cfnInitMap.put(resource, init);
    }

    /**
     Gets the CFNInit belonging to the given resource.

     @param resource String id of the resource
     */
    public CFNInit getCFNInit(String resource) {
        return this.cfnInitMap.get(resource);
    }

    /**
     Puts an {@link Fn} with its string representation into a map.
     <br>
     This is done because the {@link org.opentosca.toscana.model.EffectiveModel} only supports {@link String} as values
     for most properties while the CloudFormation Plugin needs {@link Fn} objects which can be used to reference values
     between resources.

     @param key   the string representation.
     @param value the actual {@link Fn} value
     */
    public void putFn(String key, Fn value) {
        this.fnSaver.put(key, value);
    }

    /**
     Checks if the key is saved as a Fn object.
     <br>
     If yes this means at transformation the {@link String} should be replaced with the {@link Fn} so the
     {@link #toString()} works properly.

     @param key the {@link String} to check
     @return returns true if the key is stored in the Fn map
     */
    public boolean checkFn(String key) {
        return this.fnSaver.containsKey(key);
    }

    /**
     Gets the {@link Fn} object corresponding to the key.

     @param key the {@link String} key to get
     @return returns the corresponding {@link Fn} object.
     */
    public Fn getFn(String key) {
        return this.fnSaver.get(key);
    }

    /**
     Gets the list of all files that need uploading.

     @return the list of files that will be uploaded
     */
    public List<FileUpload> getFileUploadList() {
        return fileUploadList;
    }

    /**
     Adds the {@link FileUpload} to the list of files that need uploading.

     @param filePath the file representation
     */
    public void addFileUpload(FileUpload filePath) {
        this.fileUploadList.add(filePath);
    }

    /**
     Marks the instance that it needs authentication to the S3 Bucket. It is put into a map.

     @param instanceName the name of the resource that need authentication
     */
    public void putAuthentication(String instanceName) {
        authenticationSet.add(instanceName);
    }

    /**
     Gets the {@link Set} of instance names of resources that need authentication to the S3 Bucket that will be created.

     @return the set of {@link String} instance names of resources that need authentication
     */
    public Set<String> getAuthenticationSet() {
        return authenticationSet;
    }

    /**
     Puts an environment variable corresponding to a instance into a {@link Map}.
     <br>
     The environment variables get stored in a map where each instance has its own map of environment variables.

     @param instanceName the instance name the environment variable belongs to
     @param key          the key of the environment variable
     @param value        the value of the environment variable
     @see org.opentosca.toscana.plugins.cloudformation.handler.EnvironmentHandler
     */
    public void putEnvironmentMap(String instanceName, String key, String value) {
        this.environmentMap.computeIfAbsent(instanceName, k -> new HashMap<>());
        this.environmentMap.get(instanceName).put(key, value);
    }

    /**
     Gets the {@link Map} of all instances that need environment variables and their environment variables.

     @return the map of instances with their corresponding environment variables
     */
    public Map<String, Map<String, String>> getEnvironmentMap() {
        return environmentMap;
    }

    /**
     Gets the {@link Template} of this CloudFormationModule.
     <br>
     Used to reference things like {@code AWS::StackName} using the {@link Template#ref(String)} method.

     @return the {@link Template} of this CloudFormationModule
     */
    public Template getTemplate() {
        return this.template;
    }

    /**
     Gets the parameters of the template belonging to this CloudFormationModule.

     @return map with the parameters of the template
     */
    public Map<String, Parameter> getParameters() {
        return this.template.getParameters();
    }

    /**
     Transforms this CloudFormationModule into a {@link String} representation.
     <br>
     The returning {@link String} is a valid CloudFormation template.
     Before the {@link Template#toString(Boolean)} method is called with yaml set to true, the {@link #build()} function
     is called to prepare the module.
     */
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
     Build the template.
     <br>
     Following steps are taken:
     <ol>
     <li>Add CFNInit to corresponding instance resource </li>
     <li>Check if EC2 instances need access to S3. If yes, then
     <ol>
     <li>Add necessary IAM resources to the module</li>
     <li>Add <tt>Authentication<tt> and <tt>IamInstanceProfile<tt> to corresponding instance resource</li>
     </ol>
     </li>
     <li>Add the KeyPair to the template if it is needed</li>
     </ol>
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
        if (!fileUploadList.isEmpty()) {
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
