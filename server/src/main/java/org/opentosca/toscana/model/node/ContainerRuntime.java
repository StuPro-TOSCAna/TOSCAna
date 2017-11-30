package org.opentosca.toscana.model.node;

import java.util.Objects;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 Represents operating system-level virtualization technology used to run
 multiple application services on a single {@link Compute} host.
 (TOSCA Simple Profile in YAML Version 1.1, p. 176)
 */
@Data
public class ContainerRuntime extends SoftwareComponent {

    @Getter(AccessLevel.NONE)
    public final ContainerCapability host;

    private final ScalableCapability scalable;

    @Builder
    private ContainerRuntime(ContainerCapability host,
                             ScalableCapability scalable,
                             String componentVersion,
                             Credential adminCredential,
                             String nodeName,
                             StandardLifecycle standardLifecycle,
                             String description) {
        super(componentVersion, adminCredential, nodeName, standardLifecycle, description);
        this.host = Objects.requireNonNull(host);
        this.scalable = Objects.requireNonNull(scalable);

        capabilities.add(host);
        capabilities.add(scalable);
    }

    /**
     @param nodeName {@link #nodeName}
     @param host     {@link #host}
     @param scalable {@link #scalable}
     */
    public static ContainerRuntimeBuilder builder(String nodeName,
                                                  ContainerCapability host,
                                                  ScalableCapability scalable) {
        return new ContainerRuntimeBuilder()
            .nodeName(nodeName)
            .scalable(scalable)
            .host(host);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
