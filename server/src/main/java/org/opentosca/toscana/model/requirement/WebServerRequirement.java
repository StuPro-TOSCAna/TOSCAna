package org.opentosca.toscana.model.requirement;

import java.util.Set;

import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.relation.HostedOn;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
public class WebServerRequirement extends Requirement<ContainerCapability, WebServer, HostedOn> {

    @Builder
    protected WebServerRequirement(ContainerCapability capability,
                                   Range occurrence,
                                   @Singular Set<WebServer> fulfillers,
                                   HostedOn relationship) {
        super(ContainerCapability.getFallback(capability), occurrence, fulfillers, HostedOn.getFallback(relationship));
    }
}
