package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.nodedefinition.BaseDefinition;
import org.opentosca.toscana.model.nodedefinition.BlockStorageDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

import static java.lang.String.format;
import static org.opentosca.toscana.model.nodedefinition.BlockStorageDefinition.ATTACHMENT_CAPABILITY;
import static org.opentosca.toscana.model.nodedefinition.BlockStorageDefinition.SIZE_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.BlockStorageDefinition.SNAPSHOT_ID_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.BlockStorageDefinition.VOLUME_ID_PROPERTY;

/**
 Represents a server-local block storage device (i.e., not shared) offering evenly sized blocks of data
 from which raw storage volumes can be created.
 <p>
 Note: In this draft of the TOSCA Simple Profile, distributed or Network Attached Storage (NAS) are not
 yet considered (nor are clustered file systems), but the TC plans to do so in future drafts.
 <p>
 Resize is of existing volumes is not considered at this time.
 <p>
 It is assumed that the volume contains a single filesystem that the operating system (that is
 hosting an associate application) can recognize and mount without additional information (i.e., it
 is operating system independent).
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 175-176)
 */
@Data
public class BlockStorage extends RootNode {

    /**
     The requested storage size in MB.
     <p>
     Note : Required when an existing volume ({@link #volumeId}) is not available.
     <p>
     If {@link #volumeId} is provided, size is ignored.
     <p>
     Resize of existing volumes is not considered at this time.
     (TOSCA Simple Profile in YAML Version 1.1, p. 175)
     */
    private final Integer sizeInMB;

    /**
     Optional ID of an existing volume (that is in the accessible scope of the requesting application).
     (TOSCA Simple Profile in YAML Version 1.1, p. 175)
     */
    private final String volumeId;

    /**
     Optional ID of an existing snapshot that should be used when creating the block storage (volume).
     (TOSCA Simple Profile in YAML Version 1.1, p. 175)
     */
    private final String snapshotId;

    private final AttachmentCapability attachment;

    /**
     Either sizeInMB or volumeId must be given.
     */
    @Builder
    private BlockStorage(Integer sizeInMB,
                         String volumeId,
                         String snapshotId,
                         AttachmentCapability attachment,
                         String nodeName,
                         StandardLifecycle standardLifecycle,
                         Set<Requirement> requirements,
                         Set<Capability> capabilities,
                         String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        if (sizeInMB != null && sizeInMB < 1) {
            throw new IllegalArgumentException(format("Constraint violation: sizeInMB >= 1; but was '%d'", sizeInMB));
        }
        this.sizeInMB = sizeInMB;
        this.volumeId = volumeId;
        this.snapshotId = snapshotId;
        this.attachment = Objects.requireNonNull(attachment);

        this.capabilities.add(this.attachment);
    }

    /**
     @param nodeName   {@link #nodeName}
     @param attachment {@link #attachment}
     @param snapshotId {@link #snapshotId}
     */
    public static BlockStorageBuilder builder(String nodeName,
                                              AttachmentCapability attachment,
                                              String snapshotId) {
        return new BlockStorageBuilder()
            .nodeName(nodeName)
            .attachment(attachment)
            .snapshotId(Objects.requireNonNull(snapshotId));
    }

    /**
     @param nodeName   {@link #nodeName}
     @param volumeId   {@link #volumeId}
     @param attachment {@link #attachment}
     */
    public static BlockStorageBuilder builder(String nodeName,
                                              String volumeId,
                                              AttachmentCapability attachment) {
        return new BlockStorageBuilder()
            .nodeName(nodeName)
            .volumeId(Objects.requireNonNull(volumeId))
            .attachment(attachment);
    }

    /**
     @return the optional size of this block storage (in MB)
     */
    public Optional<Integer> getSizeInMB() {
        return Optional.ofNullable(get(SIZE_PROPERTY));
    }

    /**
     @return the optional ID of the volume
     */
    public Optional<String> getVolumeId() {
        return Optional.ofNullable(get(VOLUME_ID_PROPERTY));
    }

    /**
     @return optional identifier of the existing snapshot which is used when creating the block storage
     */
    public Optional<String> getSnapshotId() {
        return Optional.ofNullable(get(SNAPSHOT_ID_PROPERTY));
    }

    public AttachmentCapability getAttachment() {
        return get(ATTACHMENT_CAPABILITY);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected BaseDefinition getDefinition() {
        return new BlockStorageDefinition();
    }

    public static class BlockStorageBuilder extends RootNodeBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
