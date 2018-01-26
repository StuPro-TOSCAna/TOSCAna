package org.opentosca.toscana.plugins.cloudfoundry.transformation.sort;

import java.util.List;
import java.util.function.Consumer;

import org.opentosca.toscana.model.node.Compute;

public class CloudFoundryStack {

    private final List<CloudFoundryNode> nodeStacks;

    public CloudFoundryStack(List<CloudFoundryNode> nodeStacks) {
        this.nodeStacks = nodeStacks;
    }

    public void forEachNode(Consumer<CloudFoundryNode> consumer) {
        for (CloudFoundryNode nodeStack : nodeStacks) {
            consumer.accept(nodeStack);
        }
    }

    public int getNodeCount() {
        return nodeStacks.size();
    }

    public List<CloudFoundryNode> getNodes() {
        return nodeStacks;
    }

    public CloudFoundryNode getRootNode() {
        return nodeStacks.get(nodeStacks.size() - 1);
    }

    public String getStackName() {
        return nodeStacks.get(0).getNode().getEntityName();
    }

    public String getCleanStackName() {
        return getStackName().replaceAll("_", "-");
    }

    public Compute getComputeNode() {
        return (Compute) this.nodeStacks.stream().filter(e -> e.getNode() instanceof Compute)
            .findFirst().orElseThrow(IllegalArgumentException::new).getNode();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Node Stack (name = ").append(getStackName()).append(" , topology = ");

        for (CloudFoundryNode node : nodeStacks) {
            string.append(node.getNode().getEntityName());
            if (!node.getNode().equals(getRootNode().getNode())) {
                string.append(" -> ");
            }
        }
        string.append(")");
        return string.toString();
    }
}
