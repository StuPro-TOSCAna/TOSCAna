package org.opentosca.toscana.retrofit.model;

import java.util.List;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Csar extends HALResource {
    private String name;
    private List<LifecyclePhase> phases;

    public Csar(
        @JsonProperty("name") String name,
        @JsonProperty("phases") List<LifecyclePhase> phases
    ) {
        this.name = name;
        this.phases = phases;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("phases")
    public List<LifecyclePhase> getPhases() {
        return phases;
    }
}
