package org.opentosca.toscana.plugins.cloudfoundry.transformation.visitors;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;

/**
 Overwrites every supported Node Type, Exception thrown for unsupported Node Types
 */
public class NodeSupported implements StrictNodeVisitor {

    public NodeSupported() {
    }

    @Override
    public void visit(Compute node) {
    }

    @Override
    public void visit(MysqlDatabase node) {
    }

    @Override
    public void visit(MysqlDbms node) {
    }

    @Override
    public void visit(Apache node) {
    }

    @Override
    public void visit(WebApplication node) {
    }
}
