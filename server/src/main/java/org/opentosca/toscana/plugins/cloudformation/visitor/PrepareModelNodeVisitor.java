package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.security.SecureRandom;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import com.scaleset.cfbuilder.core.Fn;
import org.apache.commons.lang3.RandomStringUtils;
import org.jgrapht.Graph;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;

public class PrepareModelNodeVisitor implements NodeVisitor {

    protected static final String AWS_ENDPOINT_REFERENCE = "Endpoint.Address";
    private static final int minPWLength = 8;
    private static final String DEFAULT_USER = "root";
    private static final Integer DEFAULT_PORT = 3306;
    private final Logger logger;
    private Graph<RootNode, RootRelationship> topology;

    public PrepareModelNodeVisitor(Logger logger, Graph<RootNode, RootRelationship> topology) {
        this.logger = logger;
        this.topology = topology;
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

        // check if Mysql is only one hosted on compute node
        Dbms dbms = node.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("MysqlDatabase is missing Dbms")
        );
        Compute compute = dbms.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("Dbms is missing Compute")
        );
        if (topology.incomingEdgesOf(compute).size() == 1) {
            // means our dbms is the only one hosted on this compute
            // means we can set the private address as reference the database endpoint
            //TODO only set privateAddress or also publicAddress?
            compute.setPrivateAddress(Fn.fnGetAtt(toAlphanumerical(node.getEntityName()), AWS_ENDPOINT_REFERENCE)
                .toString(true));
            logger.debug("Set private Address of {} to reference MysqlDatabase {}", compute.getEntityName(), node
                .getEntityName());
        }
    }

    private String randomString(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, new SecureRandom());
    }
}
