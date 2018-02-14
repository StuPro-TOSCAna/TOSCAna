package org.opentosca.toscana.plugins.cloudfoundry.visitor;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;

/**
 The nodes in this class are already supported by the CloudFoundry plugin
 */
public class NodeTypeCheck implements StrictNodeVisitor {

    @Override
    public void visit(Apache node) {
        //no action here
    }

    @Override
    public void visit(Compute node) {
        //no action here
    }

    @Override
    public void visit(MysqlDatabase node) {
        //no action here
    }

    @Override
    public void visit(MysqlDbms node) {
        //no action here
    }

    @Override
    public void visit(WebApplication node) {
        //no action here
    }
}
