package org.opentosca.toscana.model.node;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents an abstract software component or service that is capable of hosting and providing management operations
 for one or more {@link WebApplication} nodes.
 <p>
 This node SHALL export both a secure endpoint capability ({@link #ADMIN_ENDPOINT}), typically for
 administration, as well as a regular endpoint ({@link #DATA_ENDPOINT}) for serving data.
 (TOSCA Simple Profile in YAML Version 1.1, p.171)
 */
@EqualsAndHashCode
@ToString
public class WebServer extends SoftwareComponent {

    public static ToscaKey<EndpointCapability> DATA_ENDPOINT = new ToscaKey<>(CAPABILITIES, "data_endpoint")
        .type(EndpointCapability.class);

    public static ToscaKey<AdminEndpointCapability> ADMIN_ENDPOINT = new ToscaKey<>(CAPABILITIES, "admin_endpoint")
        .type(AdminEndpointCapability.class);
    public static ToscaKey<ContainerCapability> CONTAINER_HOST = new ToscaKey<>(CAPABILITIES, "host")
        .type(ContainerCapability.class);

    public WebServer(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(DATA_ENDPOINT, new EndpointCapability(getChildEntity(DATA_ENDPOINT)));
        setDefault(ADMIN_ENDPOINT, new AdminEndpointCapability(getChildEntity(ADMIN_ENDPOINT)));
        setDefault(CONTAINER_HOST, new ContainerCapability(getChildEntity(CONTAINER_HOST)));
    }

    /**
     @return {@link #DATA_ENDPOINT}
     */
    public EndpointCapability getDataEndpoint() {
        return get(DATA_ENDPOINT);
    }

    /**
     Sets {@link #DATA_ENDPOINT}
     */
    public WebServer setDataEndpoint(EndpointCapability dataEndpoint) {
        set(DATA_ENDPOINT, dataEndpoint);
        return this;
    }

    /**
     @return {@link #ADMIN_ENDPOINT}
     */
    public AdminEndpointCapability getAdminEndpoint() {
        return get(ADMIN_ENDPOINT);
    }

    /**
     Sets {@link #ADMIN_ENDPOINT}
     */
    public WebServer setAdminEndpoint(AdminEndpointCapability adminEndpoint) {
        set(ADMIN_ENDPOINT, adminEndpoint);
        return this;
    }

    /**
     @return {@link #CONTAINER_HOST}
     */
    public ContainerCapability getContainerHost() {
        return get(CONTAINER_HOST);
    }

    /**
     Sets {@link #CONTAINER_HOST}
     */
    public WebServer setContainerHost(ContainerCapability containerHost) {
        set(CONTAINER_HOST, containerHost);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
