package org.opentosca.toscana.retrofit.model;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Platform extends HALResource {
    private String id;
    private String name;
    private boolean supportsDeployment;

    public Platform(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("supportsDeployment") boolean supportsDeployment
    ) {
        this.id = id;
        this.name = name;
        this.supportsDeployment = supportsDeployment;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("supportsDeployment")
    public boolean supportsDeployment() {
        return supportsDeployment;
    }
}
