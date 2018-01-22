package org.opentosca.toscana.plugins.cloudformation.visitor;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import org.slf4j.Logger;

public class PrepareModelNodeVisitor implements NodeVisitor{
    
    private final Logger logger;
    
    public PrepareModelNodeVisitor(Logger logger) {
        this.logger = logger;    
    }
    
    @Override
    public void visit(Compute node) {
        logger.info("Prepare Compute node {}.", node.getEntityName());
    }

    @Override
    public void visit(MysqlDatabase node) {
        logger.info("Prepare MysqlDatabase node {}.", node.getEntityName());
    }

    private String toAlphanumerical(String inp) {
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }

}
