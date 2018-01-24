package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.security.SecureRandom;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;

public class PrepareModelNodeVisitor implements NodeVisitor {
    
    private final Logger logger;
    private static final int minPWLength = 8;
    private static final String DEFAULT_USER = "root";
    private static final Integer DEFAULT_PORT = 3306;
    
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
        //if certain values aren't given, fill them
        if (node.getPassword().isPresent()) {
            String password = node.getPassword().get();
            if (password.length() < minPWLength) {
                logger.warn("Database password to short, creating new random password");
                node.setPassword(randomString(minPWLength));
            }
        } else {
            logger.warn("No database password given, creating new random password");
            node.setPassword(randomString(minPWLength));
        }
        if (!node.getUser().isPresent()) {
            logger.warn("User not set, setting to default");
            node.setUser(DEFAULT_USER);
        }
        if (!node.getPort().isPresent()) {
            logger.warn("Database port not set, setting to default");
            node.setPort(DEFAULT_PORT);
        }
    }

    private String toAlphanumerical(String inp) {
        return inp.replaceAll("[^A-Za-z0-9]", "");
    }

    private String randomString(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, new SecureRandom());
    }
}
