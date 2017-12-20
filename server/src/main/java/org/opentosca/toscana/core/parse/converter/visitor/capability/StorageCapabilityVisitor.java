package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.capability.StorageCapability.StorageCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class StorageCapabilityVisitor<CapabilityT extends StorageCapability, BuilderT extends StorageCapabilityBuilder> extends CapabilityVisitor<CapabilityT, BuilderT> {

    private final static String NAME_PROPERTY = "name";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case NAME_PROPERTY:
                builder.name((String) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected Class getBuilderClass() {
        return StorageCapabilityBuilder.class;
    }
}
