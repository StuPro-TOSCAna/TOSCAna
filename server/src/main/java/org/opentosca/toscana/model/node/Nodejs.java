package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;

@Data
public class Nodejs extends WebServer {

    private final String githubUrl;

    @Builder
    protected Nodejs(String githubUrl,
                     String componentVersion,
                     Credential adminCredential,
                     ContainerCapability host,
                     EndpointCapability dataEndpoint,
                     AdminEndpointCapability adminEndpoint,
                     String nodeName,
                     StandardLifecycle standardLifecycle,
                     String description) {
        super(componentVersion, adminCredential, host, dataEndpoint,
            adminEndpoint, nodeName, standardLifecycle, description);
        this.githubUrl = (githubUrl == null || githubUrl.isEmpty()) ?
            "https://github.com/mmm/testnode.git" : githubUrl;
    }

    /**
     @param nodeName      {@link #nodeName}
     @param host          {@link #host}
     @param dataEndpoint  {@link #dataEndpoint}
     @param adminEndpoint {@link #adminEndpoint}
     */
    public static NodejsBuilder builder(String nodeName,
                                        ContainerCapability host,
                                        EndpointCapability dataEndpoint,
                                        AdminEndpointCapability adminEndpoint) {
        return new NodejsBuilder()
            .nodeName(nodeName)
            .host(host)
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
    public void accept(Visitor v) {
        v.visit(this);
    }
}
