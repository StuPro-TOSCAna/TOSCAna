package org.opentosca.toscana.plugins.kubernetes.model;

import java.util.Set;

import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.jgrapht.graph.DirectedMultigraph;

public class RelationshipGraph extends DirectedMultigraph<NodeStack, Requirement> {

    public RelationshipGraph(Set<NodeStack> stacks) {
        super(Requirement.class);
        stacks.forEach(this::addVertex);
        for (NodeStack stack : stacks) {
            stack.forEachNode(n -> {
                RootNode node = n.getNode();
                node.getRequirements().forEach(r -> {
                    //Checking for connects to relationship, Not using a visitor yet,
                    //because we only care about connects to relationships for now
                    if (r.getRelationship().isPresent() && r.getRelationship().get() instanceof ConnectsTo) {
                        Set fulfillers = r.getFulfillers();
                        if (fulfillers == null || fulfillers.size() != 1) {
                            throw new TransformationFailureException(r.getClass().getName() + " of the node " +
                                node.getEntityName() + " Does not meet fulfiler Requirement (Has to be exactly 1)." +
                                " Fulfiller count " + (fulfillers == null ? -1 : fulfillers.size()));
                        }
                        RootNode fulfiller = (RootNode) fulfillers.stream().findFirst().get();
                        for (NodeStack s : stacks) {
                            if (s.hasNode(fulfiller.getEntityName())) {
                                this.addEdge(stack, s, r);
                            }
                        }
                    }
                });
            });
        }
    }
}
