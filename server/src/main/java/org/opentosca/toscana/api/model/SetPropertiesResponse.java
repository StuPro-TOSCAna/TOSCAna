package org.opentosca.toscana.api.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class SetPropertiesResponse {
    private final Map<String, Boolean> validInputs;

    public SetPropertiesResponse(
        @JsonProperty("valid_inputs") Map<String, Boolean> validInputs
    ) {
        this.validInputs = validInputs;
    }

    @ApiModelProperty(
        required = true,
        notes = "Key - Boolean map, telling you which values have been set properly (true) and which ones have failed (false)."
    )
    @JsonProperty("valid_inputs")
    public Map<String, Boolean> getValidInputs() {
        return validInputs;
    }
}
