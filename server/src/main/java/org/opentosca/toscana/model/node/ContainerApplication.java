package org.opentosca.toscana.model.node;

import java.util.Objects;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.requirement.ContainerHostRequirement;
import org.opentosca.toscana.model.requirement.EndpointRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.StorageRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents an application that requires Container-level virtualization technology.
 (TOSCA1.1, p. 177)
 */
@Data
public class ContainerApplication extends RootNode {

    // public access due to hiding of field in subclasses (with different type):
    // here, getters can't be overridden. 
    public final ContainerHostRequirement host;
    private final StorageRequirement storage;
    private final EndpointRequirement network;

    @Builder
    protected ContainerApplication(ContainerHostRequirement host,
                                   StorageRequirement storage,
                                   EndpointRequirement network,
                                   String nodeName,
                                   StandardLifecycle standardLifecycle,
                                   String description) {
        super(nodeName, standardLifecycle, description);
        this.host = Objects.requireNonNull(host);
        this.storage = Objects.requireNonNull(storage);
        this.network = Objects.requireNonNull(network);

        requirements.add(host);
        requirements.add(storage);
        requirements.add(network);
    }

    /**
     Only use when subclass is shadowing the `host` field.
     */
    protected ContainerApplication(StorageRequirement storage,
                                   EndpointRequirement network,
                                   String nodeName,
                                   StandardLifecycle standardLifecycle,
                                   String description) {
        super(nodeName, standardLifecycle, description);
        this.host = null; // this is a hack. field shall not be used because its shadowed by the subclass
        this.storage = Objects.requireNonNull(storage);
        this.network = Objects.requireNonNull(network);

        requirements.add(storage);
        requirements.add(network);
    }

    /**
     @param nodeName {@link #nodeName}
     @param host     {@link #host}
     @param network  {@link #network}
     @param network  {@link #storage}
     */
    public static ContainerApplicationBuilder builder(String nodeName,
                                                      ContainerHostRequirement host,
                                                      EndpointRequirement network,
                                                      StorageRequirement storage) {
        return new ContainerApplicationBuilder()
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
