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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFormationLifecycle extends AbstractLifecycle {
    private final EffectiveModel model;

    public CloudFormationLifecycle(TransformationContext context) throws IOException {
        super(context);
        model = context.getModel();
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
        logger.debug("Begin transformation to CloudFormation.");
        PluginFileAccess fileAccess = context.getPluginFileAccess();
        CloudFormationModule cfnModule = new CloudFormationModule(fileAccess);
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
            logger.debug("Transformation to CloudFormation successful.");
        } catch (Exception e) {
            logger.debug("Transformation to CloudFormation unsuccessful. Please check the StackTrace for more Info.");
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
//        TODO implement cleanup
    }
}
