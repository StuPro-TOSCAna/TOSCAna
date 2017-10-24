package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SetPropertiesRequest {
    private Map<String, String> properties;

    public SetPropertiesRequest(
        @JsonProperty("properties") Map<String, String> properties
    ) {
        this.properties = properties;
    }

    @JsonProperty("properties")
    public Map<String, String> getProperties() {
        return properties;
    }
}
