package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability.AttachmentCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class AttachmentCapabilityVisitor<CapabilityT extends AttachmentCapability, BuilderT extends AttachmentCapabilityBuilder> extends CapabilityVisitor<CapabilityT, BuilderT> {

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        super.handleProperty(node, parameter, builder, value);
    }

    @Override
    protected Class getBuilderClass() {
        return AttachmentCapabilityBuilder.class;
    }
}
