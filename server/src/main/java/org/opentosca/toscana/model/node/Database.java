package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.DbmsRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a logical database that can be managed and hosted by a {@link Dbms} node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 173)
 */
@EqualsAndHashCode
@ToString
public class Database extends RootNode {

    /**
     The logical database databaseName.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    public static ToscaKey<String> DATABASE_NAME = new ToscaKey<>(PROPERTIES, "name")
        .required();

    /**
     The optional port the database service will use for incoming data and request.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    public static ToscaKey<Integer> PORT = new ToscaKey<>(PROPERTIES, "port")
        .type(Integer.class);

    /**
     The optional special user account used for database administration.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    public static ToscaKey<String> USER = new ToscaKey<>(PROPERTIES, "user");

    /**
     The optional password associated with the user account provided in the {@link #USER} field.
     (TOSCA Simple Profile in YAML Version 1.1, p. 173)
     */
    public static ToscaKey<String> PASSWORD = new ToscaKey<>(PROPERTIES, "password");

    public static ToscaKey<DatabaseEndpointCapability> DATABASE_ENDPOINT = new ToscaKey<>(CAPABILITIES, "database_endpoint")
        .type(DatabaseEndpointCapability.class);

    public static ToscaKey<DbmsRequirement> HOST = new RequirementKey<>("host")
        .subTypes(ContainerCapability.class, Dbms.class, HostedOn.class)
        .type(DbmsRequirement.class);

    public Database(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(DATABASE_ENDPOINT, new DatabaseEndpointCapability(getChildEntity(DATABASE_ENDPOINT)));
        setDefault(HOST, new DbmsRequirement(getChildEntity(HOST)));
    }

    /**
     @return {@link #DATABASE_NAME}
     */
    public String getDatabaseName() {
        return get(DATABASE_NAME);
    }

    /**
     Sets {@link #DATABASE_NAME}
     */
    public Database setDatabaseName(String databaseName) {
        set(DATABASE_NAME, databaseName);
        return this;
    }

    /**
     @return {@link #DATABASE_ENDPOINT}
     */
    public DatabaseEndpointCapability getDatabaseEndpoint() {
        return get(DATABASE_ENDPOINT);
    }

    /**
     Sets {@link #DATABASE_ENDPOINT}
     */
    public Database setDatabaseEndpoint(DatabaseEndpointCapability databaseEndpoint) {
        set(DATABASE_ENDPOINT, databaseEndpoint);
        return this;
    }

    /**
     @return {@link #HOST}
     */
    public DbmsRequirement getHost() {
        return get(HOST);
    }

    /**
     Sets {@link #HOST}
     */
    public Database setHost(DbmsRequirement host) {
        set(HOST, host);
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
    public Database setPort(Integer port) {
        set(PORT, port);
        return this;
    }

    /**
     @return {@link #USER}
     */
    public Optional<String> getUser() {
        return Optional.ofNullable(get(USER));
    }

    /**
     Sets {@link #USER}
     */
    public Database setUser(String user) {
        set(USER, user);
        return this;
    }

    /**
     @return {@link #PASSWORD}
     */
    public Optional<String> getPassword() {
        return Optional.ofNullable(get(PASSWORD));
    }

    /**
     Sets {@link #PASSWORD}
     */
    public Database setPassword(String password) {
        set(PASSWORD, password);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
