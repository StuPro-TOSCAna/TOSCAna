package org.opentosca.toscana.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 Inherit from this if there's need to describe the entity.
 */
@Data
public abstract class DescribableEntity extends AbstractEntity implements Serializable {

    /**
     The optional description of this.
     */
    private final String description;

    public DescribableEntity() {
        description = "";
    }

    @Builder
    public DescribableEntity(String description) {
        this.description = description == null ? "" : description;
    }

    public static class DescribableEntityBuilder extends AbstractEntityBuilder implements Serializable {

        public DescribableEntityBuilder() {
        }

        @Override
        public DescribableEntity build() {
            // should never happen
            throw new IllegalStateException();
        }
    }
}
