package org.opentosca.toscana.model.node;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.nodedefinition.NodejsDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static org.opentosca.toscana.model.nodedefinition.NodejsDefinition.GITHUB_URL_PROPERTY;

@EqualsAndHashCode
@ToString
public class Nodejs extends WebServer {

    private final String githubUrl;

    @Builder
    protected Nodejs(String githubUrl,
                     String componentVersion,
                     Credential adminCredential,
                     Requirement<ContainerCapability, Compute, HostedOn> host,
                     ContainerCapability containerHost,
                     EndpointCapability dataEndpoint,
                     AdminEndpointCapability adminEndpoint,
                     String nodeName,
                     StandardLifecycle standardLifecycle,
                     Set<Requirement> requirements,
                     Set<Capability> capabilities,
                     String description) {
        super(componentVersion, adminCredential, host, containerHost, dataEndpoint,
            adminEndpoint, nodeName, standardLifecycle, requirements, capabilities, description);
        this.githubUrl = (githubUrl == null || githubUrl.isEmpty()) ?
            "https://github.com/mmm/testnode.git" : githubUrl;
    }

    /**
     @param nodeName      {@link #nodeName}
     @param containerHost {@link #containerHost}
     @param dataEndpoint  {@link #dataEndpoint}
     @param adminEndpoint {@link #adminEndpoint}
     */
    public static NodejsBuilder builder(String nodeName,
                                        ContainerCapability containerHost,
                                        EndpointCapability dataEndpoint,
                                        AdminEndpointCapability adminEndpoint) {
        return new NodejsBuilder()
            .nodeName(nodeName)
            .containerHost(containerHost)
            .dataEndpoint(dataEndpoint)
            .adminEndpoint(adminEndpoint);
    }

    /**
     @return {@link #githubUrl}
     */
    public Optional<String> getGithubUrl() {
        return Optional.ofNullable(get(GITHUB_URL_PROPERTY));
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected AbstractDefinition getDefinition() {
        return new NodejsDefinition();
    }

    public static class NodejsBuilder extends WebServerBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
