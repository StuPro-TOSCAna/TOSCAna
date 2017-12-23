package org.opentosca.toscana.model.nodedefinition;

public class BlockStorageDefinition extends BaseDefinition {

    public final static String SIZE_PROPERTY = "size";
    public final static String VOLUME_ID_PROPERTY = "volume_id";
    public final static String SNAPSHOT_ID_PROPERTY = "snapshot_id";

    public final static String ATTACHMENT_CAPABILITY = "attachment";

    public BlockStorageDefinition() {
        mappings.put(SIZE_PROPERTY, "sizeInMB");
    }
}
