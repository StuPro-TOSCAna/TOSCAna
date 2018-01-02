package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.capability.EndpointCapabilityVisitor;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebApplication.WebApplicationBuilder;
import org.opentosca.toscana.model.relation.HostedOn;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

public class WebApplicationVisitor<NodeT extends WebApplication, BuilderT extends WebApplicationBuilder> extends RootNodeVisitor<NodeT, BuilderT> {

    private final static String CONTEXT_ROOT_PROPERTY = "context_root";

    private final static String APP_ENDPOINT_CAPABILITY = "app_endpoint";

    private final static String HOST_REQUIREMENT = "host";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case CONTEXT_ROOT_PROPERTY:
                builder.contextRoot((String) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        switch (key) {
            case APP_ENDPOINT_CAPABILITY:
                builder.endpoint(new EndpointCapabilityVisitor<>().handle(node));
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
