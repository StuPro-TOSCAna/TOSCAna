package org.opentosca.toscana.model.capability;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The default TOSCA type that should be used or extended to define a specialized database endpoint capability.
 (TOSCA Simple Profile in YAML Version 1.1, p. 156)
 */
@EqualsAndHashCode
@ToString
public class DatabaseEndpointCapability extends EndpointCapability {

    public DatabaseEndpointCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
