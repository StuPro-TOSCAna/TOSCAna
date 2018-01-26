package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.SizeUnit;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents storage that provides the ability to store data as objects (or BLOBs of data)
 without consideration for the underlying filesystem or devices.
 (TOSCA Simple Profile in YAML Version 1.1, p. 174)
 */
@EqualsAndHashCode
@ToString
public class ObjectStorage extends RootNode {

    /**
     The logical name of the object store (or container).
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    public static ToscaKey<String> STORAGE_NAME = new ToscaKey<>(PROPERTIES, "name")
        .required();

    /**
     The optional requested initial storage size in GB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    public static ToscaKey<Integer> SIZE_IN_GB = new ToscaKey<>(PROPERTIES, "size")
        .type(Integer.class).directive(SizeUnit.FROM, SizeUnit.Unit.GB).directive(SizeUnit.TO, SizeUnit.Unit.GB);

    /**
     The optional requested maximum storage size in GB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    public static ToscaKey<Integer> MAX_SIZE_IN_GB = new ToscaKey<>(PROPERTIES, "maxsize")
        .type(Integer.class).directive(SizeUnit.FROM, SizeUnit.Unit.GB).directive(SizeUnit.TO, SizeUnit.Unit.GB);

    public static ToscaKey<EndpointCapability> STORAGE_ENDPOINT = new ToscaKey<>(CAPABILITIES, "storage_endpoint")
        .type(EndpointCapability.class);

    public ObjectStorage(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(STORAGE_ENDPOINT, new EndpointCapability(getChildEntity(STORAGE_ENDPOINT)));
    }

    /**
     @return {@link #STORAGE_NAME}
     */
    public String getStorageName() {
        return get(STORAGE_NAME);
    }

    /**
     Sets {@link #STORAGE_NAME}
     */
    public ObjectStorage setStorageName(String storageName) {
        set(STORAGE_NAME, storageName);
        return this;
    }

    /**
     @return {@link #STORAGE_ENDPOINT}
     */
    public EndpointCapability getStorageEndpoint() {
        return get(STORAGE_ENDPOINT);
    }

    /**
     Sets {@link #STORAGE_ENDPOINT}
     */
    public ObjectStorage setStorageEndpoint(EndpointCapability storageEndpoint) {
        set(STORAGE_ENDPOINT, storageEndpoint);
        return this;
    }

    /**
     @return {@link #SIZE_IN_GB}
     */
    public Optional<Integer> getSizeInGb() {
        return Optional.ofNullable(get(SIZE_IN_GB));
    }

    /**
     Sets {@link #SIZE_IN_GB}
     */
    public ObjectStorage setSizeInGb(Integer sizeInGb) {
        set(SIZE_IN_GB, sizeInGb);
        return this;
    }

    /**
     @return {@link #MAX_SIZE_IN_GB}
     */
    public Optional<Integer> getMaxSizeInGb() {
        return Optional.ofNullable(get(MAX_SIZE_IN_GB));
    }

    /**
     Sets {@link #MAX_SIZE_IN_GB}
     */
    public ObjectStorage setMaxSizeInGb(Integer maxSizeInGb) {
        set(MAX_SIZE_IN_GB, maxSizeInGb);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
