package org.opentosca.toscana.model.node;

import java.util.Objects;
import java.util.Optional;

import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;

import static java.lang.String.format;

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
                         String description) {
        super(nodeName, standardLifecycle, description);
        if (sizeInMB != null && sizeInMB < 1) {
            throw new IllegalArgumentException(format("Constraint violation: sizeInMB >= 1; but was '%d'", sizeInMB));
        }
        this.sizeInMB = sizeInMB;
        this.volumeId = volumeId;
        this.snapshotId = snapshotId;
        this.attachment = Objects.requireNonNull(attachment);

        capabilities.add(attachment);
    }


    /**
     @param attachment {@link #attachment}
     @param lifecycle  {@link #standardLifecycle}
     @param snapshotId {@link #snapshotId}
     */
    public static BlockStorageBuilder builder(AttachmentCapability attachment,
                                              StandardLifecycle lifecycle,
                                              String snapshotId) {
        return new BlockStorageBuilder()
            .attachment(attachment)
            .standardLifecycle(lifecycle)
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
        return Optional.ofNullable(sizeInMB);
    }

    /**
     @return the optional ID of the volume
     */
    public Optional<String> getVolumeId() {
        return Optional.ofNullable(volumeId);
    }

    /**
     @return optional identifier of the existing snapshot which is used when creating the block storage
     */
    public Optional<String> getSnapshotId() {
        return Optional.ofNullable(snapshotId);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
