package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.WebServerRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a software application that can be managed and run by a {@link WebServer} node.
 Specific types of web applications such as Java, etc. should be derived from this type.
 (TOSCA Simple Profile in YAML Version 1.1, p. 171)
 */
@EqualsAndHashCode
@ToString
public class WebApplication extends RootNode {
    /**
     The optional web application’s context root which designates
     the application’s URL path within the web server it is hosted on.
     (TOSCA Simple Profile in YAML Version 1.1, p. 172)
     */
    public static ToscaKey<String> CONTEXT_ROOT = new ToscaKey<>(PROPERTIES, "context_root");

    public static ToscaKey<EndpointCapability> APP_ENDPOINT = new ToscaKey<>(CAPABILITIES, "app_endpoint")
        .type(EndpointCapability.class);
    public static ToscaKey<WebServerRequirement> HOST = new RequirementKey<>("host")
        .subTypes(ContainerCapability.class, WebServer.class, HostedOn.class)
        .type(WebServerRequirement.class);

    public WebApplication(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(APP_ENDPOINT, new EndpointCapability(getChildEntity(APP_ENDPOINT)));
        setDefault(HOST, new WebServerRequirement(getChildEntity(HOST)));
    }

    /**
     @return {@link #CONTEXT_ROOT}
     */
    public Optional<String> getContextRoot() {
        return Optional.ofNullable(get(CONTEXT_ROOT));
    }

    /**
     Sets {@link #CONTEXT_ROOT}
     */
    public WebApplication setContextRoot(String contextRoot) {
        set(CONTEXT_ROOT, contextRoot);
        return this;
    }

    /**
     @return {@link #APP_ENDPOINT}
     */
    public EndpointCapability getAppEndpoint() {
        return get(APP_ENDPOINT);
    }

    /**
     Sets {@link #APP_ENDPOINT}
     */
    public WebApplication setAppEndpoint(EndpointCapability appEndpoint) {
        set(APP_ENDPOINT, appEndpoint);
        return this;
    }

    /**
     @return {@link #HOST}
     */
    public WebServerRequirement getHost() {
        return get(HOST);
    }

    /**
     Sets {@link #HOST}
     */
    public WebApplication setHost(WebServerRequirement host) {
        set(HOST, host);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
