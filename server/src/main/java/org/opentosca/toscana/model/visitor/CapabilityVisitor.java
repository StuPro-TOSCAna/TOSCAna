package org.opentosca.toscana.model.visitor;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.Capability;
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

public interface CapabilityVisitor extends Visitor{
    
    default void visit(AdminEndpointCapability capability){
        throw new UnsupportedTypeException(AdminEndpointCapability.class);
    }
    default void visit(AttachmentCapability capability){
        throw new UnsupportedTypeException(AttachmentCapability.class);
    }
    default void visit(BindableCapability capability){
        throw new UnsupportedTypeException(BindableCapability.class);
    }
    default void visit(Capability capability){
        throw new UnsupportedTypeException(Capability.class);
    }
    default void visit(ComputeCapability capability){
        throw new UnsupportedTypeException(ComputeCapability.class);
    }
    default void visit(ContainerCapability capability){
        throw new UnsupportedTypeException(ContainerCapability.class);
    }
    default void visit(DatabaseEndpointCapability capability){
        throw new UnsupportedTypeException(DatabaseEndpointCapability.class);
    }
    default void visit(DockerContainerCapability capability){
        throw new UnsupportedTypeException(DockerContainerCapability.class);
    }
    default void visit(EndpointCapability capability){
        throw new UnsupportedTypeException(EndpointCapability.class);
    }
    default void visit(NetworkCapability capability){
        throw new UnsupportedTypeException(NetworkCapability.class);
    }
    default void visit(NodeCapability capability){
        throw new UnsupportedTypeException(NodeCapability.class);
    }
    default void visit(OsCapability capability){
        throw new UnsupportedTypeException(OsCapability.class);
    }
    default void visit(PublicEndpointCapability capability){
        throw new UnsupportedTypeException(PublicEndpointCapability.class);
    }
    default void visit(ScalableCapability capability){
        throw new UnsupportedTypeException(ScalableCapability.class);
    }
    default void visit(StorageCapability capability){
        throw new UnsupportedTypeException(StorageCapability.class);
    }
}
