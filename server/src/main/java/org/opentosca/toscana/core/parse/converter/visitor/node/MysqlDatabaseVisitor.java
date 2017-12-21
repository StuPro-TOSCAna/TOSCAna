package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDatabase.MysqlDatabaseBuilder;
import org.opentosca.toscana.model.relation.HostedOn;

import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

import static org.opentosca.toscana.model.nodedefinition.MysqlDatabaseDefinition.HOST_REQUIREMENT;

public class MysqlDatabaseVisitor<NodeT extends MysqlDatabase, BuilderT extends MysqlDatabaseBuilder> extends DatabaseVisitor<NodeT, BuilderT> {

    @Override
    protected void handleRequirement(TRequirementAssignment requirement, Context<BuilderT> context, BuilderT builder) {
        switch (context.getKey()) {
            case HOST_REQUIREMENT:
                builder.mysqlHost(provideRequirement(requirement, context, HostedOn.class));
                break;
            default:
                super.handleRequirement(requirement, context, builder);
                break;
        }
    }
}
