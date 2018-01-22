package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import org.jgrapht.Graph;
import org.slf4j.Logger;

public class PrepareModelRelationshipVisitor implements RelationshipVisitor {

    private final Logger logger;
    private Graph<RootNode, RootRelationship> topology;

    public PrepareModelRelationshipVisitor(Logger logger, Graph<RootNode, RootRelationship> topology) {
        this.logger = logger;
        this.topology = topology;
    }

    @Override
    public void visit(ConnectsTo relation) {
        logger.info("Prepare ConnectsTo relation {}.", relation.getEntityName());
        RootNode source = topology.getEdgeSource(relation);
        RootNode target = topology.getEdgeTarget(relation);
        if (target instanceof MysqlDatabase) {
            MysqlDatabase mysqlDatabase = (MysqlDatabase) target;
            mysqlDatabase.getHost().getFulfillers().iterator().next().getHost().getFulfillers().iterator().next()
                .setPrivateAddress("Test");
        }
    }
}
