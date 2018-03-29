package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;
import org.opentosca.toscana.plugins.cloudformation.util.StackUtils;

import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.UserData;
import org.jgrapht.Graph;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle.toAlphanumerical;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.SECURITY_GROUP;

/**
 Abstract class that is the base to all visitors ({@link org.opentosca.toscana.model.visitor.NodeVisitor}, {@link
org.opentosca.toscana.model.visitor.StrictNodeVisitor}, {@link org.opentosca.toscana.model.visitor.RelationshipVisitor}),
 {@link org.opentosca.toscana.model.visitor.StrictRelationshipVisitor} for the CloudFormation plugin.
 <br>
 It provides general function, a logger, the topology of the {@link org.opentosca.toscana.model.EffectiveModel} and the
 {@link CloudFormationModule}.
 */
public abstract class CloudFormationVisitor {

    protected final Logger logger;
    /**
     The topology of the {@link org.opentosca.toscana.model.EffectiveModel} used in the transformation.
     */
    protected Graph<RootNode, RootRelationship> topology;
    /**
     The CloudFormationModule that holds all the information during the transformation.
     */
    protected CloudFormationModule cfnModule;

    /**
     Standard constructor.
     <br>
     Sets up the topology and the logger.

     @param context the {@link TransformationContext} this visitor will run on
     */
    public CloudFormationVisitor(TransformationContext context) {
        this.logger = context.getLogger(getClass());
        this.topology = context.getModel().getTopology();
    }

    /**
     Extended constructor if also a {@link CloudFormationModule} is given.
     <br>
     Executes the {@link #CloudFormationVisitor(TransformationContext)} standard constructor} and sets up the
     {@link CloudFormationModule}.

     @param context   the {@link TransformationContext} this visitor will run on
     @param cfnModule the {@link CloudFormationModule} this visitor will use
     */
    public CloudFormationVisitor(TransformationContext context, CloudFormationModule cfnModule) {
        this(context);
        this.cfnModule = cfnModule;
    }

