package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.security.SecureRandom;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.core.Fn;
import org.apache.commons.lang3.RandomStringUtils;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;

/**
 Prepares a model's nodes.
 */
public class PrepareModelNodeVisitor extends CloudFormationVisitor implements NodeVisitor {

    protected static final String AWS_ENDPOINT_REFERENCE = "Endpoint.Address";
    private static final String AWS_INSTANCE_PRIVATE_IP = "PrivateIp";
    private static final String AWS_INSTANCE_PUBLIC_IP = "PublicIp";
    private static final int minPWLength = 8;
    private static final String DEFAULT_DB_USER = "root";
    private static final int DEFAULT_DB_PORT = 3306;
    private static final int DEFAULT_WEBAPP_PORT = 80;

    /**
     Creates a <tt>PrepareModelNodeVisitor</tt> to prepare a models nodes.

     @param context   {@link TransformationContext} to extract the topology and a logger
     @param cfnModule {@link CloudFormationModule} to modify
     */
    public PrepareModelNodeVisitor(TransformationContext context, CloudFormationModule cfnModule) {
        super(context, cfnModule);
    }

    /**
     Sets values to default values that are not required by the {@link org.opentosca.toscana.model.EffectiveModel} but
     by
     CloudFormation.
     <br>
     Passwords need to have a length of 8. If it is shorter than that or no password is given at all, a random password
     is used instead.
     <br>
     {@link Compute} nodes that only host {@link MysqlDatabase} should not be transformed to EC2s because the
     {@link MysqlDatabase} will be an AWS RDS and won't need a host.

     @param node the {@link MysqlDatabase} node to visit
     */
    @Override
    public void visit(MysqlDatabase node) {
        //if certain values aren't given, fill them
        if (node.getPassword().isPresent()) {
            //password needs to be at least 8 characters long
            String password = node.getPassword().get();
            if (password.length() < minPWLength) {
                logger.warn("Database password too short, creating new random password");
                node.setPassword(randomString(minPWLength));
            }
        } else {
            logger.warn("No database password given, creating new random password");
            node.setPassword(randomString(minPWLength));
        }
        if (!node.getUser().isPresent()) {
            logger.warn("User not set, setting to default");
            node.setUser(DEFAULT_DB_USER);
        }
        if (!node.getPort().isPresent()) {
            logger.warn("Database port not set, setting to default");
            node.setPort(DEFAULT_DB_PORT);
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
            Fn databaseEndpointFn = Fn.fnGetAtt(toAlphanumerical(node.getEntityName()), AWS_ENDPOINT_REFERENCE);
            String databaseEndpoint = databaseEndpointFn.toString(true);
            cfnModule.putFn(databaseEndpoint, databaseEndpointFn);
            compute.setPrivateAddress(databaseEndpoint);
            compute.setPublicAddress(databaseEndpoint);
            logger.debug("Set private address and public address of '{}' to reference MysqlDatabase '{}'",
                compute.getEntityName(), node.getEntityName());
            //also the underlying compute should not get mapped to an ec2
            cfnModule.removeComputeToEc2(compute);
            logger.debug("Removing Compute '{}' to be transformed", compute.getEntityName());
        }
    }

    /**
     Marks this {@link Compute} node to be transformed to an EC2 and sets the private and public address.

     @param node the {@link Compute} node to visit
     */
    @Override
    public void visit(Compute node) {
        // compute nodes only get transformed if they are present in this map
        cfnModule.addComputeToEc2(node);

        // Set private and public address of this EC2 instance
        String computeName = toAlphanumerical(node.getEntityName());
        Fn privateIpFn = Fn.fnGetAtt(computeName, AWS_INSTANCE_PRIVATE_IP);
        Fn publicIpFn = Fn.fnGetAtt(computeName, AWS_INSTANCE_PUBLIC_IP);
        String privateIpFnString = privateIpFn.toString(true);
        String publicIpFnString = publicIpFn.toString(true);
        cfnModule.putFn(privateIpFnString, privateIpFn);
        cfnModule.putFn(publicIpFnString, publicIpFn);
        node.setPrivateAddress(privateIpFnString);
        node.setPublicAddress(publicIpFnString);
    }

    /**
     Sets the application endpoint port of a this {@link WebApplication} to a default port if it is not yet set.

     @param node the {@link WebApplication} node to visit
     */
    @Override
    public void visit(WebApplication node) {
        //if port is not set, set to default 80
        if (!node.getAppEndpoint().getPort().isPresent()) {
            node.getAppEndpoint().setPort(new Port(DEFAULT_WEBAPP_PORT));
        }
    }

    @Override
    public void visit(JavaApplication node) {
        // check if JavaApplication is the only node hosted on its compute node
        Compute compute = getCompute(node);
        if (topology.incomingEdgesOf(compute)
            .stream()
            .filter(relation -> relation instanceof HostedOn)
            .collect(Collectors.toSet())
            .size() == 1) {
            //JavaApplication or JavaRuntime is the only node hosted on this compute
            cfnModule.removeComputeToEc2(compute);
            logger.debug("Removing Compute '{}' to be transformed", compute.getEntityName());
        }
    }

    /**
     Generates a random string that is also usable as a password.
     <br>
     Using {@link SecureRandom} so the {@link String} can be used as a password.

     @param count length of the {@link String}
     @return a random {@link String} of given length
     */
    private String randomString(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, new SecureRandom());
    }
}
