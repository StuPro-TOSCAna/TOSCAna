package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.datatype.SizeUnit;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@EqualsAndHashCode
@ToString
public class BlockStorage extends RootNode {

    /**
     The requested storage size in MB.
     <p>
     Note : Required when an existing volume ({@link #VOLUME_ID}) is not available.
     <p>
     If {@link #VOLUME_ID} is provided, size is ignored.
     <p>
     Resize of existing volumes is not considered at this time.
     (TOSCA Simple Profile in YAML Version 1.1, p. 175)
     */
    public static ToscaKey<Integer> SIZE_IN_MB = new ToscaKey<>(PROPERTIES, "size")
        .type(Integer.class).directive(SizeUnit.FROM, SizeUnit.Unit.MB).directive(SizeUnit.TO, SizeUnit.Unit.MB);

    /**
     Optional ID of an existing volume (that is in the accessible scope of the requesting application).
     (TOSCA Simple Profile in YAML Version 1.1, p. 175)
     */
    public static ToscaKey<String> VOLUME_ID = new ToscaKey<>(PROPERTIES, "volume_id");

    /**
     Optional ID of an existing snapshot that should be used when creating the block storage (volume).
     (TOSCA Simple Profile in YAML Version 1.1, p. 175)
     */
    public static ToscaKey<String> SNAPSHOT_ID = new ToscaKey<>(PROPERTIES, "snapshot_id");

    public static ToscaKey<AttachmentCapability> ATTACHMENT = new ToscaKey<>(CAPABILITIES, "attachment")
        .type(AttachmentCapability.class);

    public BlockStorage(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(ATTACHMENT, new AttachmentCapability(getChildEntity(ATTACHMENT)));
    }

    /**
     @return {@link #ATTACHMENT}
     */
    public Optional<AttachmentCapability> getAttachment() {
        return Optional.ofNullable(get(ATTACHMENT));
    }

    /**
     Sets {@link #ATTACHMENT}
     */
    public BlockStorage setAttachment(AttachmentCapability attachment) {
        set(ATTACHMENT, attachment);
        return this;
    }

    /**
     @return {@link #SIZE_IN_MB}
     */
    public Optional<Integer> getSizeInMb() {
        return Optional.ofNullable(get(SIZE_IN_MB));
    }

    /**
     Sets {@link #SIZE_IN_MB}
     */
    public BlockStorage setSizeInMb(Integer sizeInMb) {
        if (sizeInMb < 1) {
            throw new IllegalArgumentException(format("Constraint violation: sizeInMB >= 1; but was '%d'", sizeInMb));
        }
        set(SIZE_IN_MB, sizeInMb);
        return this;
    }

    /**
     @return {@link #VOLUME_ID}
     */
    public Optional<String> getVolumeId() {
        return Optional.ofNullable(get(VOLUME_ID));
    }

    /**
     Sets {@link #VOLUME_ID}
     */
    public BlockStorage setVolumeId(String volumeId) {
        set(VOLUME_ID, volumeId);
        return this;
    }

    /**
     @return {@link #SNAPSHOT_ID}
     */
    public Optional<String> getSnapshotId() {
        return Optional.ofNullable(get(SNAPSHOT_ID));
    }

    /**
     Sets {@link #SNAPSHOT_ID}
     */
    public BlockStorage setSnapshotId(String snapshotId) {
        set(SNAPSHOT_ID, snapshotId);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
