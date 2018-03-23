package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.core.Fn;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.visitor.PrepareModelNodeVisitor.AWS_ENDPOINT_REFERENCE;

/**
 Class for preparing a models relationships.
 */
public class PrepareModelRelationshipVisitor extends CloudFormationVisitor implements RelationshipVisitor {

    /**
     Standard constructor.
     <br>
     Creates a <tt>PrepareModelRelationshipVisitor</tt> to prepare a models relationships.

     @param context   {@link TransformationContext} to extract the topology and a logger
     @param cfnModule {@link CloudFormationModule} to modify
     */
    public PrepareModelRelationshipVisitor(TransformationContext context, CloudFormationModule cfnModule) {
        super(context, cfnModule);
    }

    /**
     Sets the endpoint of a {@link Compute} node to reference the {@link MysqlDatabase} endpoint if both the
     {@link WebApplication} and the {@link MysqlDatabase} this connection is between are hosted on that {@link Compute}
     node.

     @param relation the {@link ConnectsTo} relationship to visit
     */
    @Override
    public void visit(ConnectsTo relation) {
        RootNode source = topology.getEdgeSource(relation);
        RootNode target = topology.getEdgeTarget(relation);
        if (source instanceof WebApplication && target instanceof MysqlDatabase) {
            MysqlDatabase mysqlDatabase = (MysqlDatabase) target;
            WebApplication webApplication = (WebApplication) source;
            // if they are hosted on the same compute --> we can set the compute private address to
            Compute computeMysqlDatabase = getCompute(mysqlDatabase);
            Compute computeWebApplication = getCompute(webApplication);
            if (computeMysqlDatabase.equals(computeWebApplication)) {
                // means we can set the private address as reference to the database endpoint
                Fn databaseEndpointFn = Fn.fnGetAtt(toAlphanumerical(mysqlDatabase.getEntityName()),
                    AWS_ENDPOINT_REFERENCE);
                String databaseEndpoint = databaseEndpointFn.toString(true);
                cfnModule.putFn(databaseEndpoint, databaseEndpointFn);
                computeMysqlDatabase.setPrivateAddress(databaseEndpoint);
                computeMysqlDatabase.setPublicAddress(databaseEndpoint);
                logger.debug("Set private address and public address of '{}' to reference MysqlDatabase '{}'",
                    computeMysqlDatabase.getEntityName(), mysqlDatabase.getEntityName());
            } else {
                logger.debug("Cannot safely set private/public address of '{}'", computeMysqlDatabase.getEntityName());
            }
        } else {
            logger.debug("Drop relationship, because it is not supported");
        }
    }
}
