package org.opentosca.toscana.plugins.cloudformation.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;
import org.opentosca.toscana.plugins.cloudformation.util.FileUpload;

import com.scaleset.cfbuilder.beanstalk.OptionSetting;
import com.scaleset.cfbuilder.beanstalk.SourceBundle;
import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.ABSOLUTE_FILE_PATH;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CONFIGURE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_CREATE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_SETS;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.CONFIG_START;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.MODE_500;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.MODE_644;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.OWNER_GROUP_ROOT;
import static org.opentosca.toscana.plugins.cloudformation.util.FileUpload.UploadFileType.FROM_CSAR;
import static org.opentosca.toscana.plugins.cloudformation.util.FileUpload.UploadFileType.UTIL;
import static org.opentosca.toscana.plugins.cloudformation.util.StackUtils.getFileURL;

/**
 Handles {@link Operation}s of nodes.
 */
public class OperationHandler {
    public static final String APACHE_RESTART_COMMAND = "service apache2 restart";
    private static final String BEANSTALK_NAMESPACE_ENVIRONMENT = "aws:elasticbeanstalk:application:environment";

    private CloudFormationModule cfnModule;
    private Logger logger;

    /**
     Sets up the OperationHandler with the {@link CloudFormationModule} and the {@link Logger}.

     @param cfnModule the {@link CloudFormationModule} to use
     @param logger    the {@link Logger} to use
     */
    public OperationHandler(CloudFormationModule cfnModule, Logger logger) {
        this.cfnModule = cfnModule;
        this.logger = logger;
    }

    /**
     Handles a create {@link Operation}.

     @param node            which the {@link Operation} belongs to
     @param computeHostName alphanumerical name of the {@link Compute} host of node
     */
    public void handleCreate(RootNode node, String computeHostName) {
        if (node.getStandardLifecycle().getCreate().isPresent()) {
            Operation create = node.getStandardLifecycle().getCreate().get();
            handleOperation(create, computeHostName, CONFIG_CREATE);
        }
    }

    /**
     Handles a configure {@link Operation}.

     @param node            which the {@link Operation} belongs to
     @param computeHostName alphanumerical name of the {@link Compute} host of node
     */
    public void handleConfigure(RootNode node, String computeHostName) {
        if (node.getStandardLifecycle().getConfigure().isPresent()) {
            Operation configure = node.getStandardLifecycle().getConfigure().get();
            handleOperation(configure, computeHostName, CONFIG_CONFIGURE);
        }
    }

    /**
     Handles a start {@link Operation}.

     @param node            which the {@link Operation} belongs to
     @param computeHostName alphanumerical name of the {@link Compute} host of node
     */
    public void handleStart(RootNode node, String computeHostName) {
        if (node.getStandardLifecycle().getStart().isPresent()) {
            Operation start = node.getStandardLifecycle().getStart().get();
            handleOperation(start, computeHostName, CONFIG_START);

            // Add environment variables
            Map<String, String> envVars = getEnvVars(start);
            envVars.forEach((key, value) -> cfnModule.putEnvironmentMap(computeHostName, key, value));
        }
    }

    /**
     Gets a map of all environment variables from this {@link Operation}.

     @param operation an existing operation
     @return a map of all environment variables from this operation
     */
    public Map<String, String> getEnvVars(Operation operation) {
        Map<String, String> envVars = new HashMap<>();
        operation.getInputs().forEach(operationVariable -> {
            String value = operationVariable.getValue().orElseThrow(
                () -> new IllegalArgumentException("Input value of " + operationVariable.getKey() + " expected to" +
                    " not be null")
            );
            envVars.put(operationVariable.getKey(), value);
        });
        return envVars;
    }

    /**
     Manually adds a create command with the given input variables and file path to the given EC2 instance.

     @param filePath   path to the artifact
     @param serverName name of the Instance
     */
    public void addCreate(String filePath, String serverName) {
        markUtilFile(filePath);
        CFNFile cfnFile = handleOperationFile(filePath, MODE_500, serverName);
        CFNCommand cfnCommand = handleOperationCommand(filePath, new HashSet<>());

        // Add file to config and execution command
        cfnModule.getCFNInit(serverName)
            .getOrAddConfig(CONFIG_SETS, CONFIG_CREATE)
            .putFile(cfnFile)
            .putCommand(cfnCommand);
    }

    /**
     Handles implementation artifacts and dependencies for given {@link Operation}.

     @param operation  to be handled
     @param serverName name of the {@link Compute}/EC2 where the artifacts/dependencies must be stored/used
     @param config     name of the config (Create/Start/Configure)
     */
    private void handleOperation(Operation operation, String serverName, String config) {
        handleDependency(operation, serverName, config);
        handleArtifact(operation, serverName, config);
    }

    /**
     Adds all dependencies to file uploads and to the EC2 Instance in the CloudFormation template.

     @param operation  to be handled
     @param serverName name of the {@link Compute}/EC2 where the dependencies must be stored
     @param config     name of the config (Create/Start/Configure)
     */
    private void handleDependency(Operation operation, String serverName, String config) {
        //Add dependencies
        for (String dependency : operation.getDependencies()) {
            markFile(dependency);
            CFNFile cfnFile = handleOperationFile(dependency, MODE_644, serverName);

            // Add file to config
            cfnModule.getCFNInit(serverName)
                .getOrAddConfig(CONFIG_SETS, config)
                .putFile(cfnFile);
        }
    }

