package org.opentosca.toscana.plugins.kubernetes.visitor.check;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerApplication;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;

/**
 Overwrites for every supported NodeType the interfaces visit method.
 As result the UnsupportedTypeException is only thrown for unsupported NodeTypes
 */
public class NodeTypeCheckVisitor implements StrictNodeVisitor {
    @Override
    public void visit(Compute node) {
        //noop
    }

    @Override
    public void visit(WebApplication node) {
        //noop 
    }

    @Override
    public void visit(Nodejs node) {
        //noop 
    }

    @Override
    public void visit(Database node) {
        //noop 
    }

    @Override
    public void visit(Dbms node) {
        //noop 
    }

    @Override
    public void visit(Apache node) {
        //noop 
    }

    @Override
    public void visit(MysqlDbms node) {
        //noop 
    }

    @Override
    public void visit(MysqlDatabase node) {
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

    @Override
    public void visit(JavaRuntime node) {
        //noop
    }

    @Override
    public void visit(JavaApplication node) {
        //noop
    }
}
