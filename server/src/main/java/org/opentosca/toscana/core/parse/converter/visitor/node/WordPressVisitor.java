package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.node.WordPress;
import org.opentosca.toscana.model.node.WordPress.WordPressBuilder;
import org.opentosca.toscana.model.relation.ConnectsTo;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

import static org.opentosca.toscana.model.nodedefinition.WordPressDefinition.ADMIN_PASSWORD_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.WordPressDefinition.ADMIN_USER_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.WordPressDefinition.DB_HOST_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.WordPressDefinition.HOST_REQUIREMENT;

public class WordPressVisitor<NodeT extends WordPress, BuilderT extends WordPressBuilder> extends WebApplicationVisitor<NodeT, BuilderT> {

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case ADMIN_USER_PROPERTY:
                builder.adminUser((String) value);
                break;
            case ADMIN_PASSWORD_PROPERTY:
                builder.adminPassword((String) value);
                break;
            case DB_HOST_PROPERTY:
                builder.dbHost((String) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected void handleRequirement(TRequirementAssignment requirement, Context<BuilderT> context, BuilderT builder) {
        switch (context.getKey()) {
            case HOST_REQUIREMENT:
                builder.databaseEndpoint(provideRequirement(requirement, context, ConnectsTo.class));
                break;
            default:
                super.handleRequirement(requirement, context, builder);
                break;
        }
    }
}
