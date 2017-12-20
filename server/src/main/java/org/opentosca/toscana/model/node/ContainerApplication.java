package org.opentosca.toscana.model.node;

import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.ContainerHostRequirement;
import org.opentosca.toscana.model.requirement.EndpointRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.requirement.StorageRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 Represents an application that requires Container-level virtualization technology.
 (TOSCA1.1, p. 177)
 */
@Data
public class ContainerApplication extends RootNode {

    // public access due to hiding of field in subclasses (with different type):
    // here, getters can't be overridden. 
    public final Requirement<ContainerCapability, ContainerRuntime, HostedOn> host;
    private final Requirement<StorageCapability, RootNode, RootRelationship> storage;
    private final Requirement<EndpointCapability, RootNode, RootRelationship> network;

    @Builder
    protected ContainerApplication(Requirement<ContainerCapability, ContainerRuntime, HostedOn> host,
                                   Requirement<StorageCapability, RootNode, RootRelationship> storage,
                                   Requirement<EndpointCapability, RootNode, RootRelationship> network,
                                   String nodeName,
                                   StandardLifecycle standardLifecycle,
                                   Set<Requirement> requirements,
                                   Set<Capability> capabilities,
                                   String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        this.storage = StorageRequirement.getFallback(storage);
        this.host = ContainerHostRequirement.getFallback(host);
        this.network = EndpointRequirement.getFallback(network);

        this.requirements.add(this.host);
        this.requirements.add(this.storage);
        this.requirements.add(this.network);
    }

    /**
     Only use when subclass is shadowing the `host` field.
     */
    protected ContainerApplication(Requirement<StorageCapability, RootNode, RootRelationship> storage,
                                   Requirement<EndpointCapability, RootNode, RootRelationship> network,
                                   String nodeName,
                                   StandardLifecycle standardLifecycle,
                                   @Singular Set<Requirement> requirements,
                                   @Singular Set<Capability> capabilities,
                                   String description) {
        super(nodeName, standardLifecycle, requirements, capabilities, description);
        this.host = null; // this is a hack. field shall not be used because its shadowed by the subclass
        this.storage = StorageRequirement.getFallback(storage);
        this.network = EndpointRequirement.getFallback(network);

        requirements.add(this.storage);
        requirements.add(this.network);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    @Builder
    public static ContainerApplicationBuilder builder(String nodeName) {
        return new ContainerApplicationBuilder()
            .nodeName(nodeName);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public static class ContainerApplicationBuilder extends RootNodeBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
