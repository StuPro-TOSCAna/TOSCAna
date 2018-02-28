package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import org.jgrapht.Graph;
import org.slf4j.Logger;

public abstract class CloudFormationVisitorExtension {

    protected final Logger logger;
    protected Graph<RootNode, RootRelationship> topology;
    protected CloudFormationModule cfnModule;

    public CloudFormationVisitorExtension(TransformationContext context) {
        this.logger = context.getLogger(getClass());
        this.topology = context.getModel().getTopology();
    }

    public CloudFormationVisitorExtension(TransformationContext context, CloudFormationModule cfnModule) {
        this(context);
        this.cfnModule = cfnModule;
    }

    /**
     Get the Compute node this webApplication is ultimately hosted on

     @param webApplication the webApplication to find the host for
     @return the underlying Compute node
     */
    protected static Compute getCompute(WebApplication webApplication) {
        WebServer webServer = webApplication.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("WebApplication is missing WebServer")
        );
        return getCompute(webServer);
    }

    /**
     Get the Compute node this mysqlDatabase is ultimately hosted on

     @param database the mysqlDatabase to find the host for
     @return the underlying Compute node
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
     Get the Compute node this webServer is ultimately hosted on

     @param webServer the webServer to find the host for
     @return the underlying Compute node
     */
    protected static Compute getCompute(WebServer webServer) {
        return webServer.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("WebServer is missing Compute")
        );
    }

    /**
     Get the Compute node this dbms is ultimately hosted on

     @param dbms the Dbms to find the host for
     @return the underlying Compute node
     */
    protected static Compute getCompute(Dbms dbms) {
        return dbms.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("Dbms is missing Compute")
        );
    }

    /**
     Get the Compute node this nodejs is ultimately hosted on.

     @param nodejs the host of which should be returned
     @return host of given Nodejs
     */
    protected static Compute getCompute(Nodejs nodejs) {
        return nodejs.getHost().getNode().orElseThrow(
            () -> new IllegalStateException("Nodejs is missing Compute")
        );
    }

    /**
     Get the Compute node this JavaApplication is ultimately hosted on.

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
        }
        return connected;
    }
}
