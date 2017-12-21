package org.opentosca.toscana.model.nodedefinition;

public class ObjectStorageDefinition extends AbstractDefinition {

    public static final String NAME_PROPERTY = "name";
    public static final String SIZE_PROPERTY = "size";
    public static final String MAXSIZE_PROPERTY = "maxsize";

    public static final String STORAGE_ENDPOINT_CAPABILITY = "storage_endpoint";

    static {
        MAPPINGS.put(NAME_PROPERTY, "storageName");
        MAPPINGS.put(SIZE_PROPERTY, "sizeInGB");
        MAPPINGS.put(MAXSIZE_PROPERTY, "maxSizeInGB");
    }
}
