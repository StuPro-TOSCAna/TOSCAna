package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import com.scaleset.cfbuilder.core.Fn;
import org.jgrapht.Graph;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor.getCompute;
import static org.opentosca.toscana.plugins.cloudformation.visitor.PrepareModelNodeVisitor.AWS_ENDPOINT_REFERENCE;

public class PrepareModelRelationshipVisitor implements RelationshipVisitor {

    private final Logger logger;
    private Graph<RootNode, RootRelationship> topology;

    public PrepareModelRelationshipVisitor(Logger logger, Graph<RootNode, RootRelationship> topology) {
        this.logger = logger;
        this.topology = topology;
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
                String databaseEndpoint = Fn.fnGetAtt(toAlphanumerical(mysqlDatabase.getEntityName()),
                    AWS_ENDPOINT_REFERENCE).toString(true);
                computeMysqlDatabase.setPrivateAddress(databaseEndpoint);
                computeMysqlDatabase.setPublicAddress(databaseEndpoint);
                logger.debug("Set private address and public address of '{}' to reference MysqlDatabase '{}'",
                    computeMysqlDatabase.getEntityName(), mysqlDatabase.getEntityName());
            }
        }
    }
}
