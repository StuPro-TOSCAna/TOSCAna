package org.opentosca.toscana.plugins.kubernetes.model.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;

public class Pod {
    private List<NodeStack> containers;

    private Set<Port> ports;
    private Compute computeNode;
    private int replicaCount = 1;

    public Pod(List<NodeStack> containers, Compute compute) {
        this.ports = new HashSet<>();
        this.containers = containers;
        this.computeNode = compute;
        updatePorts();
        computeReplicaCount();
    }

    private void computeReplicaCount() {
        if (computeNode.getScalable().getDefaultInstances().isPresent()) {
            this.replicaCount = computeNode.getScalable().getDefaultInstances().get();
        } else {
            if (computeNode.getScalable().getMaxInstances() == Integer.MAX_VALUE) {
                this.replicaCount = computeNode.getScalable().getMinInstances();
            } else {
                this.replicaCount = computeNode.getScalable().getMaxInstances();
            }
        }
    }

    public List<NodeStack> getContainers() {
        return containers;
    }

    public Set<Port> getPorts() {
        updatePorts();
        return ports;
    }

    private void updatePorts() {
        if (ports.isEmpty()) {
            this.containers.forEach(e -> this.ports.addAll(e.getOpenPorts()));
        }
    }

    public String getName() {
        //TODO (LOW PRIORITY) find better mechanism to name the pod
        return containers.get(0).getCleanStackName();
    }

    public String getServiceName() {
        return getName() + "-service";
    }

    public String getDeploymentName() {
        return getName() + "-deployment";
    }

    public Compute getComputeNode() {
        return computeNode;
    }

    public int getReplicaCount() {
        return replicaCount;
    }

    public static List<Pod> getPods(Collection<NodeStack> stacks) {
        //Group Node Stacks
        Map<Compute, List<NodeStack>> stackMap = new HashMap<>();
        for (NodeStack stack : stacks) {
            Compute computeNode = stack.getComputeNode();
            stackMap.computeIfAbsent(computeNode, k -> new ArrayList<>());
            stackMap.get(computeNode).add(stack);
        }

        //Convert NodeStacks to Pods
        List<Pod> pods = new ArrayList<>();
        stackMap.forEach((k, v) -> pods.add(new Pod(v, k)));
        return pods;
    }
}
