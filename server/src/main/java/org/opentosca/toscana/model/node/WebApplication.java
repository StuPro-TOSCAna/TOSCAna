package org.opentosca.toscana.model.node;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.requirement.WebServerRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a software application that can be managed and run by a {@link WebServer} node.
 Specific types of web applications such as Java, etc. should be derived from this type.
 (TOSCA Simple Profile in YAML Version 1.1, p. 171)
 */
@Data
public class WebApplication extends RootNode {
    /**
     The optional web application’s context root which designates
     the application’s URL path within the web server it is hosted on.
     (TOSCA Simple Profile in YAML Version 1.1, p. 172)
     */
    private final String contextRoot;

    private final EndpointCapability appEndpoint;

    private final Requirement<ContainerCapability, WebServer, HostedOn> host;

    @Builder
    protected WebApplication(String contextRoot,
                             EndpointCapability endpoint,
                             Requirement<ContainerCapability, WebServer, HostedOn> host,
                             String nodeName,
                             StandardLifecycle standardLifecycle,
                             Set<Requirement> requirements,
                             Set<Capability> capabilities,
                             String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        this.contextRoot = contextRoot;
        this.appEndpoint = EndpointCapability.getFallback(endpoint);
        this.host = (host == null) ? WebServerRequirement.builder().build() : host;

        this.capabilities.add(this.appEndpoint);
        this.requirements.add(this.host);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static WebApplicationBuilder builder(String nodeName) {
        return new WebApplicationBuilder()
            .nodeName(nodeName);
    }

    /**
     @return {@link #contextRoot}
     */
    public Optional<String> getContextRoot() {
        return Optional.ofNullable(contextRoot);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public static class WebApplicationBuilder extends RootNodeBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
