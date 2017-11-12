package org.opentosca.toscana.retrofit.model;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Platform extends HALResource {
    private String id;
    private String name;

    public Platform(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name
    ) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
}
