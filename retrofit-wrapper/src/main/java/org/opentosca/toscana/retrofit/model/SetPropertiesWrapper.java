package org.opentosca.toscana.retrofit.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SetPropertiesWrapper {

    private Map<String, String> properties;

    public SetPropertiesWrapper(
        @JsonProperty("properties") Map<String, String> properties
    ) {
        this.properties = properties;
    }

    @JsonProperty("properties")
    public Map<String, String> getProperties() {
        return properties;
    }
}
