package org.opentosca.toscana.retrofit.model;

import java.util.List;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformationProperties extends HALResource {
    private List<TransformationProperty> properties;

    public TransformationProperties(
        @JsonProperty("properties")List<TransformationProperty> properties
    ) {
        this.properties = properties;
    }

    @JsonProperty("properties")
    public List<TransformationProperty> getProperties() {
        return properties;
    }
}
