package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.util.SizeConverter;
import org.opentosca.toscana.core.parse.converter.util.SizeConverter.Unit;
import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.capability.EndpointCapabilityVisitor;
import org.opentosca.toscana.model.node.ObjectStorage;
import org.opentosca.toscana.model.node.ObjectStorage.ObjectStorageBuilder;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class ObjectStorageVisitor<NodeT extends ObjectStorage, BuilderT extends ObjectStorageBuilder> extends RootNodeVisitor<NodeT, BuilderT> {

    private static final String NAME_PROPERTY = "name";
    private static final String SIZE_PROPERTY = "size";
    private static final String MAXSIZE_PROPERTY = "maxsize";

    private static final String STORAGE_ENDPOINT_CAPABILITY = "storage_endpoint";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case NAME_PROPERTY:
                builder.storageName((String) value);
                break;
            case SIZE_PROPERTY:
                Integer size = new SizeConverter().convert(value, Unit.GB, Unit.GB);
                builder.sizeInGB(size);
                break;
            case MAXSIZE_PROPERTY:
                Integer maxSize = new SizeConverter().convert(value, Unit.GB, Unit.GB);
                builder.maxSizeInGB(maxSize);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        switch (key) {
            case STORAGE_ENDPOINT_CAPABILITY:
                builder.storageEndpoint(new EndpointCapabilityVisitor<>().handle(node));
                break;
            default:
                super.handleCapability(node, builder, key);
                break;
        }
    }
}
