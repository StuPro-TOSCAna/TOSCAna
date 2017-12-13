package org.opentosca.toscana.model;

import lombok.Data;

/**
 Inherit from this if there's need to describe the entity.
 */
@Data
public class DescribableEntity {

    /**
     The optional description of this.
     */
    private final String description;

    protected DescribableEntity() {
        description = "";
    }

    protected DescribableEntity(String description) {
        this.description = description == null ? "" : description;
    }
}
