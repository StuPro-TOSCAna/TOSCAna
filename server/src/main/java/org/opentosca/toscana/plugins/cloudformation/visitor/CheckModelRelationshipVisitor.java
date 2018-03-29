package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.StrictRelationshipVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

/**
 Checks the model's relationships whether they are supported or not.
 <br>
 The visitor implements the {@link StrictRelationshipVisitor} interface which means every method that is not overridden
 will throw an {@link UnsupportedTypeException}. These types are not supported.
 */
public class CheckModelRelationshipVisitor extends CloudFormationVisitor implements StrictRelationshipVisitor {

    /**
     Creates a <tt>CheckModelRelationshipVisitor</tt> to check a model's relationships.
     Only hostedOn and connectsTo from a WebApplication to a Database or MysqlDatabase are supported.

     @param context {@link TransformationContext} to extract the topology and a logger
     */
    public CheckModelRelationshipVisitor(TransformationContext context) {
        super(context);
    }

    /**
     {@link ConnectsTo} relationship is supported.
     <br>
     Only connections from a {@link WebApplication} to a {@link Database} are supported.
     */
    @Override
    public void visit(ConnectsTo relation) {
        RootNode source = topology.getEdgeSource(relation);
        RootNode target = topology.getEdgeTarget(relation);
        if (!((source instanceof WebApplication || source instanceof JavaApplication) && target instanceof Database)) {
            throw new UnsupportedTypeException("ConnectsTo relationship from source: " + source + " to target: " +
                target + " not supported.");
        }
    }

    /**
     {@link HostedOn} relationship is supported.
     */
    @Override
    public void visit(HostedOn relation) {
        // noop
    }
}
