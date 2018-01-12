package org.opentosca.toscana.plugins.kubernetes.model;

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
    
    public Pod(List<NodeStack> containers) {
        this.ports = new HashSet<>();
        this.containers = containers;
        
        this.containers.forEach(e -> this.ports.addAll(e.getOpenPorts()));
    }

    public List<NodeStack> getContainers() {
        return containers;
    }

    public Set<Port> getPorts() {
        return ports;
    }

    public String getName() {
        //TODO find better mechanism to name the pod
        return containers.get(0).getCleanStackName();
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
        stackMap.forEach((k, v) -> pods.add(new Pod(v)));
        return pods;
    }
}
