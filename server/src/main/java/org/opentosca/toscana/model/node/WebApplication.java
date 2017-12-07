package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.operation.StandardLifecycle;
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

    private final WebServerRequirement host;

    @Builder
    protected WebApplication(String contextRoot,
                             EndpointCapability endpoint,
                             WebServerRequirement host,
                             String nodeName,
                             StandardLifecycle standardLifecycle,
                             String description) {
        super(nodeName, standardLifecycle, description);
        this.contextRoot = contextRoot;
        this.appEndpoint = Objects.requireNonNull(endpoint);
        this.host = (host == null) ? WebServerRequirement.builder().build() : host;

        capabilities.add(this.appEndpoint);
        requirements.add(this.host);
    }

    /**
     @param nodeName {@link #nodeName}
     @param endpoint {@link #appEndpoint}
     */
    public static WebApplicationBuilder builder(String nodeName, EndpointCapability endpoint) {
        return new WebApplicationBuilder()
            .nodeName(nodeName)
            .endpoint(endpoint);
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
    }
}
