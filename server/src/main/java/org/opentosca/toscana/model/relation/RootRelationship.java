package org.opentosca.toscana.model.relation;

import java.util.UUID;

import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.visitor.VisitableRelationship;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

/**
 The default TOSCA Relationship Type that all other TOSCA Relationships derive from.
 (TOSCA Simple Profile in YAML Version 1.1, p. 159)
 */
@Data
public abstract class RootRelationship extends DescribableEntity implements VisitableRelationship {

    @Getter(AccessLevel.NONE)
    private String uid = UUID.randomUUID().toString();
    
    protected RootRelationship(String description) {
        super(description);
    }
}
