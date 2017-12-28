package org.opentosca.toscana.model.capability;

import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

/**
 The ContainerCapability indicates that the node can act as a container (or a host) for one or more other declared Node Types.
 */
@Data
public class ContainerCapability extends ComputeCapability {

    @Builder
    protected ContainerCapability(String resourceName,
                                  Integer numCpus,
                                  Double cpuFrequencyInGhz,
                                  Integer diskSizeInMB,
                                  Integer memSizeInMB,
                                  Set<Class<? extends RootNode>> validSourceTypes,
                                  Range occurrence) {
        super(resourceName, numCpus, cpuFrequencyInGhz, diskSizeInMB, memSizeInMB, validSourceTypes, occurrence);
    }

    public static ContainerCapability getFallback(ContainerCapability c) {
        return (c == null) ? ContainerCapability.builder().build() : c;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class ContainerCapabilityBuilder extends ComputeCapabilityBuilder {
    }
}
