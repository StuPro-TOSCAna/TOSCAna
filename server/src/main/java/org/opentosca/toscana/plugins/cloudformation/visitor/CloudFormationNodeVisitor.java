package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.io.File;
import java.util.UUID;
//import java.io.IOException;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.auth.policy.resources.S3ObjectResource;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.CFNPackage;
import com.scaleset.cfbuilder.rds.DBInstance;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CONFIGURE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_INSTALL;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.SECURITY_GROUP;

/**
 * Class for building a CloudFormation template from an effective model instance via the visitor pattern. Currently only
 * supports LAMP-stacks built with Compute, WebApplication, Apache, MySQL, MySQL nodes.
 */
public class CloudFormationNodeVisitor implements StrictNodeVisitor {

    private final Logger logger;
    private CloudFormationModule cfnModule;
    private AmazonS3 s3;
    private String bucketName;

    /**
     * Creates a <tt>CloudFormationNodeVisitor<tt> in order to build a template with the given
     * <tt>CloudFormationModule<tt>.
     *
     * @param logger    Logger for logging visitor behaviour
     * @param cfnModule Module to build the template model
     */
    public CloudFormationNodeVisitor(Logger logger, CloudFormationModule cfnModule) throws Exception {
        this.logger = logger;
        this.cfnModule = cfnModule;
        // TODO Get credentials and possibly region from User
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("","");
        this.s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .withRegion(Regions.US_WEST_2)
            .build();
        this.bucketName = createBucket(s3);
    }

    @Override
    public void visit(Compute node) {
        try {
            logger.debug("Visit Compute node " + node.getNodeName() + ".");
            String nodeName = toAlphanumerical(node.getNodeName());
            //default security group the EC2 Instance opens for port 80 and 22 to the whole internet
            Object cidrIp = "0.0.0.0/0";
            SecurityGroup webServerSecurityGroup = cfnModule.resource(SecurityGroup.class,
                nodeName + SECURITY_GROUP)
                .groupDescription("Enable ports 80 and 22")
                .ingress(ingress -> ingress.cidrIp(cidrIp), "tcp", 80, 22);

            // check what image id should be taken
            // we only support linux ubuntu 16.04 but there should be a mapping of os properties to imageIds
            // ImageIds different depending on the region you use this is us-west-2 atm
            OsCapability computeOs = node.getOs();
            String imageId;
            //here should be a check for isPresent, but what to do if not present?
            if (computeOs.getType().get().equals(OsCapability.Type.LINUX) &&
                computeOs.getDistribution().get().equals(OsCapability.Distribution.UBUNTU) &&
                computeOs.getVersion().get().equals("16.04")) {
                imageId = "ami-0def3275";
            } else {
                throw new UnsupportedTypeException("Only Linux, Ubuntu 16.04 supported.");
            }
            //check what host should be taken
            // we only support t2.micro atm since its free for student accounts
            ComputeCapability computeCompute = node.getHost();
            String instanceType;
            //here should be a check for isPresent, but what to do if not present?
            if (computeCompute.getNumCpus().get().equals(1) &&
                computeCompute.getMemSizeInMB().get().equals(1024)) {
                instanceType = "t2.micro";
            } else {
                throw new UnsupportedTypeException("Only 1 CPU and 1024 MB memory supported.");
            }
            //create CFN init and store it
            CFNInit init = new CFNInit(CONFIG_SETS);
            cfnModule.putCFNInit(nodeName, init);
            //takes default 8GB storage but volume
            cfnModule.resource(Instance.class, nodeName)
                .keyName(cfnModule.getKeyNameVar())
                .securityGroupIds(webServerSecurityGroup)
                .imageId(imageId)
                .instanceType(instanceType);
        } catch (Exception e) {
            logger.error("Error while creating EC2Instance resource.");
            e.printStackTrace();
        }
    }

    @Override
    public void visit(MysqlDatabase node) {
        try {
            logger.debug("Visit MysqlDatabase node " + node.getNodeName() + ".");
            String nodeName = toAlphanumerical(node.getNodeName());

            //get the name of the server where the dbms this node is hosted on, is hosted on
            String serverName;
            if (node.host.getFulfillers().size() == 1) {
                MysqlDbms mysqlDbms = node.host.getFulfillers().toArray(new MysqlDbms[1])[0];
                serverName = toAlphanumerical(mysqlDbms.getHost().getCapability().get().getResourceName().get());
            } else {
                throw new IllegalStateException("More than one fulfiller");
            }
            String dbName = node.getDatabaseName();
            //throw error, take default or generate random?
            String masterUser = node.getUser().orElseThrow(() -> new IllegalArgumentException("Database user not set"));
            String masterPassword = node.getPassword().orElseThrow(() -> new IllegalArgumentException("Database password not set"));
            Integer port = node.getPort().orElse(3306);
            //TODO check downwards to compute and take its values
            String dBInstanceClass = "db.t2.micro";
            Integer allocatedStorage = 20;
            String storageType = "gp2"; //SSD

            String securityGroupName = nodeName + SECURITY_GROUP;
            cfnModule.resource(SecurityGroup.class, securityGroupName)
                .groupDescription("Open database " + dbName + " for access to group " + serverName + SECURITY_GROUP)
                .ingress(ingress -> ingress.sourceSecurityGroupName(cfnModule.ref(serverName + SECURITY_GROUP)),
                    "tcp", port);

            cfnModule.resource(DBInstance.class, nodeName)
                .engine("MySQL")
                .dBName(dbName)
                .masterUsername(masterUser)
                .masterUserPassword(masterPassword)
                .dBInstanceClass(dBInstanceClass)
                .allocatedStorage(allocatedStorage)
                .storageType(storageType)
                .vPCSecurityGroups(cfnModule.fnGetAtt(securityGroupName, "GroupId"));
        } catch (Exception e) {
            logger.error("Error while creating DBInstance resource.");
            e.printStackTrace();
        }
    }

