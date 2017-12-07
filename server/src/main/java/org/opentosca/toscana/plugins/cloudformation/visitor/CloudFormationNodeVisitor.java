package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;

public class CloudFormationNodeVisitor implements StrictNodeVisitor {
    
    @Override
    public void visit(Compute node) {
        //noop
    }
    
    @Override
    public void visit(MysqlDatabase node) {
        //noop
    }
    
    @Override
    public void visit(MysqlDbms node) {
        //noop
    }

    @Override
    public void visit(Apache node) {
        // noop
    }

    @Override
    public void visit(WebApplication node) {
        //noop
    }
    
}
