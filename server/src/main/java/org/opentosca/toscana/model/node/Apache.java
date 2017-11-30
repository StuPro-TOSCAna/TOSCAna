package org.opentosca.toscana.model.node;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;

/**
 (TOSCA Simple Profile in YAML Version 1.1, p. 222)
 */
@Data
public class Apache extends WebServer {

    @Builder
    protected Apache(String componentVersion,
                     Credential adminCredential,
                     ContainerCapability host,
                     EndpointCapability databaseEndpoint,
                     AdminEndpointCapability adminEndpoint,
                     String NodeName,
                     StandardLifecycle lifecycle,
                     String description) {
        super(componentVersion, adminCredential, host, databaseEndpoint, adminEndpoint, NodeName, lifecycle, description);
    }

    /**
     @param nodeName      {@link #nodeName}
     @param host          {@link #host}
     @param dataEndpoint  {@link #dataEndpoint}
     @param adminEndpoint {@link #adminEndpoint}
     */
    public static ApacheBuilder builder(String nodeName,
                                        ContainerCapability host,
                                        EndpointCapability dataEndpoint,
                                        AdminEndpointCapability adminEndpoint) {
        return (ApacheBuilder) new ApacheBuilder()
            .nodeName(nodeName)
            .host(host)
            .dataEndpoint(dataEndpoint)
            .adminEndpoint(adminEndpoint);
    }

    public static class ApacheBuilder extends WebServerBuilder {
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
