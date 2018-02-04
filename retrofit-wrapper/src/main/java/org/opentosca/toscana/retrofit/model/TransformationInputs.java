package org.opentosca.toscana.retrofit.model;

import java.util.List;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformationInputs extends HALResource {

    private List<TransformationProperty> inputs;

    public TransformationInputs(@JsonProperty("inputs") List<TransformationProperty> inputs) {
        this.inputs = inputs;
    }

    @JsonProperty("inputs")
    public List<TransformationProperty> getInputs() {
        return inputs;
    }
}
