package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_REGION_DEFAULT;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationPlugin.AWS_REGION_KEY;

public class CloudFormationLifecycle extends AbstractLifecycle {
    private final EffectiveModel model;
    private String awsRegion;
    
    public CloudFormationLifecycle(TransformationContext context) throws IOException {
        super(context);
        model = context.getModel();
        if (context.getProperties() == null) {
            //lifecycle test failes because getProperties is null
            awsRegion = AWS_REGION_DEFAULT;
            return;
        }
        awsRegion = context.getProperties().getPropertyValue(AWS_REGION_KEY).orElse(AWS_REGION_DEFAULT);
    }

    @Override
    public boolean checkModel() {
//        TODO implement model checks
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
        CloudFormationModule cfnModule = new CloudFormationModule(fileAccess);
        Set<RootNode> nodes = model.getNodes();

        // Visit Compute nodes first, then all others
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
            logger.info("Creating CloudFormation template.");
            fileAccess.access("output/template.yaml").appendln(cfnModule.toString()).close();
        } catch (Exception e) {
            logger.error("Transformation to CloudFormation failed during template creation." +
                " Please check the StackTrace for more Info.");
            e.printStackTrace();
        }

        try {
            CloudFormationFileCreator fileCreator = new CloudFormationFileCreator(logger, cfnModule);
            logger.info("Creating CloudFormation scripts.");
            fileCreator.copyUtilScripts();
            fileCreator.createScripts();
            fileCreator.copyFiles();
        } catch (IOException e) {
            logger.error("Transformation to CloudFormation failed during file creation." +
                " Please check the StackTrace for more Info.");
            e.printStackTrace();
        }

        logger.info("Transformation to CloudFormation successful.");
    }

    @Override
    public void cleanup() {
        //noop
    }
}
