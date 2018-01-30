package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

import org.slf4j.Logger;

public class CheckModelNodeVisitor implements StrictNodeVisitor {

    private final Logger logger;

    public CheckModelNodeVisitor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void visit(Apache node) {
        logger.info("Check Apache node '{}'.", node.getEntityName());
    }

    @Override
    public void visit(Compute node) {
        logger.info("Check Compute node '{}'.", node.getEntityName());
        List<OsCapability.Type> supportedTypes = new ArrayList<>();
        supportedTypes.add(OsCapability.Type.LINUX);
        //might grow but for now only linux
        List<OsCapability.Distribution> supportedDistributions = new ArrayList<>();
        supportedDistributions.add(OsCapability.Distribution.UBUNTU);
        //might grow, but for now only ubuntu, maybe already work with others but not yet tested
        OsCapability osCapability = node.getOs();
        //check type
        if (osCapability.getType().isPresent()) {
            OsCapability.Type type = osCapability.getType().get();
            if (!supportedTypes.contains(type)) {
                throw new UnsupportedTypeException("OS Type " + type + " not supported.");
            }
        }
        //check distribution
        if (osCapability.getDistribution().isPresent()) {
            OsCapability.Distribution distribution = osCapability.getDistribution().get();
            if (!supportedDistributions.contains(distribution)) {
                throw new UnsupportedTypeException("OS distribution " + distribution + " not supported.");
            }
        }
    }

    @Override
    public void visit(MysqlDatabase node) {
        logger.info("Check MysqlDatabase node '{}'.", node.getEntityName());
    }

    @Override
    public void visit(MysqlDbms node) {
        logger.info("Check MysqlDbms node '{}'.", node.getEntityName());
    }

    @Override
    public void visit(WebApplication node) {
        logger.info("Check WebApplication node '{}'.", node.getEntityName());
    }
}
