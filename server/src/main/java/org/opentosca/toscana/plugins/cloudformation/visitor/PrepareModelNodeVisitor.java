package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.security.SecureRandom;
import java.util.stream.Collectors;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import com.scaleset.cfbuilder.core.Fn;
import org.apache.commons.lang3.RandomStringUtils;
import org.jgrapht.Graph;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor.getCompute;

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
    public void visit(MysqlDatabase node) {
        logger.info("Prepare MysqlDatabase node '{}'.", node.getEntityName());
        //if certain values aren't given, fill them
        if (node.getPassword().isPresent()) {
            //password needs to be at least 8 characters long
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

        // check if Mysql is the only node hosted on his compute node
        Compute compute = getCompute(node);
        if (topology.incomingEdgesOf(compute)
            .stream()
            .filter(relation -> relation instanceof HostedOn)
            .collect(Collectors.toSet())
            .size() == 1) {
            // means our dbms is the only one hosted on this compute
            // means we can set the private address as reference the database endpoint
            String databaseEndpoint = Fn.fnGetAtt(toAlphanumerical(node.getEntityName()), AWS_ENDPOINT_REFERENCE)
                .toString(true);
            compute.setPrivateAddress(databaseEndpoint);
            compute.setPublicAddress(databaseEndpoint);
            logger.debug("Set private address and public address of '{}' to reference MysqlDatabase '{}'", compute.getEntityName(), node
                .getEntityName());
        }
    }

    /**
     Generates a random string that is also usable as a password

     @param count length of the to created string
     @return a random string of given length
     */
    private String randomString(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, new SecureRandom());
    }
}
