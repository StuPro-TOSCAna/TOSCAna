package org.opentosca.toscana.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class EffectiveModel {

    private final Graph<RootNode, RootRelationship> topology =
        new DefaultDirectedGraph<>(RootRelationship.class);

    private final Map<String, RootNode> nodeMap;

    private final Set<Property> inputs;

    public EffectiveModel(Map<String, RootNode> nodes, Set<Property> inputs) {
        this.inputs = inputs;
        nodeMap = nodes;
        nodes.forEach((name, node) -> topology.addVertex(node));
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

    public Map<String, RootNode> getNodeMap() {
        return Collections.unmodifiableMap(nodeMap);
    }

    public Graph<RootNode, RootRelationship> getTopology() {
        return topology;
    }

    public Set<Property> getInputs() {
        return inputs;
    }
}

