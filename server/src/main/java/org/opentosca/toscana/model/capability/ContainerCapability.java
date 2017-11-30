package org.opentosca.toscana.model.capability;

import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;
import org.opentosca.toscana.model.visitor.Visitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 The ContainerCapability indicates that the node can act as a container (or a host) for one or more other declared Node Types.
 */
@Data
public class ContainerCapability extends ComputeCapability {

    @Builder
    protected ContainerCapability(String name,
                                  Integer numCpus,
                                  Double cpuFrequencyInGhz,
                                  Integer diskSizeInMB,
                                  Integer memSizeInMB,
                                  @Singular Set<Class<? extends RootNode>> validSourceTypes,
                                  Range occurence,
                                  String description) {
        super(name, numCpus, cpuFrequencyInGhz, diskSizeInMB, memSizeInMB, validSourceTypes, occurence, description);
    }

    public static class ContainerCapabilityBuilder extends ComputeCapabilityBuilder {
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
