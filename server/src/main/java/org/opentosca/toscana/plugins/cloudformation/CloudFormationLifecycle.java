package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;
import org.opentosca.toscana.plugins.cloudformation.handler.EnvironmentHandler;
import org.opentosca.toscana.plugins.cloudformation.visitor.CheckModelNodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.CheckModelRelationshipVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.PrepareModelNodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.PrepareModelRelationshipVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.TransformModelNodeVisitor;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_ACCESS_KEY_ID_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_KEYPAIR_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_REGION_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_SECRET_KEY_KEY;

/**
 Implements the {@link AbstractLifecycle} for the {@link CloudFormationPlugin}.
 It creates a {@link CloudFormationModule} and is able to perform the lifecycle operations.
 */
public class CloudFormationLifecycle extends AbstractLifecycle {
    /**
     The Effective Model where the TOSCA data is offered.
     */
    private final EffectiveModel model;
    /**
     The CloudFormationModule for this transformation.
     <br>
     The CloudFormationModule contains information needed for the CloudFormation template but also additional information
     such as the intended name of the stack, paths to files that needed to be uploaded and other things related to the
     deployment. Every information that will be acquired during the lifecycle phases will be stored here.
     */
    private CloudFormationModule cfnModule;
    /**
     The PluginFileAccess for this transformation.
     <br>
     The final template and scripts will be stored using this.
     */
    private PluginFileAccess fileAccess;

    /**
     It takes the mandatory properties and sets up the {@link CloudFormationModule} that is used.

     @param context the transformation context for this transformation
     */
    public CloudFormationLifecycle(TransformationContext context) throws IOException {
        super(context);
        model = context.getModel();
        PropertyInstance properties = context.getInputs();
        String awsRegion = properties.getOrThrow(AWS_REGION_KEY);
        String keyId = properties.getOrThrow(AWS_ACCESS_KEY_ID_KEY);
        String secretKey = properties.getOrThrow(AWS_SECRET_KEY_KEY);
        String keypair = properties.getOrThrow(AWS_KEYPAIR_KEY);
        AWSCredentials awsCredentials = new BasicAWSCredentials(keyId, secretKey);
        this.fileAccess = context.getPluginFileAccess();
        this.cfnModule = new CloudFormationModule(fileAccess, awsRegion, awsCredentials);
        if (StringUtils.equals("true", keypair)) {
            cfnModule.setKeyPair(true);
        } else {
            cfnModule.setKeyPair(false);
        }
    }

