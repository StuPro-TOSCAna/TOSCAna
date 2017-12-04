package org.opentosca.toscana.model;

import java.util.Set;

import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class EffectiveModel {

    private final Graph<RootNode, RootRelationship> topology =
        new DefaultDirectedGraph<RootNode, RootRelationship>(RootRelationship.class);

    public EffectiveModel(Set<RootNode> vertices) {
        vertices.forEach(node -> topology.addVertex(node));
        initEdges();
    }

    private void initEdges() {
        for (RootNode node : topology.vertexSet()) {
            for (Requirement requirement : node.getRequirements()) {
                for (Object o : requirement.getFulfillers()) {
                    RootNode fulfiller = (RootNode) o;
                    topology.addEdge(node, fulfiller, requirement.getRelationship());
                }
            }
        }
    }
    
    public Set<RootNode> getNodes() {
        return topology.vertexSet();
    }
    
}

