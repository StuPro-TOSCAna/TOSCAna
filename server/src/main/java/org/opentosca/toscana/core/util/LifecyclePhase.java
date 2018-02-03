package org.opentosca.toscana.core.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class LifecyclePhase {

    private final String name;
    private final Lifecycle lifecycle;
    private State state = State.PENDING;

    public LifecyclePhase(String name, Lifecycle lifecycle) {
        this.name = name;
        this.lifecycle = lifecycle;
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
        if (state == State.FAILED) {
            setSuccessorsToSkipped();
        }
    }

    private void setSuccessorsToSkipped() {
        boolean passedSelf = false;
        for (LifecyclePhase phase : lifecycle.getLifecyclePhases()) {
            if (passedSelf) {
                phase.setState(State.SKIPPED);
            } else {
                if (phase == this) {
                    passedSelf = true;
                }
            }
        }
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