    /**
     Converts the input string into an alphanumerical string (regex: {@code [^A-Za-z0-9]})

     @param input the string to be converted
     @return the converted string
     */
    public static String toAlphanumerical(String input) {
        return input.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     Performs the {@code checkModel()} lifecycle operation.
     <br>
     The implementation of {@link AbstractLifecycle#checkModel()}.
     <br>
     Using the {@link CheckModelNodeVisitor} and the {@link CheckModelRelationshipVisitor} every node and relationship in
     the {@link EffectiveModel} is visited.

     @return if every found type is accepted return true
     */
    @Override
    public boolean checkModel() {
        logger.info("Check model for compatibility to CloudFormation");
        Set<RootNode> nodes = model.getNodes();
        Set<RootRelationship> relationships = model.getTopology().edgeSet();
        try {
            CheckModelNodeVisitor checkModelNodeVisitor = new CheckModelNodeVisitor(context);
            logger.info("Check nodes");
            visitAllNodes(nodes, checkModelNodeVisitor);
            CheckModelRelationshipVisitor checkModelRelationshipVisitor = new CheckModelRelationshipVisitor(context);
            logger.info("Check relationships");
            visitAllRelationships(relationships, checkModelRelationshipVisitor);
        } catch (UnsupportedTypeException ute) {
            logger.error(ute.getMessage());
            return false;
        }
        return true;
    }

    /**
     Performs the {@code prepare()} lifecycle operation.
     <br>
     The implementation of {@link AbstractLifecycle#prepare()}.
     <br>
     Using the {@link PrepareModelNodeVisitor} and the {@link PrepareModelRelationshipVisitor} every node and
     relationship in the {@link EffectiveModel} is visited.
     {@link Compute} nodes are visited first.
     */
    @Override
    public void prepare() {
        logger.info("Prepare model for compatibility to CloudFormation");
        Set<RootNode> nodes = model.getNodes();
        Graph<RootNode, RootRelationship> topology = model.getTopology();
        PrepareModelNodeVisitor prepareModelNodeVisitor = new PrepareModelNodeVisitor(context, cfnModule);
        logger.info("Prepare nodes");
        visitComputeNodesFirst(nodes, prepareModelNodeVisitor);
        logger.info("Prepare relationships");
        PrepareModelRelationshipVisitor prepareModelRelationshipVisitor = new PrepareModelRelationshipVisitor(context, cfnModule);
        visitAllRelationships(topology.edgeSet(), prepareModelRelationshipVisitor);
    }

    /**
     Performs the {@code transform()} lifecycle operation.
     <br>
     The implementation of {@link AbstractLifecycle#transform()}.
     <br>
     Using the {@link TransformModelNodeVisitor} every node in the {@link EffectiveModel} is visited.
     {@link Compute} nodes are visited first.
     Also environment variables which will appear in the transformed model are handled.
     The main artifact of the transformation, the CloudFormation template is created. Using the
     {@link CloudFormationFileCreator} all necessary scripts for the deployment are created.
     */
    @Override
    public void transform() {
        logger.info("Begin transformation to CloudFormation.");
        Set<RootNode> nodes = model.getNodes();
        // Visit Compute nodes first, then all others
        try {
            TransformModelNodeVisitor cfnNodeVisitor = new TransformModelNodeVisitor(context, cfnModule);
            logger.info("Transform nodes");
            visitComputeNodesFirst(nodes, cfnNodeVisitor);
            logger.info("Handling environment variables.");
            EnvironmentHandler environmentHandler = new EnvironmentHandler(cfnModule, logger);
            environmentHandler.handleEnvironment();
            logger.info("Creating CloudFormation template.");
            fileAccess.access(OUTPUT_DIR + CloudFormationFileCreator.TEMPLATE_YAML)
                .appendln(cfnModule.toString()).close();
            CloudFormationFileCreator fileCreator = new CloudFormationFileCreator(context, cfnModule);
            logger.info("Creating CloudFormation scripts.");
            fileCreator.copyUtilScripts();
            fileCreator.copyUtilDependencies();
            fileCreator.writeScripts();
            fileCreator.copyFiles();
            fileCreator.writeReadme(context);
        } catch (IOException ie) {
            logger.error("File access error");
            throw new TransformationFailureException("Could not write template with fileAccess", ie);
        } catch (TransformationFailureException tfe) {
            logger.error("Transformation to CloudFormation unsuccessful. Please check the StackTrace for more Info.");
            throw tfe;
        } catch (Exception e) {
            logger.error("Transformation to CloudFormation unsuccessful. Unexpected exception should not appear here.");
            throw new TransformationFailureException("Unexpected exception", e);
        }
        logger.info("Transformation to CloudFormation successful.");
    }

    /**
     Performs the {@code cleanup()} lifecycle operation.
     <br>
     The implementation of {@link AbstractLifecycle#cleanup()} does nothing.
     */
    @Override
    public void cleanup() {
        //noop
    }

    /**
     Visits all {@link Compute} nodes first then all others.
     <br>
     Using the {@code nodeVisitor} first every compute node is visited and afterwards every node that is not a compute
     node is visited.

     @param nodes       a set of {@link RootNode} to visit
     @param nodeVisitor the {@link NodeVisitor} to use
     */
    private void visitComputeNodesFirst(Set<RootNode> nodes, NodeVisitor nodeVisitor) {
        nodes.stream()
            .filter(node -> node instanceof Compute)
            .forEach(node -> {
                logger.debug("Visit '{}' node '{}'", node.getClass().getSimpleName(), node.getEntityName());
                node.accept(nodeVisitor);
            });
        nodes.stream()
            .filter(node -> !(node instanceof Compute))
            .forEach(node -> {
                logger.debug("Visit '{}' node '{}'", node.getClass().getSimpleName(), node.getEntityName());
                node.accept(nodeVisitor);
            });
    }

    /**
     Visits all nodes without any particular order.
     <br>
     Using the {@code nodeVisitor} every node is visited.

     @param nodes       a set of {@link RootNode} to visit
     @param nodeVisitor the {@link NodeVisitor} to use
     */
    private void visitAllNodes(Set<RootNode> nodes, NodeVisitor nodeVisitor) {
        nodes.forEach(node -> {
            logger.debug("Visit '{}' node '{}'", node.getClass().getSimpleName(), node.getEntityName());
            node.accept(nodeVisitor);
        });
    }

    /**
     Visits all relationships without any particular order.
     <br>
     Using the {@code relationshipVisitor} every relationship is visited.

     @param relationships       a set of {@link RootRelationship} to visit
     @param relationshipVisitor the {@link RelationshipVisitor} to use
     */
    private void visitAllRelationships(Set<RootRelationship> relationships, RelationshipVisitor relationshipVisitor) {
        relationships.forEach(relation -> {
            logger.debug("Visit '{}' relationship '{}'", relation.getClass().getSimpleName(), relation.getEntityName());
            relation.accept(relationshipVisitor);
        });
    }
}
