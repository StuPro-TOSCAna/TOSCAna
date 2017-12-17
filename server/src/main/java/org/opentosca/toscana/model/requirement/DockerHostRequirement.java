package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.relation.HostedOn;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class DockerHostRequirement extends Requirement<DockerContainerCapability, ContainerRuntime, HostedOn> {

    @Builder
    protected DockerHostRequirement(DockerContainerCapability capability,
                                    Range occurrence,
                                    @Singular Set<ContainerRuntime> fulfillers,
                                    HostedOn relationship) {
        super(DockerContainerCapability.getFallback(capability), occurrence,
            fulfillers, HostedOn.getFallback(relationship));
    }

    public static DockerHostRequirement getFallback(DockerHostRequirement r) {
        return (r == null) ? builder().build() : r;
    }
}
