package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.util.SizeConverter;
import org.opentosca.toscana.core.parse.converter.util.SizeConverter.Unit;
import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.capability.AttachmentCapabilityVisitor;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.node.BlockStorage.BlockStorageBuilder;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class BlockStorageVisitor<NodeT extends BlockStorage, BuilderT extends BlockStorageBuilder> extends RootNodeVisitor<NodeT, BuilderT> {

    private final static String SIZE_PROPERTY = "size";
    private final static String VOLUME_ID_PROPERTY = "volume_id";
    private final static String SNAPSHOT_ID_PROPERTY = "snapshot_id";

    private final static String ATTACHMENT_CAPABILITY = "attachment";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case SIZE_PROPERTY:
                Integer size = new SizeConverter().convert(value, Unit.MB, Unit.MB);
                builder.sizeInMB(size);
                break;
            case VOLUME_ID_PROPERTY:
                builder.volumeId((String) value);
                break;
            case SNAPSHOT_ID_PROPERTY:
                builder.snapshotId((String) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    public void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        switch (key) {
            case ATTACHMENT_CAPABILITY:
                builder.attachment(new AttachmentCapabilityVisitor<>().handle(node));
                break;
            default:
                super.handleCapability(node, builder, key);
                break;
        }
    }
}
