package org.opentosca.toscana.model.node;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.nodedefinition.DbmsDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static org.opentosca.toscana.model.nodedefinition.DbmsDefinition.PORT_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.DbmsDefinition.ROOT_PASSWORD_PROPERTY;

/**
 Represents a typical relational, SQL Database Management System software component or service.
 (TOSCA Simple Profile in YAML Version 1.1, p. 172)
 */
@Data
public class Dbms extends SoftwareComponent {

    /**
     The optional root password for the Dbms server.
     (TOSCA Simple Profile in YAML Version 1.1, p. 172)
     */
    private String rootPassword;

    /**
     The Dbms server’s port.
     (TOSCA Simple Profile in YAML Version 1.1, p. 172)
     */
    private final Integer port;

    private final ContainerCapability containerHost;

    @Builder
    protected Dbms(Requirement<ContainerCapability, Compute, HostedOn> host,
                   ContainerCapability containerHost,
                   String rootPassword,
                   Integer port,
                   String componentVersion,
                   Credential adminCredential,
                   String nodeName,
                   StandardLifecycle standardLifecycle,
                   Set<Requirement> requirements,
                   Set<Capability> capabilities,
                   String description) {
        super(componentVersion, adminCredential, host, nodeName, standardLifecycle, requirements, capabilities, description);
        this.containerHost = ContainerCapability.getFallback(containerHost);
        this.port = port;
        this.rootPassword = rootPassword;

        this.capabilities.add(this.containerHost);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static DbmsBuilder builder(String nodeName) {
        return new DbmsBuilder()
            .nodeName(nodeName);
    }

    /**
     @return {@link #rootPassword}
     */
    public Optional<String> getRootPassword() {
        return Optional.ofNullable(get(ROOT_PASSWORD_PROPERTY));
    }

    /**
     @return {@link #rootPassword}
     */
    public void setRootPassword(String rootPassword) {
        set(ROOT_PASSWORD_PROPERTY, rootPassword);
    }

    /**
     @return {@link #port}
     */
    public Optional<Integer> getPort() {
        return Optional.ofNullable(get(PORT_PROPERTY));
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected AbstractDefinition getDefinition() {
        return new DbmsDefinition();
    }

    public static class DbmsBuilder extends SoftwareComponentBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
