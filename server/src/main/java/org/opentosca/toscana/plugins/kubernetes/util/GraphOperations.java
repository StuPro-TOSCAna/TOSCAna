package org.opentosca.toscana.plugins.kubernetes.util;

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

public class GraphOperations {

    public static Set<RootNode> determineTopLevelNodes(
        EffectiveModel model,
        List<Compute> computeNodes,
        Consumer<RootNode> onValidNode
    ) {
        Set<RootNode> topLevelNodes = new HashSet<>();
        Graph<RootNode, RootRelationship> graph = model.getTopology();
        //Determine Top level nodes (of complete stacks) and completely explore parts that are linked to a compute nodes
        computeNodes.forEach(e -> {
            LinkedList<RootNode> nodeStack = new LinkedList<>();
            nodeStack.add(e);

            while (!nodeStack.isEmpty()) {
                RootNode currentNode = nodeStack.pop();
                Set<RootRelationship> edges = graph.edgesOf(currentNode);
                int hostChildren = 0;
                for (RootRelationship edge : edges) {
                    RootNode target = graph.getEdgeTarget(edge);
                    RootNode source = graph.getEdgeSource(edge);
                    if (target.equals(currentNode) && edge instanceof HostedOn) {
                        onValidNode.accept(source);
                        nodeStack.addLast(source);
                        hostChildren++;
                    }
                }
                if (hostChildren == 0) {
                    topLevelNodes.add(currentNode);
                }
            }
        });
        return topLevelNodes;
    }

    public static List<NodeStack> buildTopologyStacks(
        EffectiveModel model,
        Set<RootNode> topLevelNodes,
        Map<String, KubernetesNodeContainer> nodes
    ) {
        Graph<RootNode, RootRelationship> graph = model.getTopology();

        LinkedList<NodeStack> stacks = new LinkedList<>();
        topLevelNodes.forEach(n -> {
            LinkedList<KubernetesNodeContainer> stack = new LinkedList<>();
            LinkedList<RootNode> nodeStack = new LinkedList<>();
            nodeStack.add(n);
            while (!nodeStack.isEmpty()) {
                RootNode currentNode = nodeStack.pop();
                stack.add(nodes.get(currentNode.getNodeName()));
                Set<RootRelationship> edges = graph.edgesOf(currentNode);
                for (RootRelationship edge : edges) {
                    RootNode target = graph.getEdgeTarget(edge);
                    RootNode source = graph.getEdgeSource(edge);
                    if (source.equals(currentNode) && edge instanceof HostedOn) {
                        nodeStack.addLast(target);
                    }
                }
            }
            stacks.add(new NodeStack(stack));
        });
        return stacks;
    }
}
