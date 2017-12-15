package org.opentosca.toscana.model.visitor;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.NetworkCapability;
import org.opentosca.toscana.model.capability.NodeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.PublicEndpointCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.capability.StorageCapability;

public interface CapabilityVisitor {
    default void visit(AdminEndpointCapability capability) {
        // noop
    }

    default void visit(AttachmentCapability capability) {
        // noop
    }

    default void visit(BindableCapability capability) {
        // noop
    }

    default void visit(ComputeCapability capability) {
        // noop
    }

    default void visit(ContainerCapability capability) {
        // noop
    }

    default void visit(DatabaseEndpointCapability capability) {
        // noop
    }

    default void visit(DockerContainerCapability capability) {
        // noop
    }

    default void visit(EndpointCapability capability) {
        // noop
    }

    default void visit(NetworkCapability capability) {
        // noop
    }

    default void visit(NodeCapability capability) {
        // noop
    }

    default void visit(OsCapability capability) {
        // noop
    }

    default void visit(PublicEndpointCapability capability) {
        // noop
    }

    default void visit(ScalableCapability capability) {
        // noop
    }

    default void visit(StorageCapability capability) {
        // noop
    }
}
