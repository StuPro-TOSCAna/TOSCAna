package org.opentosca.toscana.model.relation;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents an attachment relationship between two nodes.
 <p>
 For example, an AttachesTo relationship would be used for attaching
 a Storage node ({@link StorageCapability}) to a {@link Compute} node.
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 161)
 */
@EqualsAndHashCode
@ToString
public class AttachesTo extends RootRelationship {

    /**
     The relative location (e.g., path on the file system), which provides the root location to address an attached node.
     e.g., a mount point / path such as ‘/usr/data’
     <p>
     Note: The user must provide it and it cannot be “root”.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 161)
     */
    public static ToscaKey<String> MOUNT_POINT = new ToscaKey<>(PROPERTIES, "mount_point");

    /**
     The optional logical device name which for the attached device (which is represented by the target node in the
     model).
     <p>
     e.g., ‘/dev/hda1’
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 161)
     */
    public static ToscaKey<String> DEVICE = new ToscaKey<>(PROPERTIES, "device");

    public AttachesTo(MappingEntity entity) {
        super(entity);
    }

    /**
     @return {@link #MOUNT_POINT}
     */
    public String getMountPoint() {
        return get(MOUNT_POINT);
    }

    /**
     Sets {@link #MOUNT_POINT}
     */
    public void setMountPoint(String mountPoint) {
        set(MOUNT_POINT, mountPoint);
    }

    /**
     @return {@link #DEVICE}
     */

    public Optional<String> getDevice() {

        return Optional.ofNullable(get(DEVICE));
    }

    /**
     Sets {@link #DEVICE}
     */
    public void setDevice(String device) {
        set(DEVICE, device);
    }

    @Override
    public void accept(RelationshipVisitor v) {
        v.visit(this);
    }
}
