package org.opentosca.toscana.plugins.cloudfoundry.transformation.sort;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;

import org.jgrapht.Graph;

public class GraphSort {

    private final EffectiveModel model;
    private final Graph<RootNode, RootRelationship> graph;

    public GraphSort(EffectiveModel effectiveModel) {
        this.model = effectiveModel;
        graph = model.getTopology();
    }

    public Set<RootNode> getTopLevelNode(List<Compute> computeNodes, Consumer<RootNode> onValidNode) {
        Set<RootNode> topLevelNodes = new HashSet<>();

        for (Compute compute : computeNodes) {
            LinkedList<RootNode> nodeStack = new LinkedList<>();
            nodeStack.add(compute);

            while (!nodeStack.isEmpty()) {
                RootNode currentNode = nodeStack.pop();
                Set<RootRelationship> edges = graph.edgesOf(currentNode);
                int hostChildren = 0;

                for (RootRelationship edge : edges) {
                    RootNode target = graph.getEdgeTarget(edge);
                    RootNode source = graph.getEdgeSource(edge);

                    if (edge instanceof HostedOn && target.equals(currentNode)) {
                        onValidNode.accept(source);
                        nodeStack.addLast(source);
                        hostChildren++;
                    }
                }
                if (hostChildren == 0) {
                    topLevelNodes.add(currentNode);
                }
            }
        }
        return topLevelNodes;
    }

    public List<CloudFoundryStack> buildStacks(Set<RootNode> topLevelNodes, Map<String, CloudFoundryNode> nodes) {
        LinkedList<CloudFoundryStack> stacks = new LinkedList<>();

        for (RootNode node : topLevelNodes) {
            LinkedList<CloudFoundryNode> stack = new LinkedList<>();
            LinkedList<RootNode> nodeStack = new LinkedList<>();
            nodeStack.add(node);

            while (!nodeStack.isEmpty()) {
                RootNode currentNode = nodeStack.pop();
                stack.add(nodes.get(currentNode.getNodeName()));
                Set<RootRelationship> edges = graph.edgesOf(currentNode);

                for (RootRelationship edge : edges) {
                    RootNode target = graph.getEdgeTarget(edge);
                    RootNode source = graph.getEdgeSource(edge);

                    if (edge instanceof HostedOn && source.equals(currentNode)) {
                        nodeStack.addLast(target);
                    }
                }
            }
            stacks.add(new CloudFoundryStack(stack));
        }
        return stacks;
    }
}
