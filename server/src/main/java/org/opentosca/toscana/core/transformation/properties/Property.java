package org.opentosca.toscana.core.transformation.properties;

import java.util.Optional;

public interface Property {
    Optional<String> getValue();

    void setValue(String value);

    String getKey();

    PropertyType getType();

    Optional<String> getDescription();

    boolean isRequired();

    Optional<String> getDefaultValue();
}
