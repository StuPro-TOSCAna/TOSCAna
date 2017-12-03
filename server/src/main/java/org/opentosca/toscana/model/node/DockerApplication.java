package org.opentosca.toscana.model.node;

import java.util.Objects;

import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.requirement.EndpointRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class DockerApplication extends ContainerApplication {

    // public access due to hiding of parent field (and therefore getter conflicts..)
    @Getter(AccessLevel.NONE)
    public final Requirement<DockerContainerCapability, ContainerRuntime, HostedOn> host;

    @Builder
    private DockerApplication(Requirement<DockerContainerCapability, ContainerRuntime, HostedOn> host,
                              Requirement<StorageCapability, RootNode, RootRelationship> storage,
                              EndpointRequirement network,
                              String nodeName,
                              StandardLifecycle standardLifecycle,
                              String description) {
        super(storage, network, nodeName, standardLifecycle, description);
        this.host = Objects.requireNonNull(host);

        requirements.add(host);
    }

    /**
     @param nodeName {@link #nodeName}
     @param host     {@link #host}
     @param network  {@link #network}
     @param storage  {@link #storage}
     */
    public static DockerApplicationBuilder builder(Requirement<DockerContainerCapability, ContainerRuntime, HostedOn> host,
                                                   String nodeName,
                                                   EndpointRequirement network,
                                                   Requirement<StorageCapability, RootNode, RootRelationship> storage) {
        return new DockerApplicationBuilder()
            .nodeName(nodeName)
            .host(host)
            .network(network)
            .storage(storage);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