    /**
     Gets the {@link Compute} node this {@link WebApplication} is ultimately hosted on

     @param webApplication the {@link WebApplication} to find the host for
     @return the underlying {@link Compute} node
     */
    protected static Compute getCompute(WebApplication webApplication) {
        WebServer webServer = webApplication.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("WebApplication is missing WebServer")
        );
        return getCompute(webServer);
    }

    /**
     Gets the {@link Compute} node this {@link MysqlDatabase} is ultimately hosted on

     @param database the {@link MysqlDatabase} to find the host for
     @return the underlying {@link Compute} node
     */
    protected static Compute getCompute(Database database) {
        Dbms dbms = database.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("MysqlDatabase is missing Dbms")
        );
        return dbms.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("Dbms is missing Compute")
        );
    }

    /**
     Gets the {@link Compute} node this {@link WebServer} is ultimately hosted on

     @param webServer the {@link WebServer} to find the host for
     @return the underlying {@link Compute} node
     */
    protected static Compute getCompute(WebServer webServer) {
        return webServer.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("WebServer is missing Compute")
        );
    }

    /**
     Gets the {@link Compute} node this {@link Dbms} is ultimately hosted on

     @param dbms the {@link Dbms} to find the host for
     @return the underlying {@link Compute} node
     */
    protected static Compute getCompute(Dbms dbms) {
        return dbms.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("Dbms is missing Compute")
        );
    }

    /**
     Gets the {@link Compute} node this {@link Nodejs} is ultimately hosted on.

     @param nodejs the {@link Nodejs} to find the host for
     @return the underlying {@link Compute} node
     */
    protected static Compute getCompute(Nodejs nodejs) {
        return nodejs.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("Nodejs is missing Compute")
        );
    }

    /**
     Gets the {@link Compute} node this {@link JavaApplication} is ultimately hosted on.

     @param javaApplication the JavaApplication to find the host for
     @return host of given JavaApplication
     */
    protected static Compute getCompute(JavaApplication javaApplication) {
        JavaRuntime javaRuntime = javaApplication.getJreHost().getNode().orElseThrow(
            () -> new IllegalStateException("JavaApplication is missing JavaRuntime")
        );
        return javaRuntime.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("JavaRuntime is missing Compute")
        );
    }

    /**
     Gets the {@link JavaRuntime} node this {@link JavaApplication} is hosted on.

     @param javaApplication the JavaApplication to find the JavaRuntime for
     @return JavaRuntime the given JavaApplication is hosted on
     */
    protected static JavaRuntime getJavaRuntime(JavaApplication javaApplication) {
        return javaApplication.getJreHost().getNode().orElseThrow(
            () -> new IllegalStateException("JavaApplication is missing JavaRuntime")
        );
    }

    /**
     Gets a {@link Set} of {@link Compute} nodes hosting {@link WebApplication WebApplications} which in turn have a
     {@link org.opentosca.toscana.model.relation.ConnectsTo} relationship to {@code node}.
     <br>
     Searches all incoming edges to {@code node} for {@link WebApplication WebApplications}. Then adds the underlying
     {@link Compute} to a {@link Set}.

     @param node the node to search hosts of connected to {@link WebApplication WebApplications} for
     @return a {@link Set} of compute nodes that fulfill this requirement
     */
    protected Set<Compute> getHostsOfConnectedTo(RootNode node) {
        Set<Compute> connected = new HashSet<>();
        Set<RootRelationship> incomingEdges = topology.incomingEdgesOf(node);
        for (RootRelationship incomingEdge : incomingEdges) {
            RootNode source = topology.getEdgeSource(incomingEdge);
            if (source instanceof WebApplication) {
                WebApplication webApplication = (WebApplication) source;
                Compute compute = getCompute(webApplication);
                connected.add(compute);
            }
            if (source instanceof JavaApplication) {
                JavaApplication javaApplication = (JavaApplication) source;
                Compute compute = getCompute(javaApplication);
                connected.add(compute);
            }
        }
        return connected;
    }

    /**
     Creates a EC2 for a {@link MysqlDatabase} that runs an initial sql query.

     @param mysqlDatabase the {@link MysqlDatabase} this sql query should be run on
     @param sqlQuery      the sql query to run
     @return the instance name of the created EC2 Instance
     */
    protected String createSqlEc2(MysqlDatabase mysqlDatabase, String sqlQuery) {
        String ec2Name = toAlphanumerical(mysqlDatabase.getEntityName()) + "TmpSqlServer";
        SecurityGroup webServerSecurityGroup = cfnModule.resource(SecurityGroup.class,
            ec2Name + SECURITY_GROUP)
            .groupDescription("Temporary group for accessing mysqlDatabase" + toAlphanumerical(mysqlDatabase
                .getEntityName()) + " with SQLRequest");
        cfnModule.resource(Instance.class, ec2Name)
            .securityGroupIds(webServerSecurityGroup)
            .imageId("ami-79873901")
            .instanceType("t2.micro")
            .instanceInitiatedShutdownBehavior("terminate")
            .userData(new UserData(StackUtils.getUserDataDBConnFn(mysqlDatabase, sqlQuery)));
        return ec2Name;
    }

    /**
     Gets the ports from the {@link EndpointCapability EndpointCapabilities} if there are any.

     @param node the node to check
     @return list of ports that may be empty
     */
    protected List<Integer> getPortsFromEnpointCapability(RootNode node) {
        List<Integer> portList = new ArrayList<>();
        String nodeName = node.getEntityName();
        node.getCapabilities().forEach(e -> {
            try {
                if (e instanceof EndpointCapability && ((EndpointCapability) e).getPort().isPresent()) {
                    int port = ((EndpointCapability) e).getPort().get().port;
                    logger.debug("Marking '{}' as port to be opened for '{}'.", port, nodeName);
                    portList.add(port);
                }
            } catch (Exception ex) {
                logger.warn("Failed reading Port from node {}", nodeName, ex);
            }
        });
        return portList;
    }
}
