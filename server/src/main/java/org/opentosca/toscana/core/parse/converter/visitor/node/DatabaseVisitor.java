package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.capability.DatabaseEndpointCapabilityVisitor;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Database.DatabaseBuilder;
import org.opentosca.toscana.model.relation.HostedOn;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

public class DatabaseVisitor<NodeT extends Database, BuilderT extends DatabaseBuilder> extends RootNodeVisitor<NodeT, BuilderT> {

    private final static String NAME_PROPERTY = "name";
    private final static String PORT_PROPERTY = "port";
    private final static String USER_PROPERTY = "user";
    private final static String PASSWORD_PROPERTY = "password";

    private final static String DATABASE_ENDPOINT_CAPABILITY = "database_endpoint";

    private final static String HOST_REQUIREMENT = "host";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case NAME_PROPERTY:
                builder.databaseName((String) value);
                break;
            case PORT_PROPERTY:
                builder.port((Integer) value);
                break;
            case USER_PROPERTY:
                builder.user((String) value);
                break;
            case PASSWORD_PROPERTY:
                builder.password((String) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        switch (key) {
            case DATABASE_ENDPOINT_CAPABILITY:
                builder.databaseEndpoint(new DatabaseEndpointCapabilityVisitor<>().handle(node));
                break;
            default:
                super.handleCapability(node, builder, key);
                break;
        }
    }

    @Override
    protected void handleRequirement(TRequirementAssignment requirement, Context<BuilderT> context, BuilderT builder) {
        switch (context.getKey()) {
            case HOST_REQUIREMENT:
                builder.host(provideRequirement(requirement, context, HostedOn.class));
                break;
            default:
                super.handleRequirement(requirement, context, builder);
                break;
        }
    }
}
