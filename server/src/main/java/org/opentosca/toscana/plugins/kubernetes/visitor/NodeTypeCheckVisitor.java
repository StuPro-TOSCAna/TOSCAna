package org.opentosca.toscana.plugins.kubernetes.visitor;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerApplication;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;

public class NodeTypeCheckVisitor implements StrictNodeVisitor {
    @Override
    public void visit(Compute node) {
        //noop
    }

    @Override
    public void visit(ContainerApplication node) {
        //noop 
    }

    @Override
    public void visit(ContainerRuntime node) {
        //noop 
    }

    @Override
    public void visit(DockerApplication node) {
        //noop 
    }
}
