package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.model.visitor.VisitableRelationship;
import org.opentosca.toscana.plugins.cloudformation.visitor.CheckModelNodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.CheckModelRelationshipVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.PrepareModelNodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.PrepareModelRelationshipVisitor;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.jgrapht.Graph;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_ACCESS_KEY_ID_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_REGION_KEY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_SECRET_KEY_KEY;

public class CloudFormationLifecycle extends AbstractLifecycle {
    private final EffectiveModel model;
    private String awsRegion;
    private AWSCredentials awsCredentials;

    public CloudFormationLifecycle(TransformationContext context) throws IOException {
        super(context);
        model = context.getModel();
        if (context.getProperties() == null) {
            //lifecycle test failes because getProperties is null
            awsRegion = "us-west-2";
            awsCredentials = new BasicAWSCredentials("", "");
            return;
        }
        Map<String, String> properties = context.getProperties().getPropertyValues();
        awsRegion = properties.get(AWS_REGION_KEY);
        String keyId = properties.get(AWS_ACCESS_KEY_ID_KEY);
        String secretKey = properties.get(AWS_SECRET_KEY_KEY);
        awsCredentials = new BasicAWSCredentials(keyId, secretKey);
    }

    public static String toAlphanumerical(String inp) {
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }

    @Override
    public boolean checkModel() {
        logger.info("Check model for compatibility to CloudFormation");
        Set<RootNode> nodes = model.getNodes();
        Set<RootRelationship> relationships = model.getTopology().edgeSet();
        try {
            CheckModelNodeVisitor checkModelNodeVisitor = new CheckModelNodeVisitor(logger);
            logger.debug("Check nodes");
            for (VisitableNode node : nodes) {
                node.accept(checkModelNodeVisitor);
            }
            CheckModelRelationshipVisitor checkModelRelationshipVisitor = new CheckModelRelationshipVisitor(logger,
                model.getTopology());
            logger.debug("Check relationships");
            for (VisitableRelationship relationship : relationships) {
                relationship.accept(checkModelRelationshipVisitor);
            }
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
        PrepareModelNodeVisitor prepareModelNodeVisitor = new PrepareModelNodeVisitor(logger, topology);
        logger.debug("Prepare nodes");
        for (VisitableNode node : nodes) {
            node.accept(prepareModelNodeVisitor);
        }
        logger.debug("Prepare relationships");
        PrepareModelRelationshipVisitor prepareModelRelationshipVisitor = new PrepareModelRelationshipVisitor(logger,
            topology);
        for (VisitableRelationship relationship : topology.edgeSet()) {
            relationship.accept(prepareModelRelationshipVisitor);
        }
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to CloudFormation.");
        PluginFileAccess fileAccess = context.getPluginFileAccess();
        CloudFormationModule cfnModule = new CloudFormationModule(fileAccess, awsRegion, awsCredentials);
        Set<RootNode> nodes = model.getNodes();

        // Visit Compute nodes first, then all others
        try {
            CloudFormationNodeVisitor cfnNodeVisitor = new CloudFormationNodeVisitor(logger, cfnModule, model.getTopology());
            for (VisitableNode node : nodes) {
                if (node instanceof Compute) {
                    node.accept(cfnNodeVisitor);
                }
            }
            for (VisitableNode node : nodes) {
                if (!(node instanceof Compute)) {
                    node.accept(cfnNodeVisitor);
                }
            }
            logger.info("Creating CloudFormation template.");
            fileAccess.access(OUTPUT_DIR + CloudFormationFileCreator.TEMPLATE_YAML)
                .appendln(cfnModule.toString()).close();
            CloudFormationFileCreator fileCreator = new CloudFormationFileCreator(logger, cfnModule);
            logger.info("Creating CloudFormation scripts.");
            fileCreator.copyUtilScripts();
            fileCreator.createScripts();
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
}
