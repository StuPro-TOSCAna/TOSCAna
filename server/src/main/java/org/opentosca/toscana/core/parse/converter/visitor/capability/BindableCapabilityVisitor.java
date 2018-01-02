package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.BindableCapability.BindableCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class BindableCapabilityVisitor<CapabilityT extends BindableCapability, BuilderT extends BindableCapabilityBuilder> extends NodeCapabilityVisitor<CapabilityT, BuilderT> {

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        super.handleProperty(node, parameter, builder, value);
    }

    @Override
    protected Class getBuilderClass() {
        return BindableCapabilityBuilder.class;
    }
}
