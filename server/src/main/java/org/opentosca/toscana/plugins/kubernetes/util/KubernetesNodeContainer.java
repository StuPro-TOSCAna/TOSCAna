package org.opentosca.toscana.plugins.kubernetes.util;

import org.opentosca.toscana.model.node.RootNode;

/**
 This container class stores the data about this node aquired by the kubernetes plugin
 */
public class KubernetesNodeContainer {

    private final RootNode node;

    private boolean hasParentComputeNode = false;

    public KubernetesNodeContainer(RootNode node) {
        this.node = node;
    }

    public RootNode getNode() {
        return node;
    }

    public void activateParentComputeNode() {
        hasParentComputeNode = true;
    }

    public boolean hasParentComputeNode() {
        return hasParentComputeNode;
    }
}
