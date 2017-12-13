package org.opentosca.toscana.plugins.kubernetes.visitor.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.visitor.NodeVisitor;

/**
 A Simple visitor that collects all compute nodes that visit him
 */
public class ComputeNodeFindingVisitor implements NodeVisitor {

    private final Set<Compute> computeNodes = new HashSet<>();

    @Override
    public void visit(Compute node) {
        computeNodes.add(node);
    }

    public Set<Compute> getComputeNodes() {
        return Collections.unmodifiableSet(computeNodes);
    }
}
