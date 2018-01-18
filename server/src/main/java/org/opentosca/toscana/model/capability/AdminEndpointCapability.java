package org.opentosca.toscana.model.capability;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The default TOSCA type that should be used or extended to define a specialized administrator endpoint capability.
 (TOSCA Simple Profile in YAML Version 1.1, p. 155)
 <p>
 Note: TOSCA Orchestrator handling for this type SHALL assure
 that network-level security is enforced if possible.
 (TOSCA Simple Profile in YAML Version 1.1, p. 156)
 */
@EqualsAndHashCode
@ToString
public class AdminEndpointCapability extends EndpointCapability {

    public AdminEndpointCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
