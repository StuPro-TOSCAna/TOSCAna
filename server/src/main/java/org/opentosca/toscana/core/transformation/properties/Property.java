package org.opentosca.toscana.core.transformation.properties;

import java.util.Optional;

public interface Property {
    default Optional<String> getValue() {
        return Optional.ofNullable(getValueWithoutDefault().orElse(getDefaultValue().orElse(null)));
    }

    Optional<String> getValueWithoutDefault();

    void setValue(String value);

    String getKey();

    PropertyType getType();

    Optional<String> getDescription();

    boolean isRequired();

    Optional<String> getDefaultValue();

    default boolean isValid() {
        if (getValueWithoutDefault().isPresent()) {
            return getType().validate(getValue().get());
        } else {
            return !isRequired() || getDefaultValue().isPresent();
        }
    }
}
