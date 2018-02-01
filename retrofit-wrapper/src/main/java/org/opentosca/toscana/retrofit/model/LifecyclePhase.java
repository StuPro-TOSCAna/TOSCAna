package org.opentosca.toscana.retrofit.model;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LifecyclePhase extends HALResource {

    private String name;
    private State state;

    public LifecyclePhase(
        @JsonProperty("name") String name,
        @JsonProperty("state") State state
    ) {
        this.name = name;
        this.state = state;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public State getState() {
        return state;
    }

    public enum State {
        PENDING,
        SKIPPING,
        EXECUTING,
        SKIPPED,
        DONE,
        FAILED
    }
}
