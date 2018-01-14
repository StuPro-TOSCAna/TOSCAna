package org.opentosca.toscana.model;

import java.util.Optional;

import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Inherit from this if there's need to describe the entity.
 */
@EqualsAndHashCode
@ToString
public abstract class DescribableEntity extends BaseToscaElement {

    /**
     The optional description of this entity
     */
    private String description;

    public DescribableEntity(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    /**
     @return {@link #description}
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     Sets {@link #description}
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
