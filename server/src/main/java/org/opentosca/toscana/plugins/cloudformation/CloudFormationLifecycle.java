package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudformation.visitor.CheckModelVisitor;
import org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

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

    @Override
    public boolean checkModel() {
        logger.info("Check model for compatibility to CloudFormation");
        Set<RootNode> nodes = model.getNodes();
        try {
            CheckModelVisitor checkModelVisitor = new CheckModelVisitor(logger);
            for (VisitableNode node : nodes) {
                node.accept(checkModelVisitor);
            }
        } catch (UnsupportedTypeException ute) {
            return false;
        }
        return true;
    }

    @Override
    public void prepare() {
//        TODO implement preparation
    }

    @Override
    public void transform() {
        logger.info("Begin transformation to CloudFormation.");
        PluginFileAccess fileAccess = context.getPluginFileAccess();
        CloudFormationModule cfnModule = new CloudFormationModule(fileAccess, awsRegion, awsCredentials);
        Set<RootNode> nodes = model.getNodes();

        try {
            CloudFormationNodeVisitor cfnNodeVisitor = new CloudFormationNodeVisitor(logger, cfnModule);
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

            fileAccess.access("output/template.yaml").appendln(cfnModule.toString()).close();
            logger.info("Transformation to CloudFormation successful.");
        } catch (IOException ie) {
            logger.error("File access error");
            throw new TransformationFailureException("Could not write with fileAccess", ie);
        } catch (TransformationFailureException tfe) {
            logger.error("Transformation to CloudFormation unsuccessful. Please check the StackTrace for more Info.");
            tfe.printStackTrace();
            throw tfe;
        } catch (Exception e) {
            logger.error("Transformation to CloudFormation unsuccessful. Random Exception should not appear here.");
            throw new TransformationFailureException("Random exception", e);
        }
    }

    @Override
    public void cleanup() {
        //noop
    }
}
