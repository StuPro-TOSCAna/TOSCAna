package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.capability.PublicEndpointCapabilityVisitor;
import org.opentosca.toscana.model.node.LoadBalancer;
import org.opentosca.toscana.model.node.LoadBalancer.LoadBalancerBuilder;
import org.opentosca.toscana.model.relation.RoutesTo;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

public class LoadBalancerVisitor<NodeT extends LoadBalancer, BuilderT extends LoadBalancerBuilder> extends RootNodeVisitor<NodeT, BuilderT> {

    private static final String ALGORITHM_PROPERTY = "algorithm";

    private static final String CLIENT_CAPABILITY = "client";

    private static final String APPLICATION_REQUIREMENT = "application";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case ALGORITHM_PROPERTY:
                builder.algorithm((String) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        switch (key) {
            case CLIENT_CAPABILITY:
                builder.client(new PublicEndpointCapabilityVisitor<>().handle(node));
                break;
            default:
                super.handleCapability(node, builder, key);
                break;
        }
    }

    @Override
    protected void handleRequirement(TRequirementAssignment requirement, Context<BuilderT> context, BuilderT builder) {
        switch (context.getKey()) {
            case APPLICATION_REQUIREMENT:
                builder.application(provideRequirement(requirement, context, RoutesTo.class));
                break;
            default:
                super.handleRequirement(requirement, context, builder);
                break;
        }
    }
}
