package org.opentosca.toscana.model.node;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

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

    public static ToscaKey<ContainerCapability> CONTAINER_HOST = new ToscaKey<>(CAPABILITIES, "host")
        .type(ContainerCapability.class);
    public static ToscaKey<ScalableCapability> SCALABLE = new ToscaKey<>(CAPABILITIES, "scalable")
        .type(ScalableCapability.class);

    public ContainerRuntime(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(CONTAINER_HOST, new ContainerCapability(getChildEntity(CONTAINER_HOST)));
        setDefault(SCALABLE, new ScalableCapability(getChildEntity(SCALABLE)));
    }

    /**
     @return {@link #CONTAINER_HOST}
     */
    public ContainerCapability getContainerHost() {

        return get(CONTAINER_HOST);
    }

    /**
     Sets {@link #CONTAINER_HOST}
     */
    public ContainerRuntime setContainerHost(ContainerCapability containerHost) {
        set(CONTAINER_HOST, containerHost);
        return this;
    }

    /**
     @return {@link #SCALABLE}
     */
    public ScalableCapability getScalable() {
        return get(SCALABLE);
    }

    /**
     Sets {@link #SCALABLE}
     */
    public ContainerRuntime setScalable(ScalableCapability scalable) {
        set(SCALABLE, scalable);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
