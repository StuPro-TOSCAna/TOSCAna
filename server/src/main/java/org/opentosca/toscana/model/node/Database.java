package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.nodedefinition.BaseDefinition;
import org.opentosca.toscana.model.nodedefinition.DatabaseDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.DbmsRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

import static org.opentosca.toscana.model.nodedefinition.DatabaseDefinition.NAME_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.DatabaseDefinition.PASSWORD_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.DatabaseDefinition.PORT_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.DatabaseDefinition.USER_PROPERTY;

/**
 Represents a logical database that can be managed and hosted by a {@link Dbms} node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 173)
 */
@Data
public class Database extends RootNode {

    public final Requirement<ContainerCapability, Dbms, HostedOn> host;

    /**
     The logical database databaseName.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    private final String databaseName;

    /**
     The optional port the database service will use for incoming data and request.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    private final Integer port;

    /**
     The optional special user account used for database administration.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    private final String user;
    /**
     The optional password associated with the user account provided in the {@link #user} field.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    private final String password;

    private final DatabaseEndpointCapability databaseEndpoint;

    @Builder
    private Database(String databaseName,
                     Integer port,
                     String user,
                     String password,
                     Requirement<ContainerCapability, Dbms, HostedOn> host,
                     DatabaseEndpointCapability databaseEndpoint,
                     String nodeName,
                     StandardLifecycle standardLifecycle,
                     Set<Requirement> requirements,
                     Set<Capability> capabilities,
                     String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        this.databaseName = Objects.requireNonNull(databaseName);
        this.port = port;
        this.user = user;
        this.password = password;
        this.host = DbmsRequirement.getFallback(host);
        this.databaseEndpoint = DatabaseEndpointCapability.getFallback(databaseEndpoint);

        capabilities.add(this.databaseEndpoint);
        requirements.add(this.host);
    }

    // only use when subclassing this and hiding host field
    protected Database(String databaseName,
                       Integer port,
                       String user,
                       String password,
                       DatabaseEndpointCapability databaseEndpoint,
                       String nodeName,
                       StandardLifecycle standardLifecycle,
                       Set<Requirement> requirements,
                       Set<Capability> capabilities,
                       String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        this.databaseName = Objects.requireNonNull(databaseName);
        this.port = port;
        this.user = user;
        this.password = password;
        this.host = null;
        this.databaseEndpoint = DatabaseEndpointCapability.getFallback(databaseEndpoint);

        this.capabilities.add(this.databaseEndpoint);
    }

    /**
     @param nodeName     {@link #nodeName}
     @param databaseName {@link #databaseName}
     */
    public static DatabaseBuilder builder(String nodeName,
                                          String databaseName) {
        return new DatabaseBuilder()
            .nodeName(nodeName)
            .databaseName(databaseName);
    }

    /**
     @return {@link #port}
     */
    public Optional<Integer> getPort() {
        return Optional.ofNullable(get(PORT_PROPERTY));
    }

    /**
     @return {@link #user}
     */
    public Optional<String> getUser() {
        return Optional.ofNullable(get(USER_PROPERTY));
    }

    /**
     @return {@link #password}
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(get(PASSWORD_PROPERTY));
    }

    public String getDatabaseName() {
        return get(NAME_PROPERTY);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected BaseDefinition getDefinition() {
        return new DatabaseDefinition();
    }

    public static class DatabaseBuilder extends RootNodeBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
