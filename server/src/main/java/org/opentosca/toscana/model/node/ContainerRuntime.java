package org.opentosca.toscana.model.node;

import java.util.Objects;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents operating system-level virtualization technology used to run
 multiple application services on a single {@link Compute} host.
 (TOSCA Simple Profile in YAML Version 1.1, p. 176)
 */
@Data
public class ContainerRuntime extends SoftwareComponent {

    private final ContainerCapability containerHost;

    private final ScalableCapability scalable;

    @Builder
    private ContainerRuntime(HostRequirement host,
                             ContainerCapability containerHost,
                             ScalableCapability scalable,
                             String componentVersion,
                             Credential adminCredential,
                             String nodeName,
                             StandardLifecycle standardLifecycle,
                             String description) {
        super(componentVersion, adminCredential, host, nodeName, standardLifecycle, description);
        this.containerHost = Objects.requireNonNull(containerHost);
        this.scalable = Objects.requireNonNull(scalable);

        capabilities.add(containerHost);
        capabilities.add(scalable);
    }

    /**
     @param nodeName      {@link #nodeName}
     @param host          {@link #host}
     @param containerHost {@link #containerHost}
     @param scalable      {@link #scalable}
     */
    public static ContainerRuntimeBuilder builder(String nodeName,
                                                  HostRequirement host,
                                                  ContainerCapability containerHost,
                                                  ScalableCapability scalable) {
        return new ContainerRuntimeBuilder()
            .nodeName(nodeName)
            .scalable(scalable)
            .host(host)
            .containerHost(containerHost);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
