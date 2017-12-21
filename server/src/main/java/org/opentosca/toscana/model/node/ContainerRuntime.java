package org.opentosca.toscana.model.node;

import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.nodedefinition.ContainerRuntimeDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents operating system-level virtualization technology used to run
 multiple application services on a single {@link Compute} host.
 (TOSCA Simple Profile in YAML Version 1.1, p. 176)
 */
@EqualsAndHashCode
@ToString
public class ContainerRuntime extends SoftwareComponent {

    private final ContainerCapability containerHost;

    private final ScalableCapability scalable;

    @Builder
    private ContainerRuntime(Requirement<ContainerCapability, Compute, HostedOn> host,
                             ContainerCapability containerHost,
                             ScalableCapability scalable,
                             String componentVersion,
                             Credential adminCredential,
                             String nodeName,
                             StandardLifecycle standardLifecycle,
                             Set<Requirement> requirements,
                             Set<Capability> capabilities,
                             String description) {
        super(componentVersion, adminCredential, host, nodeName, standardLifecycle, requirements, capabilities, description);
        this.containerHost = (containerHost == null) ? ContainerCapability.builder().build() : containerHost;
        this.scalable = (scalable == null) ? ScalableCapability.builder().build() : scalable;

        this.capabilities.add(containerHost);
        this.capabilities.add(scalable);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static ContainerRuntimeBuilder builder(String nodeName) {
        return new ContainerRuntimeBuilder()
            .nodeName(nodeName);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    @Override
    protected AbstractDefinition getDefinition() {
        return new ContainerRuntimeDefinition();
    }

    public static class ContainerRuntimeBuilder extends SoftwareComponentBuilder {
        protected Set<Requirement> requirements = super.requirements;
        protected Set<Capability> capabilities = super.capabilities;
    }
}