    @Override
    public void visit(MysqlDbms node) {
        logger.debug("Visit MysqlDbms node " + node.getNodeName() + ".");
        // TODO what to do if there is a configure script
        // Get the host of this DBMS node
        // String host = toAlphanumerical(node.getHost().getCapability().getResourceName().get());
    }

    @Override
    public void visit(Apache node) {
        logger.debug("Visit Apache node " + node.getNodeName() + ".");
        // check if host is available
        ComputeCapability computeCapability = node.getHost().getCapability().get();
        if (computeCapability.getResourceName().isPresent()) {
            //Hosted on name
            String host = toAlphanumerical(computeCapability.getResourceName().get());

            cfnModule.getCFNInit(host)
                .getOrAddConfig(CONFIG_SETS, CONFIG_INSTALL)
                .putPackage(
                    //TODO apt only if linux
                    new CFNPackage("apt")
                        .addPackage("apache2"));
        }
    }

    @Override
    public void visit(WebApplication node) {
        logger.debug("Visit WebApplication node " + node.getNodeName() + ".");

        //get the name of the server where this node is hosted on
        String serverName;
        if (node.getHost().getFulfillers().size() == 1) {
            WebServer webServer = node.getHost().getFulfillers().toArray(new WebServer[1])[0];
            serverName = toAlphanumerical(webServer.getHost().getCapability().get().getResourceName().get());
        } else {
            throw new IllegalStateException("More than one or no fulfiller");
        }

        if (node.getStandardLifecycle().getCreate().isPresent()) {
            Operation create = node.getStandardLifecycle().getCreate().get();
            handleOperation(create, serverName, CONFIG_INSTALL);
        }

        if (node.getStandardLifecycle().getConfigure().isPresent()) {
            Operation configure = node.getStandardLifecycle().getConfigure().get();
            handleOperation(configure, serverName, CONFIG_CONFIGURE);
        }
    }

    private String toAlphanumerical(String inp) {
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }

