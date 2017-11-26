package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.model.DescribableEntity;

import lombok.Builder;
import lombok.Data;

/**
 The default TOSCA Relationship Type that all other TOSCA Relationships derive from.
 (TOSCA Simple Profile in YAML Version 1.1, p. 159)
 */
@Data
public class RootRelationship extends DescribableEntity {

    @Builder
    public RootRelationship(String description) {
        super(description);
    }
}
