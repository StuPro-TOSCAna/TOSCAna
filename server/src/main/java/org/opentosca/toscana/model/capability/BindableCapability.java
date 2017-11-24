package org.opentosca.toscana.model.capability;

import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 A node that has the BindableCapability indicates that it can be bound to a logical network association via a network port.
 (TOSCA Simple Profile in YAML Version 1.1, p. 159)
 */
@Data
public class BindableCapability extends NodeCapability {

    @Builder
    protected BindableCapability(@Singular Set<Class<? extends RootNode>> validSourceTypes,
                                 Range occurence,
                                 String description) {
        super(validSourceTypes, occurence, description);
    }

    public static class BindableCapabilityBuilder extends NodeCapabilityBuilder {
    }
}
