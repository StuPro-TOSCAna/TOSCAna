package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.core.transformation.TransformationContext;
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

/**
 Class for checking the models relationships
 */
public class CheckModelRelationshipVisitor implements StrictRelationshipVisitor {

    private final Logger logger;
    private Graph<RootNode, RootRelationship> topology;

    /**
     Create a <tt>CheckModelRelationshipVisitor</tt> to check a models relationships
     Only hostedOn and connectsTo from a WebApplication to a MysqlDatabase are supported

     @param context TransformationContext to extract topology and logger
     */
    public CheckModelRelationshipVisitor(TransformationContext context) {
        this.logger = context.getLogger(getClass());
        this.topology = context.getModel().getTopology();
    }

    @Override
    public void visit(ConnectsTo relation) {
        RootNode source = topology.getEdgeSource(relation);
        RootNode target = topology.getEdgeTarget(relation);
        if (!(source instanceof WebApplication && target instanceof MysqlDatabase)) {
            throw new UnsupportedTypeException("ConnectsTo relationship from source: " + source + " to target: " +
                target + " not supported.");
        }
    }

    @Override
    public void visit(HostedOn relation) {
        // noop
    }
}
