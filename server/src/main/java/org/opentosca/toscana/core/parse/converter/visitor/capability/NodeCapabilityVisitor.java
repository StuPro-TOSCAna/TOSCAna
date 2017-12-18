package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.NodeCapability;
import org.opentosca.toscana.model.capability.NodeCapability.NodeCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class NodeCapabilityVisitor<CapabilityT extends NodeCapability, BuilderT extends NodeCapabilityBuilder> extends CapabilityVisitor<CapabilityT, BuilderT> {

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        super.handleProperty(node, parameter, builder, value);
    }

    @Override
    protected Class getBuilderClass() {
        return NodeCapabilityBuilder.class;
    }
}
