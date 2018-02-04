package org.opentosca.toscana.core.transformation.properties;

import java.util.Optional;

public interface OutputProperty {
    Optional<String> getValue();

    String getKey();

    PropertyType getType();

    Optional<String> getDescription();
}
