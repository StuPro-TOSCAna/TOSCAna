package org.opentosca.toscana.model.relation;

import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.visitor.VisitableRelationship;

import lombok.Data;

/**
 The default TOSCA Relationship Type that all other TOSCA Relationships derive from.
 (TOSCA Simple Profile in YAML Version 1.1, p. 159)
 */
@Data
public abstract class RootRelationship extends DescribableEntity implements VisitableRelationship {

    protected RootRelationship(String description) {
        super(description);
    }
}
