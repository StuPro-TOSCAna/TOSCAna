package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.core.Fn;
import org.jgrapht.Graph;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor.getCompute;
import static org.opentosca.toscana.plugins.cloudformation.visitor.PrepareModelNodeVisitor.AWS_ENDPOINT_REFERENCE;

/**
 Class for preparing a models relationships
 */
public class PrepareModelRelationshipVisitor implements RelationshipVisitor {

    private final Logger logger;
    private Graph<RootNode, RootRelationship> topology;
    private CloudFormationModule cfnModule;

    /**
     Create a <tt>PrepareModelRelationshipVisitor</tt> to prepare a models relationships.

     @param context TransformationContext to extract topology and logger
     */
    public PrepareModelRelationshipVisitor(TransformationContext context, CloudFormationModule cfnModule) {
        this.logger = context.getLogger(getClass());
        this.topology = context.getModel().getTopology();
        this.cfnModule = cfnModule;
    }

    @Override
    public void visit(ConnectsTo relation) {
        logger.info("Prepare ConnectsTo relation '{}'.", relation.getEntityName());
        RootNode source = topology.getEdgeSource(relation);
        RootNode target = topology.getEdgeTarget(relation);
        if (source instanceof WebApplication && target instanceof MysqlDatabase) {
            MysqlDatabase mysqlDatabase = (MysqlDatabase) target;
            WebApplication webApplication = (WebApplication) source;
            // if they are hosted on the same compute --> we can set the compute private address to
            Compute computeMysqlDatabase = getCompute(mysqlDatabase);
            Compute computeWebApplication = getCompute(webApplication);
            if (computeMysqlDatabase.equals(computeWebApplication)) {
                // means we can set the private address as reference the database endpoint
                Fn databaseEndpointFn = Fn.fnGetAtt(toAlphanumerical(mysqlDatabase.getEntityName()),
                    AWS_ENDPOINT_REFERENCE);
                String databaseEndpoint = databaseEndpointFn.toString(true);
                cfnModule.putFn(databaseEndpoint, databaseEndpointFn);
                computeMysqlDatabase.setPrivateAddress(databaseEndpoint);
                computeMysqlDatabase.setPublicAddress(databaseEndpoint);
                logger.debug("Set private address and public address of '{}' to reference MysqlDatabase '{}'",
                    computeMysqlDatabase.getEntityName(), mysqlDatabase.getEntityName());
            }
        }
    }
}