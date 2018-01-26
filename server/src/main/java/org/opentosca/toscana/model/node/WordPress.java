package org.opentosca.toscana.model.node;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.requirement.DatabaseEndpointRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 (TOSCA Simple Profile in YAML Version 1.1, p. 222)
 */
@EqualsAndHashCode
@ToString
public class WordPress extends WebApplication {

    public static ToscaKey<String> ADMIN_USER = new ToscaKey<>(PROPERTIES, "admin_user").required();

    public static ToscaKey<String> ADMIN_PASSWORD = new ToscaKey<>(PROPERTIES, "admin_password").required();

    public static ToscaKey<String> DB_HOST = new ToscaKey<>(PROPERTIES, "db_host").required();

    public static ToscaKey<DatabaseEndpointRequirement> DATABASE_ENDPOINT = new RequirementKey<>("database_endpoint")
        .subTypes(DatabaseEndpointCapability.class, Database.class, ConnectsTo.class)
        .type(DatabaseEndpointRequirement.class);

    public WordPress(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(DATABASE_ENDPOINT, new DatabaseEndpointRequirement(getChildEntity(DATABASE_ENDPOINT)));
    }

    /**
     @return {@link #ADMIN_USER}
     */
    public String getAdminUser() {
        return get(ADMIN_USER);
    }

    /**
     Sets {@link #ADMIN_USER}
     */
    public WordPress setAdminUser(String adminUser) {
        set(ADMIN_USER, adminUser);
        return this;
    }

    /**
     @return {@link #ADMIN_PASSWORD}
     */
    public String getAdminPassword() {
        return get(ADMIN_PASSWORD);
    }

    /**
     Sets {@link #ADMIN_PASSWORD}
     */
    public WordPress setAdminPassword(String adminPassword) {
        set(ADMIN_PASSWORD, adminPassword);
        return this;
    }

    /**
     @return {@link #DB_HOST}
     */
    public String getDbHost() {
        return get(DB_HOST);
    }

    /**
     Sets {@link #DB_HOST}
     */
    public WordPress setDbHost(String dbHost) {
        set(DB_HOST, dbHost);
        return this;
    }

    /**
     @return {@link #DATABASE_ENDPOINT}
     */
    public DatabaseEndpointRequirement getDatabaseEndpoint() {
        return get(DATABASE_ENDPOINT);
    }

    /**
     Sets {@link #DATABASE_ENDPOINT}
     */
    public WordPress setDatabaseEndpoint(DatabaseEndpointRequirement databaseEndpoint) {
        set(DATABASE_ENDPOINT, databaseEndpoint);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
