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

/**
 Represents a Pod,
 We Consider a Pod to be a Set of Node Stacks that have the same Compute Node
 */
public class Pod {
    
    /**
     The List of NodeStacks contained by this Pod
     */
    private List<NodeStack> containers;

    /**
     The Set of Ports Exposed by the Pod
     */
    private Set<Port> ports;
    /**
     The Common Compute Node
     */
    private Compute computeNode;
    /**
     The Amount of instances that should be Created by Kubernetes when deploying the result
     */
    private int replicaCount = 1;

    public Pod(List<NodeStack> containers, Compute compute) {
        this.ports = new HashSet<>();
        this.containers = containers;
        this.computeNode = compute;
        updatePorts();
        computeReplicaCount();
    }

    /**
     Looks at the Compute node and determines the Replica Count
     <p>
     We follow the following Procedure:
     If the Default Instance Count is given it will be used as <code>replicaCount</code>
     If the Default Instance Count is not given we try to use the Maximum,
     if that is also not present (set to unbounded) we use the minimum
     */
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
        // We need to check if the Ports have been loaded, because the Pods objects get created before
        // we find out what ports get exposed by each NodeStack
        updatePorts();
        return ports;
    }

    /**
     Looks at all node stacks and adds the Exposed Ports if they are not present
     */
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

    /**
     @param stacks The List of NodeStacks to group in Pods
     @return a List Containing the given NodeStacks grouped in Pods
     */
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
