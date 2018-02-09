package org.opentosca.toscana.plugins.cloudformation.util;

import java.io.File;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

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
import static org.opentosca.toscana.plugins.cloudformation.util.StackUtils.getFileURL;

public class OperationHandler {
    private CloudFormationModule cfnModule;
    private Logger logger;

    public OperationHandler(CloudFormationModule cfnModule, Logger logger) {
        this.cfnModule = cfnModule;
        this.logger = logger;
    }

    /**
     Handles a create operation.

     @param node            which the operation belongs to
     @param computeHostName alphanumerical name of the Compute host of node
     */
    public void handleCreate(RootNode node, String computeHostName) {
        if (node.getStandardLifecycle().getCreate().isPresent()) {
            Operation create = node.getStandardLifecycle().getCreate().get();
            handleOperation(create, computeHostName, CONFIG_CREATE);
        }
    }

    /**
     Handles a configure operation.

     @param node            which the operation belongs to
     @param computeHostName alphanumerical name of the Compute host of node
     */
    public void handleConfigure(RootNode node, String computeHostName) {
        if (node.getStandardLifecycle().getConfigure().isPresent()) {
            Operation configure = node.getStandardLifecycle().getConfigure().get();
            handleOperation(configure, computeHostName, CONFIG_CONFIGURE);
        }
    }

    /**
     Handles a start operation.

     @param node            which the operation belongs to
     @param computeHostName alphanumerical name of the Compute host of node
     */
    public void handleStart(RootNode node, String computeHostName) {
        if (node.getStandardLifecycle().getStart().isPresent()) {
            Operation start = node.getStandardLifecycle().getStart().get();
            handleOperation(start, computeHostName, CONFIG_START);
        }
    }

    /**
     Handle implementation artifacts and dependencies for given operation.

     @param operation  to be handled
     @param serverName name of the Compute/EC2 where the artifacts/dependencies must be stored/used
     @param config     name of the config (Create/Start/Configure)
     */
    private void handleOperation(Operation operation, String serverName, String config) {
        handleDependency(operation, serverName, config);
        handleArtifact(operation, serverName, config);
    }

    /**
     Adds all dependencies to file uploads and to the EC2 Instance in the CloudFormation template.

     @param operation  to be handled
     @param serverName name of the Compute/EC2 where the dependencies must be stored
     @param config     name of the config (Create/Start/Configure)
     */
    private void handleDependency(Operation operation, String serverName, String config) {
        //Add dependencies
        for (String dependency : operation.getDependencies()) {
            String cfnSource = getFileURL(cfnModule.getBucketName(), dependency);

            logger.debug("Marking '{}' as file to be uploaded.", dependency);
            cfnModule.putFileToBeUploaded(dependency);
            if (!cfnModule.getAuthenticationSet().contains(serverName)) {
                logger.debug("Marking '{}' as instance in need of authentication.", serverName);
                cfnModule.putAuthentication(serverName);
            } else {
                logger.debug("'{}' already marked as instance in need of authentication. " +
                    "Skipping authentication marking.", serverName);
            }
            CFNFile cfnFile = new CFNFile(ABSOLUTE_FILE_PATH + dependency)
                .setSource(cfnSource)
                .setMode(MODE_644) //TODO Check what mode is needed (only read?)
                .setOwner(OWNER_GROUP_ROOT) //TODO Check what Owner is needed
                .setGroup(OWNER_GROUP_ROOT); //TODO Check what Group is needed

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
     @param serverName name of the Compute/EC2 where the artifacts must be stored and executed
     @param config     name of the config (Create/Start/Configure)
     */
    private void handleArtifact(Operation operation, String serverName, String config) {
        //Add artifact
        if (operation.getArtifact().isPresent()) {
            String artifact = operation.getArtifact().get().getFilePath();
            String cfnSource = getFileURL(cfnModule.getBucketName(), artifact);

            logger.debug("Marking '{}' as file to be uploaded.", artifact);
            cfnModule.putFileToBeUploaded(artifact);
            if (!cfnModule.getAuthenticationSet().contains(serverName)) {
                logger.debug("Marking '{}' as instance in need of authentication.", serverName);
            } else {
                logger.debug("'{}' already marked as instance in need of authentication. " +
                    "Skipping authentication marking.", serverName);
            }
            cfnModule.putAuthentication(serverName);

            CFNFile cfnFile = new CFNFile(ABSOLUTE_FILE_PATH + artifact)
                .setSource(cfnSource)
                .setMode(MODE_500) //TODO Check what mode is needed (read? + execute?)
                .setOwner(OWNER_GROUP_ROOT) //TODO Check what Owner is needed
                .setGroup(OWNER_GROUP_ROOT); //TODO Check what Group is needed

            CFNCommand cfnCommand = new CFNCommand(artifact,
                ABSOLUTE_FILE_PATH + artifact) //file is the full path, so need for "./"
                .setCwd(ABSOLUTE_FILE_PATH + new File(artifact).getParent());
            // add inputs to environment
            for (OperationVariable input : operation.getInputs()) {
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
            // Add file to config and execution command
            cfnModule.getCFNInit(serverName)
                .getOrAddConfig(CONFIG_SETS, config)
                .putFile(cfnFile)
                .putCommand(cfnCommand);
        }
    }

    /**
     Handles the create, configure and start lifecycle operations for the given node.

     @param node        node which the operations belong to
     @param computeHost Compute host of the node
     */
    public void handleGenericHostedNode(RootNode node, Compute computeHost) {
        String computeHostName = toAlphanumerical(computeHost.getEntityName());
        handleCreate(node, computeHostName);
        handleConfigure(node, computeHostName);
        handleStart(node, computeHostName);
    }
}
