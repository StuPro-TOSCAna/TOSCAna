package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.nodedefinition.WordPressDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.requirement.DatabaseEndpointRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.requirement.WebServerRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

import static org.opentosca.toscana.model.nodedefinition.WordPressDefinition.ADMIN_PASSWORD_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.WordPressDefinition.ADMIN_USER_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.WordPressDefinition.DB_HOST_PROPERTY;

/**
 (TOSCA Simple Profile in YAML Version 1.1, p. 222)
 */
@Data
public class WordPress extends WebApplication {

    private final String adminUser;

    private final String adminPassword;

    private final String dbHost;

    private final Requirement<DatabaseEndpointCapability, Database, ConnectsTo> databaseEndpoint;

    @Builder
    protected WordPress(String adminUser,
                        String adminPassword,
                        String dbHost,
                        Requirement<DatabaseEndpointCapability, Database, ConnectsTo> databaseEndpoint,
                        String contextRoot,
                        EndpointCapability endpoint,
                        WebServerRequirement host,
                        String nodeName,
                        StandardLifecycle standardLifecycle,
                        Set<Requirement> requirements,
                        Set<Capability> capabilities,
                        String description) {
        super(contextRoot, endpoint, host, nodeName, standardLifecycle, requirements, capabilities, description);
        this.adminUser = Objects.requireNonNull(adminUser);
        this.adminPassword = Objects.requireNonNull(adminPassword);
        this.dbHost = Objects.requireNonNull(dbHost);
        this.databaseEndpoint = DatabaseEndpointRequirement.getFallback(databaseEndpoint);

        this.requirements.add(this.databaseEndpoint);
    }

    /**
     @param nodeName      {@link #nodeName}
     @param adminUser     {@link #adminUser}
     @param adminPassword {@link #adminPassword}
     @param dbHost        {@link #dbHost}
     @param endpoint      {@link WebApplication#appEndpoint}
     */
    public static WordPressBuilder builder(String nodeName,
                                           String adminUser,
                                           String adminPassword,
                                           String dbHost,
                                           EndpointCapability endpoint) {
        return (WordPressBuilder) new WordPressBuilder()
            .nodeName(nodeName)
            .adminUser(adminUser)
            .adminPassword(adminPassword)
            .dbHost(dbHost)
            .endpoint(endpoint);
    }

    public static WordPressBuilder builder() {
        return new WordPressBuilder();
    }

    public String getAdminUser() {
        return get(ADMIN_USER_PROPERTY);
    }

    public String getAdminPassword() {
        return get(ADMIN_PASSWORD_PROPERTY);
    }

    public String getDbHost() {
        return get(DB_HOST_PROPERTY);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected AbstractDefinition getDefinition() {
        return new WordPressDefinition();
    }

    public static class WordPressBuilder extends WebApplicationBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
