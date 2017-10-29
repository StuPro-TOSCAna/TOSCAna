package org.opentosca.toscana.core.api.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SetPropertiesResponse {
    private Map<String, Boolean> validInputs;

    public SetPropertiesResponse(
        @JsonProperty("valid_inputs") Map<String, Boolean> validInputs
    ) {
        this.validInputs = validInputs;
    }

    @JsonProperty("valid_inputs")
    public Map<String, Boolean> getValidInputs() {
        return validInputs;
    }
}