    /**
     Adds all artifacts to file uploads and to the EC2 Instance in the CloudFormation template.
     Also adds them as commands with input variables as environment variables to the given config.

     @param operation  to be handled
     @param serverName name of the {@link Compute}/EC2 where the artifacts must be stored and executed
     @param config     name of the config (Create/Start/Configure)
     */
    private void handleArtifact(Operation operation, String serverName, String config) {
        //Add artifact
        if (operation.getArtifact().isPresent()) {
            String artifact = operation.getArtifact().get().getFilePath();
            Set<OperationVariable> inputs = operation.getInputs();

            CFNCommand cfnCommand = handleOperationCommand(artifact, inputs);
            markFile(artifact);
            CFNFile cfnFile = handleOperationFile(artifact, MODE_500, serverName);

            // Add file to config and execution command
            cfnModule.getCFNInit(serverName)
                .getOrAddConfig(CONFIG_SETS, config)
                .putFile(cfnFile)
                .putCommand(cfnCommand);
        }
    }

    /**
     Takes an artifact path and input variables and returns the corresponding CloudFormation command with input
     variables.

     @param artifact path to the artifact
     @param inputs   set with all input variables
     @return CFNCommand corresponding to the given artifact
     */
    private CFNCommand handleOperationCommand(String artifact, Set<OperationVariable> inputs) {
        String parent = new File(artifact).getParent();
        if (parent == null) {
            parent = "";
        }
        CFNCommand cfnCommand = new CFNCommand(artifact,
            ABSOLUTE_FILE_PATH + artifact) //file is the full path, so need for "./"
            .setCwd(ABSOLUTE_FILE_PATH + parent);
        // add inputs to environment
        for (OperationVariable input : inputs) {
            String value = input.getValue().orElseThrow(
                () -> new IllegalArgumentException("Input value of " + input.getKey() + " expected to not be " +
                    "null")
            );
            if (cfnModule.checkFn(value)) {
                cfnCommand.addEnv(input.getKey(), cfnModule.getFn(value));
            } else {
                cfnCommand.addEnv(input.getKey(), value);
            }
        }
        return cfnCommand;
    }

    /**
     Takes a filePath, mode and instance name and returns the corresponding CloudFormation file.
     Also marks instance as in need of authentication and file to be uploaded if needed.

     @param filePath   path to the file
     @param mode       mode to set access rights
     @param serverName corresponding to the EC2 instance
     @return CFNFile corresponding to the given filePath, mode and serverName
     */
    private CFNFile handleOperationFile(String filePath, String mode, String serverName) {
        String cfnSource = getFileURL(cfnModule.getBucketName(), filePath);

        if (!cfnModule.getAuthenticationSet().contains(serverName)) {
            logger.debug("Marking '{}' as instance in need of authentication.", serverName);
        } else {
            logger.debug("'{}' already marked as instance in need of authentication. " +
                "Skipping authentication marking.", serverName);
        }
        cfnModule.putAuthentication(serverName);

        return new CFNFile(ABSOLUTE_FILE_PATH + filePath)
            .setSource(cfnSource)
            .setMode(mode) //TODO Check what mode is needed (read? + execute?)
            .setOwner(OWNER_GROUP_ROOT) //TODO Check what Owner is needed
            .setGroup(OWNER_GROUP_ROOT);
    }

    /**
     Marks file as file to be uploaded.

     @param filePath path to file that needs to be uploaded
     */
    private void markFile(String filePath) {
        logger.debug("Marking '{}' as file to be uploaded.", filePath);
        cfnModule.addFileUpload(new FileUpload(filePath, FROM_CSAR));
    }

    /**
     Marks file as util file to be uploaded.

     @param filePath path to file that needs to be uploaded
     */
    private void markUtilFile(String filePath) {
        logger.debug("Marking '{}' as util file to be uploaded.", filePath);
        cfnModule.addFileUpload(new FileUpload(filePath, UTIL));
    }

    /**
     Handles the create, configure and start lifecycle {@link Operation}s for the given node.

     @param node        node which the {@link Operation}s belong to
     @param computeHost {@link Compute} host of the node
     */
    public void handleGenericHostedNode(RootNode node, Compute computeHost) {
        String computeHostName = toAlphanumerical(computeHost.getEntityName());
        handleCreate(node, computeHostName);
        handleConfigure(node, computeHostName);
        handleStart(node, computeHostName);
    }

    /**
     Handles a artifact that is a jar

     @param artifact artifact that needs to be handled
     @return a SourceBundle that contains the bucket and filename
     */
    public SourceBundle handleJarArtifact(Artifact artifact) {
        String jarFilePath = artifact.getFilePath();
        markFile(jarFilePath);
        return new SourceBundle(cfnModule.getBucketName(), jarFilePath);
    }

    /**
     Handles the start lifecycle {@link Operation} for a {@link JavaApplication} node.
     <br>
     It generates a list of {@link OptionSetting} where each OptionSetting contains a environment variables defined in
     the start lifecycle. These are needed for the Beanstalk Application.

     @param node the JavaApplication node to handle the start lifecycle for
     @return a list of OptionSettings from environment variables
     */
    public List<OptionSetting> handleStartJava(JavaApplication node) {
        List<OptionSetting> optionSettings = new ArrayList<>();
        if (node.getStandardLifecycle().getStart().isPresent()) {
            Map<String, String> envVars = getEnvVars(node.getStandardLifecycle().getStart().get());
            envVars.forEach((key, value) -> {
                if (cfnModule.checkFn(value)) {
                    optionSettings.add(new OptionSetting(BEANSTALK_NAMESPACE_ENVIRONMENT, key)
                        .setValue(cfnModule.getFn(value)));
                } else {
                    optionSettings.add(new OptionSetting(BEANSTALK_NAMESPACE_ENVIRONMENT, key)
                        .setValue(value));
                }
            });
        }
        return optionSettings;
    }
}
