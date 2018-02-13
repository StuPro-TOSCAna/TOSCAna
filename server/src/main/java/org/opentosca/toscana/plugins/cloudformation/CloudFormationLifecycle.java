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

public class CloudFormationLifecycle extends AbstractLifecycle {
    private final EffectiveModel model;
    private CloudFormationModule cfnModule;
    private PluginFileAccess fileAccess;

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
     Convert input to alphanumerical string
     */
    public static String toAlphanumerical(String inp) {
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }

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

    @Override
    public void transform() {
        logger.info("Begin transformation to CloudFormation.");
        Set<RootNode> nodes = model.getNodes();
        // Visit Compute nodes first, then all others
        try {
            TransformModelNodeVisitor cfnNodeVisitor = new TransformModelNodeVisitor(context, cfnModule);
            logger.info("Transform nodes");
            visitComputeNodesFirst(nodes, cfnNodeVisitor);
            logger.info("Creating CloudFormation template.");
            fileAccess.access(OUTPUT_DIR + CloudFormationFileCreator.TEMPLATE_YAML)
                .appendln(cfnModule.toString()).close();
            CloudFormationFileCreator fileCreator = new CloudFormationFileCreator(context, cfnModule);
            logger.info("Creating CloudFormation scripts.");
            fileCreator.copyUtilScripts();
            fileCreator.writeScripts();
            fileCreator.copyFiles();
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

    @Override
    public void cleanup() {
        //noop
    }

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

    private void visitAllNodes(Set<RootNode> nodes, NodeVisitor nodeVisitor) {
        nodes.forEach(node -> {
            logger.debug("Visit '{}' node '{}'", node.getClass().getSimpleName(), node.getEntityName());
            node.accept(nodeVisitor);
        });
    }

    private void visitAllRelationships(Set<RootRelationship> relationships, RelationshipVisitor relationshipVisitor) {
        relationships.forEach(relation -> {
            logger.debug("Visit '{}' relationship '{}'", relation.getClass().getSimpleName(), relation.getEntityName());
            relation.accept(relationshipVisitor);
        });
    }
}
