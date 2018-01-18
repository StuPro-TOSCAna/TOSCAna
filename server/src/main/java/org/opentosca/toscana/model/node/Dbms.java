package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a typical relational, SQL Database Management System software component or service.
 (TOSCA Simple Profile in YAML Version 1.1, p. 172)
 */
@EqualsAndHashCode
@ToString
public class Dbms extends SoftwareComponent {

    /**
     The optional root password for the Dbms server.
     (TOSCA Simple Profile in YAML Version 1.1, p. 172)
     */
    public static ToscaKey<String> ROOT_PASSWORD = new ToscaKey<>(PROPERTIES, "root_password");

    /**
     The optional Dbms server port.
     (TOSCA Simple Profile in YAML Version 1.1, p. 172)
     */
    public static ToscaKey<Integer> PORT = new ToscaKey<>(PROPERTIES, "port")
        .type(Integer.class);

    public static ToscaKey<ContainerCapability> CONTAINER_HOST = new ToscaKey<>(CAPABILITIES, "host")
        .type(ContainerCapability.class);

    public Dbms(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(CONTAINER_HOST, new ContainerCapability(getChildEntity(CONTAINER_HOST)));
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
    public Dbms setContainerHost(ContainerCapability containerHost) {
        set(CONTAINER_HOST, containerHost);
        return this;
    }

    /**
     @return {@link #ROOT_PASSWORD}
     */
    public Optional<String> getRootPassword() {
        return Optional.ofNullable(get(ROOT_PASSWORD));
    }

    /**
     Sets {@link #ROOT_PASSWORD}
     */
    public Dbms setRootPassword(String rootPassword) {
        set(ROOT_PASSWORD, rootPassword);
        return this;
    }

    /**
     @return {@link #PORT}
     */
    public Optional<Integer> getPort() {
        return Optional.ofNullable(get(PORT));
    }

    /**
     Sets {@link #PORT}
     */
    public Dbms setPort(Integer port) {
        set(PORT, port);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
