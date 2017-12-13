package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

@Data
public class Nodejs extends WebServer {

    private final String githubUrl;

    @Builder
    protected Nodejs(String githubUrl,
                     String componentVersion,
                     Credential adminCredential,
                     HostRequirement host,
                     ContainerCapability containerHost,
                     EndpointCapability dataEndpoint,
                     AdminEndpointCapability adminEndpoint,
                     String nodeName,
                     StandardLifecycle standardLifecycle,
                     String description) {
        super(componentVersion, adminCredential, host, containerHost, dataEndpoint,
            adminEndpoint, nodeName, standardLifecycle, description);
        this.githubUrl = (githubUrl == null || githubUrl.isEmpty()) ?
            "https://github.com/mmm/testnode.git" : githubUrl;
    }

    /**
     @param nodeName      {@link #nodeName}
     @param host          {@link #host}
     @param containerHost {@link #containerHost}
     @param dataEndpoint  {@link #dataEndpoint}
     @param adminEndpoint {@link #adminEndpoint}
     */
    public static NodejsBuilder builder(String nodeName,
                                        HostRequirement host,
                                        ContainerCapability containerHost,
                                        EndpointCapability dataEndpoint,
                                        AdminEndpointCapability adminEndpoint) {
        return new NodejsBuilder()
            .nodeName(nodeName)
            .host(host)
            .containerHost(containerHost)
            .dataEndpoint(dataEndpoint)
            .adminEndpoint(adminEndpoint);
    }

    /**
     @return {@link #githubUrl}
     */
    public Optional<String> getGithubUrl() {
        return Optional.ofNullable(githubUrl);
    }

    public static class NodejsBuilder extends WebServerBuilder {
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