    private void handleOperation(Operation operation, String serverName, String config) {
        String cfnFilePath = "/home/ubuntu/"; // TODO Check what path is needed

        //Add dependencies
        for (String dependency : operation.getDependencies()) {
            try {
                String cfnFileMode = "000400"; //TODO Check what mode is needed (only read?)
                String cfnFileOwner = "root"; //TODO Check what Owner is needed
                String cfnFileGroup = "root"; //TODO Check what Group is needed

                // TODO: re-allow content-dumping instead of source
//                CFNFile cfnFile = new CFNFile(cfnFilePath + dependency)
//                    .setContent(cfnModule.getFileAccess().read(dependency))
//                    .setMode(cfnFileMode)
//                    .setOwner(cfnFileOwner)
//                    .setGroup(cfnFileGroup);

                String cfnSource = uploadFileAndGetURL(s3, bucketName, new File(dependency));
                CFNFile cfnFile = new CFNFile(cfnFilePath + dependency)
                    .setSource(cfnSource)
                    .setMode(cfnFileMode)
                    .setOwner(cfnFileOwner)
                    .setGroup(cfnFileGroup);

                // Add file to install
                cfnModule.getCFNInit(serverName)
                    .getOrAddConfig(CONFIG_SETS, config)
                    .putFile(cfnFile);
//            } catch (IOException e) {
            } catch (Exception e) {
                logger.error("Problem with reading file " + dependency);
                e.printStackTrace();
            }
        }

        //Add artifact
        if (operation.getArtifact().isPresent()) {
            String artifact = operation.getArtifact().get().getFilePath();

            String cfnFileMode = "000500"; //TODO Check what mode is needed (read? + execute?)
            String cfnFileOwner = "root"; //TODO Check what Owner is needed
            String cfnFileGroup = "root"; //TODO Check what Group is needed

            try {
                // TODO: re-allow content-dumping instead of source
//                CFNFile cfnFile = new CFNFile(cfnFilePath + artifact)
//                    .setContent(cfnModule.getFileAccess().read(artifact))
//                    .setMode(cfnFileMode)
//                    .setOwner(cfnFileOwner)
//                    .setGroup(cfnFileGroup);

                String cfnSource = uploadFileAndGetURL(s3, bucketName, new File(artifact));
                CFNFile cfnFile = new CFNFile(cfnFilePath + artifact)
                    .setSource(cfnSource)
                    .setMode(cfnFileMode)
                    .setOwner(cfnFileOwner)
                    .setGroup(cfnFileGroup);

                CFNCommand cfnCommand = new CFNCommand(artifact,
                    cfnFilePath + artifact) //file is the full path, so need for "./"
                    .setCwd(cfnFilePath + new File(artifact).getParent());
                // add inputs to environment, but where to get other needed variables?
                for (OperationVariable input : operation.getInputs()) {
                    Object value = input.getValue().orElse("");
                    if ("".equals(value) && input.getKey().contains("host")) {
                        value = cfnModule.fnGetAtt("mydb", "Endpoint.Address");
                    }
                    cfnCommand.addEnv(input.getKey(), value); //TODO add default
                }
                cfnModule.getCFNInit(serverName)
                    .getOrAddConfig(CONFIG_SETS, config)
                    .putFile(cfnFile)
                    .putCommand(cfnCommand)
                    .putCommand(new CFNCommand("restart apache2", "service apache2 restart")); //put commands
//            } catch (IOException e) {
            } catch (Exception e) {
                logger.error("Problem with reading file " + artifact);
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a new bucket with a random name on the given AmazonS3, sets its policy
     * and returns the name of said bucket.
     *
     * @param s3 where the bucket should be created
     * @return name of the created bucket
     */
    private String createBucket(AmazonS3 s3) {
        try {
            String bucketName = "cf-bucket-" + UUID.randomUUID();
            logger.debug("Creating bucket " + bucketName + ".");
            s3.createBucket(bucketName);

            // TODO check if files need to be public for CloudFormation to access them
            // Allows anyone to access files in the bucket
            s3.setBucketPolicy(bucketName, getPublicReadPolicy().toJson());
            // TODO: Possibly stop transformation when Exceptions occur
            // Then Handle these Exception in the transform() function in the CloudFormationLifeCycle
            // The same goes for those in the fileUploadAndGetUrl function.
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException while trying to create the bucket.");
            logASEError(ase);
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException while trying to create the bucket.");
            logACEError(ace);
        } catch (IllegalArgumentException iae) {
            logger.error("Caught an IllegalArgumentException while trying to create the bucket.");
            logIAEError(iae);
        }
        return bucketName;
    }

    /**
     * Logs the details of an AmazonServiceException as an error.
     *
     * @param ase the AmazonServiceException to be reported on
     */
    private void logASEError(AmazonServiceException ase) {
        logger.error("This means the request made it to Amazon S3, but was rejected with an error response.");
        logger.error("Error Message:    " + ase.getMessage());
        logger.error("HTTP Status Code: " + ase.getStatusCode());
        logger.error("AWS Error Code:   " + ase.getErrorCode());
        logger.error("Error Type:       " + ase.getErrorType());
        logger.error("Request ID:       " + ase.getRequestId());
    }

    /**
     * Logs the details of an AmazonClientException as an error.
     *
     * @param ace the AmazonClientException to be reported on
     */
    private void logACEError(AmazonClientException ace) {
        logger.error("This means the client encountered an internal problem while trying to communicate with S3.");
        logger.error("Error Message: " + ace.getMessage());
    }

    private void logIAEError(IllegalArgumentException iae) {
        logger.error("This means that the bucketName is invalid" +
            " most likely due to a failure during the bucket creation.");
        iae.printStackTrace();
    }

    /**
     * Uploads a file to a bucket on AmazonS3 and returns its URL.
     * If the upload fails, an empty URL is returned instead.
     *
     * @return the URL of the file
     */
    private String uploadFileAndGetURL(AmazonS3 s3, String bucketName, File file) {
        String fileURL = "";
        String key = file.getName();
        try {
            String fileName = "";
            logger.debug("Uploading file " + fileName + "to S3.");
            s3.putObject(new PutObjectRequest(bucketName, key, file));
            //TODO: Find non-deprecated method for retrieving the URL
            fileURL = new AmazonS3Client().getResourceUrl(bucketName, key);
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException while trying to upload a file.");
            logASEError(ase);
            logger.error("Returning an empty fileURL.");
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException while trying to upload a file.");
            logACEError(ace);
            logger.error("Returning an empty fileURL.");
        } catch (IllegalArgumentException iae) {
            logger.error("Caught an IllegalArgumentException while trying to upload a file.");
            logIAEError(iae);
            logger.error("Returning an empty fileURL.");
        }
        return fileURL;
    }

    /**
     * Returns a policy that allows anyone access to read all the objects in a bucket.
     *
     * @return the public read policy
     */
    private Policy getPublicReadPolicy() {
        Statement allowPublicReadStatement = new Statement(Statement.Effect.Allow)
            .withPrincipals(Principal.AllUsers)
            .withActions(S3Actions.GetObject)
            .withResources(new S3ObjectResource(bucketName, "*"));
        return new Policy().withStatements(allowPublicReadStatement);
    }
}
