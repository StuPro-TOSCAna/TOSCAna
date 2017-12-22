package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.nodedefinition.ObjectStorageDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static org.opentosca.toscana.model.nodedefinition.ObjectStorageDefinition.MAXSIZE_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ObjectStorageDefinition.NAME_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ObjectStorageDefinition.SIZE_PROPERTY;

/**
 Represents storage that provides the ability to store data as objects (or BLOBs of data)
 without consideration for the underlying filesystem or devices.
 (TOSCA Simple Profile in YAML Version 1.1, p. 174)
 */
@Data
public class ObjectStorage extends RootNode {

    /**
     The logical name of the object store (or container).
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    private final String storageName;

    /**
     The optional requested initial storage size in GB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    private final Integer sizeInGB;

    /**
     The optional requested maximum storage size in GB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 174)
     */
    private final Integer maxSizeInGB;

    private final EndpointCapability storageEndpoint;

    @Builder
    private ObjectStorage(String storageName,
                          Integer sizeInGB,
                          Integer maxSizeInGB,
                          EndpointCapability storageEndpoint,
                          String nodeName,
                          StandardLifecycle standardLifecycle,
                          Set<Requirement> requirements,
                          Set<Capability> capabilities,
                          String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        if ((sizeInGB != null && sizeInGB < 0) || (sizeInGB != null && maxSizeInGB < 0)) {
            throw new IllegalArgumentException("Size for ObjectStorage must not be < 0");
        }
        this.storageName = Objects.requireNonNull(storageName);
        this.sizeInGB = sizeInGB;
        this.maxSizeInGB = maxSizeInGB;
        this.storageEndpoint = Objects.requireNonNull(storageEndpoint);

        this.capabilities.add(this.storageEndpoint);
    }

    /**
     @param nodeName        {@link #nodeName}
     @param storageName     {@link #storageName}
     @param storageEndpoint {@link #storageEndpoint}
     */
    public static ObjectStorageBuilder builder(String nodeName,
                                               String storageName,
                                               EndpointCapability storageEndpoint) {
        return new ObjectStorageBuilder()
            .nodeName(nodeName)
            .storageName(storageName)
            .storageEndpoint(storageEndpoint);
    }

    /**
     @return {@link #sizeInGB}
     */
    public Optional<Integer> getSizeInGB() {
        return Optional.ofNullable(get(SIZE_PROPERTY));
    }

    /**
     @return {@link #maxSizeInGB}
     */
    public Optional<Integer> getMaxSizeInGB() {
        return Optional.ofNullable(get(MAXSIZE_PROPERTY));
    }

    public String getStorageName() {
        return get(NAME_PROPERTY);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected AbstractDefinition getDefinition() {
        return new ObjectStorageDefinition();
    }

    public static class ObjectStorageBuilder extends RootNodeBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
