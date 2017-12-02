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

/**
 Unimplemented methods throw an {@link UnsupportedTypeException} when invoked.
 */
public interface StrictCapabilityVisitor extends CapabilityVisitor {

    @Override
    default void visit(AdminEndpointCapability capability) {
        throw new UnsupportedTypeException(AdminEndpointCapability.class);
    }

    @Override
    default void visit(AttachmentCapability capability) {
        throw new UnsupportedTypeException(AttachmentCapability.class);
    }

    @Override
    default void visit(BindableCapability capability) {
        throw new UnsupportedTypeException(BindableCapability.class);
    }

    @Override
    default void visit(ComputeCapability capability) {
        throw new UnsupportedTypeException(ComputeCapability.class);
    }

    @Override
    default void visit(ContainerCapability capability) {
        throw new UnsupportedTypeException(ContainerCapability.class);
    }

    @Override
    default void visit(DatabaseEndpointCapability capability) {
        throw new UnsupportedTypeException(DatabaseEndpointCapability.class);
    }

    @Override
    default void visit(DockerContainerCapability capability) {
        throw new UnsupportedTypeException(DockerContainerCapability.class);
    }

    @Override
    default void visit(EndpointCapability capability) {
        throw new UnsupportedTypeException(EndpointCapability.class);
    }

    @Override
    default void visit(NetworkCapability capability) {
        throw new UnsupportedTypeException(NetworkCapability.class);
    }

    @Override
    default void visit(NodeCapability capability) {
        throw new UnsupportedTypeException(NodeCapability.class);
    }

    @Override
    default void visit(OsCapability capability) {
        throw new UnsupportedTypeException(OsCapability.class);
    }

    @Override
    default void visit(PublicEndpointCapability capability) {
        throw new UnsupportedTypeException(PublicEndpointCapability.class);
    }

    @Override
    default void visit(ScalableCapability capability) {
        throw new UnsupportedTypeException(ScalableCapability.class);
    }

    @Override
    default void visit(StorageCapability capability) {
        throw new UnsupportedTypeException(StorageCapability.class);
    }
}
