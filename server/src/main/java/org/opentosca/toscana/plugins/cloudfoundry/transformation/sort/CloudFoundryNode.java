package org.opentosca.toscana.plugins.cloudfoundry.transformation.sort;

import org.opentosca.toscana.model.node.RootNode;

public class CloudFoundryNode {

    private final RootNode node;

    private boolean hasParentComputeNode = false;

    public CloudFoundryNode(RootNode node) {
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
