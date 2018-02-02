package org.opentosca.toscana.retrofit.model;

import java.util.List;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AbstractTransformationProperties<E extends TransformationProperty> extends HALResource {
    
    private List<E> properties;

    public AbstractTransformationProperties(
        @JsonProperty("properties") List<E> properties
    ) {
        this.properties = properties;
    }

    @JsonProperty("properties")
    public List<E> getProperties() {
        return properties;
    }
}
