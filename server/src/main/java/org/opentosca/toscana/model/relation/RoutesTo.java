package org.opentosca.toscana.model.relation;

import lombok.Builder;
import lombok.Data;

/**
 Represents an intentional network routing between two Endpoints in different networks.
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 162)
 */
@Data
public class RoutesTo extends RootRelationship {

    @Builder
    protected RoutesTo(String description) {
        super(description);
    }

    public static class RoutesToBuilder extends RootRelationshipBuilder {
    }
}
