package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.StrictRelationshipVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

import org.jgrapht.Graph;
import org.slf4j.Logger;

public class CheckModelRelationshipVisitor implements StrictRelationshipVisitor {

    private final Logger logger;
    private Graph<RootNode, RootRelationship> topology;

    public CheckModelRelationshipVisitor(Logger logger, Graph<RootNode, RootRelationship> topology) {
        this.logger = logger;
        this.topology = topology;
    }

    @Override
    public void visit(ConnectsTo relation) {
        logger.info("Check ConnectsTo relation '{}'.", relation.getEntityName());
        RootNode source = topology.getEdgeSource(relation);
        RootNode target = topology.getEdgeTarget(relation);
        if (!(source instanceof WebApplication && target instanceof MysqlDatabase)) {
            throw new UnsupportedTypeException("ConnectsTo relationship from source: " + source + " to target: " +
                target + " not supported.");
        }
    }

    @Override
    public void visit(HostedOn relation) {
        logger.info("Check HostedOn relation '{}'.", relation.getEntityName());
    }
}
