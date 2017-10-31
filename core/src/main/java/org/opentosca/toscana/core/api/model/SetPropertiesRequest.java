package org.opentosca.toscana.core.api.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class SetPropertiesRequest {
    private final Map<String, String> properties;

    public SetPropertiesRequest(
        @JsonProperty("properties") Map<String, String> properties
    ) {
        this.properties = properties;
    }

    @ApiModelProperty(
        required = true,
        notes = "Represents a Key-Value map (String to string) that associates one Property key " +
            "(received by calling get Properties) with a value, the value then gets processed by the the server " +
            "and the values get set if the input is valid"
    )
    @JsonProperty("properties")
    public Map<String, String> getProperties() {
        return properties;
    }
}
