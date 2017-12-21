package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.ConversionResult;
import org.opentosca.toscana.model.node.ContainerApplication;
import org.opentosca.toscana.model.node.ContainerApplication.ContainerApplicationBuilder;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

import static org.opentosca.toscana.model.nodedefinition.ContainerApplicationDefinition.HOST_REQUIREMENT;
import static org.opentosca.toscana.model.nodedefinition.ContainerApplicationDefinition.NETWORK_REQUIREMENT;
import static org.opentosca.toscana.model.nodedefinition.ContainerApplicationDefinition.STORAGE_REQUIREMENT;

public class ContainerApplicationVisitor<NodeT extends ContainerApplication, BuilderT extends ContainerApplicationBuilder> extends RootNodeVisitor<NodeT, BuilderT> {

    @Override
    public ConversionResult<NodeT> visit(TCapabilityAssignment node, Context<BuilderT> parameter) {
        return super.visit(node, parameter);
    }

    @Override
    protected void handleRequirement(TRequirementAssignment requirement, Context<BuilderT> context, BuilderT builder) {
        switch (context.getKey()) {
            case HOST_REQUIREMENT:
                builder.host(provideRequirement(requirement, context, HostedOn.class));
                break;
            case STORAGE_REQUIREMENT:
                builder.storage(provideRequirement(requirement, context, RootRelationship.class));
                break;
            case NETWORK_REQUIREMENT:
                builder.network(provideRequirement(requirement, context, RootRelationship.class));
                break;
            default:
                super.handleRequirement(requirement, context, builder);
                break;
        }
    }
}
