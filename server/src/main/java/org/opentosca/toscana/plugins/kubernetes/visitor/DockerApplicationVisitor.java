package org.opentosca.toscana.plugins.kubernetes.visitor;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.visitor.NodeVisitor;

public class DockerApplicationVisitor implements NodeVisitor {
    private List<DockerApplication> stack;

    public DockerApplicationVisitor() {
        stack = new ArrayList<>();
    }

    @Override
    public void visit(DockerApplication node) {
        stack.add(node);
    }

    public List<DockerApplication> getStack() {
        return stack;
    }
}
