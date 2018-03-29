package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.core.Fn;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.visitor.PrepareModelNodeVisitor.AWS_ENDPOINT_REFERENCE;

/**
 Class for preparing a models relationships
 */
public class PrepareModelRelationshipVisitor extends CloudFormationVisitorExtension implements RelationshipVisitor {

    /**
     Create a <tt>PrepareModelRelationshipVisitor</tt> to prepare a models relationships.

     @param context TransformationContext to extract topology and logger
     */
    public PrepareModelRelationshipVisitor(TransformationContext context, CloudFormationModule cfnModule) {
        super(context, cfnModule);
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootNode source = topology.getEdgeSource(relation);
        RootNode target = topology.getEdgeTarget(relation);
        if ((source instanceof WebApplication || source instanceof JavaApplication) && target instanceof MysqlDatabase) {
            MysqlDatabase mysqlDatabase = (MysqlDatabase) target;
            Compute computeMysqlDatabase = getCompute(mysqlDatabase);
            Compute sourceCompute = null;
            if (source instanceof WebApplication) {
                WebApplication webApplication = (WebApplication) source;
                sourceCompute = getCompute(webApplication);
            } else {
                //source has to be JavaApplication
                JavaApplication javaApplication = (JavaApplication) source;
                sourceCompute = getCompute(javaApplication);
            }
            // if they are hosted on the same compute --> we can set the compute private address to
            if (computeMysqlDatabase.equals(sourceCompute)) {
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
