package org.opentosca.toscana.model;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Inherit from this if there's need to describe the entity.
 */
@EqualsAndHashCode
@ToString
public abstract class DescribableElement extends BaseToscaElement {

    /**
     The optional description of this entity
     */
    public static ToscaKey<String> DESCRIPTION = new ToscaKey<>("description");

    public DescribableElement(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    /**
     @return {@link #DESCRIPTION}
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(get(DESCRIPTION));
    }

    /**
     Sets {@link #DESCRIPTION}
     */
    public DescribableElement setDescription(String description) {
        set(DESCRIPTION, description);
        return this;
    }
}
