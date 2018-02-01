package org.opentosca.toscana.core.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class LifecyclePhase {

    private final String name;
    private State state = State.PENDING;

    public LifecyclePhase(String name) {
        this.name = name;
    }

    @ApiModelProperty(
        required = true,
        notes = "the name of this execution phase",
        example = "deploy"
    )
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @ApiModelProperty(
        required = true,
        notes = "the current state of the phase. Must be one of { PENDING, SKIPPING, EXECUTING, SKIPPED, DONE, ERROR }",
        example = "PENDING"
    )
    @JsonProperty("state")
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        PENDING,
        SKIPPING,
        EXECUTING,
        DONE,
        SKIPPED,
        FAILED
    }
}
