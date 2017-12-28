package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability.DatabaseEndpointCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class DatabaseEndpointCapabilityVisitor<CapabilityT extends DatabaseEndpointCapability, BuilderT extends DatabaseEndpointCapabilityBuilder> extends EndpointCapabilityVisitor<CapabilityT, BuilderT> {

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        super.handleProperty(node, parameter, builder, value);
    }

    @Override
    protected Class getBuilderClass() {
        return DatabaseEndpointCapabilityBuilder.class;
    }
}
