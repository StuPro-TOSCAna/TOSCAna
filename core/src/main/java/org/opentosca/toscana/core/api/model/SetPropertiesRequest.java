package org.opentosca.toscana.core.api.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

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
