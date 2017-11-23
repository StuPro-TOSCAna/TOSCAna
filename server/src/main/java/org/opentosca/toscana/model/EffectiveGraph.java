package org.opentosca.toscana.model;

import java.util.Set;

import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;

import org.jgrapht.graph.DefaultDirectedGraph;

public class EffectiveGraph extends DefaultDirectedGraph<RootNode, RootRelationship> {

    public EffectiveGraph(Set<RootNode> vertices) {
        super(RootRelationship.class);
        vertices.forEach(node -> addVertex(node));
        initEdges();
    }

    private void initEdges() {
        for (RootNode node : vertexSet()) {
            for (Requirement requirement : node.getRequirements()) {
                for (Object o : requirement.getFulfillers()) {
                    RootNode fulfiller = (RootNode) o;
                    addEdge(node, fulfiller, requirement.getRelationship());
                }
            }
        }
    }
}

