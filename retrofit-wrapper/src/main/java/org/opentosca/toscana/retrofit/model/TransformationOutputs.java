package org.opentosca.toscana.retrofit.model;

import java.util.List;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformationOutputs extends HALResource {
    private List<TransformationProperty> properties;

    public TransformationOutputs(
        @JsonProperty("outputs")List<TransformationProperty> properties
    ) {
        this.properties = properties;
    }

    @JsonProperty("outputs")
    public List<TransformationProperty> getProperties() {
        return properties;
    }
}
