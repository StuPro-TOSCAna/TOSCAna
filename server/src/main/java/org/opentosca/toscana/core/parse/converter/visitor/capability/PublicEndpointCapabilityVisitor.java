package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.PublicEndpointCapability;
import org.opentosca.toscana.model.capability.PublicEndpointCapability.PublicEndpointCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class PublicEndpointCapabilityVisitor<CapabilityT extends PublicEndpointCapability, BuilderT extends PublicEndpointCapabilityBuilder> extends EndpointCapabilityVisitor<CapabilityT, BuilderT> {

    private final static String FLOATING_PROPERTY = "floating";
    private final static String DNS_NAME_PROPERTY = "dns_name";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case DNS_NAME_PROPERTY:
                builder.dnsName((String) value);
                break;
            case FLOATING_PROPERTY:
                builder.floating((Boolean) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected Class getBuilderClass() {
        return PublicEndpointCapabilityBuilder.class;
    }
}
