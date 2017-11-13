package org.opentosca.toscana.retrofit.model;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Csar extends HALResource {
    private String name;

    public Csar(
        @JsonProperty("name") String name
    ) {
        this.name = name;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
}
