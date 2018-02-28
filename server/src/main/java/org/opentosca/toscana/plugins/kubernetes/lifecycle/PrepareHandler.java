package org.opentosca.toscana.plugins.kubernetes.lifecycle;

import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.model.transform.Pod;
import org.opentosca.toscana.plugins.kubernetes.util.KubernetesNodeContainer;
import org.opentosca.toscana.plugins.kubernetes.visitor.util.ComputeNodeFindingVisitor;

import static org.opentosca.toscana.plugins.kubernetes.util.GraphOperations.buildTopologyStacks;
import static org.opentosca.toscana.plugins.kubernetes.util.GraphOperations.determineTopLevelNodes;

class PrepareHandler extends LifecycleHandler {

    PrepareHandler(KubernetesLifecycle lifecycle) {
        super(lifecycle);
    }

    public void prepare() {
        ComputeNodeFindingVisitor computeFinder = findComputeNodes();
        Set<RootNode> topLevelNodes = findTopLevelNodes(computeFinder);
        groupStacks(topLevelNodes);
        updateAddresses();
    }

    private void groupStacks(Set<RootNode> topLevelNodes) {
        logger.debug("Building complete Topology stacks");
        lifecycle.stacks.addAll(buildTopologyStacks(lifecycle.model, topLevelNodes, lifecycle.nodes));

        logger.debug("Grouping Stacks in Pods");
        lifecycle.pods = Pod.getPods(lifecycle.stacks);
    }

    private void updateAddresses() {
        logger.debug("Setting Private and Public addresses of Compute Nodes");
        lifecycle.pods.forEach(e -> {
            e.getComputeNode().setPrivateAddress(e.getServiceName());
            e.getComputeNode().setPublicAddress(e.getServiceName());
        });
    }

    private Set<RootNode> findTopLevelNodes(ComputeNodeFindingVisitor computeFinder) {
        logger.debug("Finding top Level Nodes");
        return determineTopLevelNodes(
            lifecycle.getContext().getModel(),
            computeFinder.getComputeNodes().stream().map(Compute.class::cast).collect(Collectors.toList()),
            e -> lifecycle.nodes.get(e.getEntityName()).activateParentComputeNode()
        );
    }

    private ComputeNodeFindingVisitor findComputeNodes() {
        logger.debug("Collecting Compute Nodes in topology");
        ComputeNodeFindingVisitor computeFinder = new ComputeNodeFindingVisitor();
        lifecycle.model.getNodes().forEach(e -> {
            e.accept(computeFinder);
            KubernetesNodeContainer container = new KubernetesNodeContainer(e);
            lifecycle.nodes.put(e.getEntityName(), container);
        });
        computeFinder.getComputeNodes().forEach(e -> lifecycle.computeNodes.add(lifecycle.nodes.get(e.getEntityName())));
        return computeFinder;
    }
}
