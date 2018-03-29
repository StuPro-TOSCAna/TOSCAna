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

    /**
     Finds the top level nodes in a given topology

     @param model        The effective model containing the topology that should get analyzed
     @param computeNodes The list of Compute nodes for which the top level nodes should get determined
     @param onValidNode  Callback method that gets called if the node that currently processed by the iterator is valid.
     It does not only get called for every top level node
     @return the set of top level nodes for the given list of Compute nodes
     */
    public static Set<RootNode> determineTopLevelNodes(
        EffectiveModel model,
        List<Compute> computeNodes,
        Consumer<RootNode> onValidNode
    ) {
        Set<RootNode> topLevelNodes = new HashSet<>();
        Graph<RootNode, RootRelationship> graph = model.getTopology();
        //Determine Top level nodes (of complete stacks) and completely explore parts that are linked to a compute nodes
        computeNodes.forEach(computeNode -> {
            LinkedList<RootNode> nodeStack = new LinkedList<>();
            nodeStack.add(computeNode);

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

    /**
     Converts a set of top level nodes into a list of NodeStacks
     @param model The model containing the topology to perform the operations with
     @param topLevelNodes The set of top level nodes for which the node stacks should be generated
     @param nodes a map containing all nodes in the topology (key is the node name), 
     the nodes are wrapped in the {@link KubernetesNodeContainer} class
     @return the resulting list of node stacks
     */
    public static List<NodeStack> buildTopologyStacks(
        EffectiveModel model,
        Set<RootNode> topLevelNodes,
        Map<String, KubernetesNodeContainer> nodes
    ) {
        Graph<RootNode, RootRelationship> graph = model.getTopology();

        LinkedList<NodeStack> stacks = new LinkedList<>();
        topLevelNodes.forEach(node -> {
            LinkedList<KubernetesNodeContainer> stack = new LinkedList<>();
            LinkedList<RootNode> nodeStack = new LinkedList<>();
            nodeStack.add(node);
            while (!nodeStack.isEmpty()) {
                RootNode currentNode = nodeStack.pop();
                stack.add(nodes.get(currentNode.getEntityName()));
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
