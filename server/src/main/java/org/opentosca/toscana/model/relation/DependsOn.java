package org.opentosca.toscana.model.relation;

import lombok.Builder;
import lombok.Data;

/**
 Represents a general dependency relationship between two nodes.
 */
@Data
public class DependsOn extends RootRelationship {

    @Builder
    protected DependsOn(String description) {
        super(description);
    }

    public static class DependsOnBuilder extends RootRelationshipBuilder {
    }
}
