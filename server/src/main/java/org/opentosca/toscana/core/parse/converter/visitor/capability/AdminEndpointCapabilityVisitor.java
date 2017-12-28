package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AdminEndpointCapability.AdminEndpointCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class AdminEndpointCapabilityVisitor<CapabilityT extends AdminEndpointCapability, BuilderT extends AdminEndpointCapabilityBuilder> extends EndpointCapabilityVisitor<CapabilityT, BuilderT> {

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        super.handleProperty(node, parameter, builder, value);
    }

    @Override
    protected Class getBuilderClass() {
        return AdminEndpointCapabilityBuilder.class;
    }
}
