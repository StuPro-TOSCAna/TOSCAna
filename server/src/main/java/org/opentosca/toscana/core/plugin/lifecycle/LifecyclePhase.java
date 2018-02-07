package org.opentosca.toscana.core.plugin.lifecycle;

import org.opentosca.toscana.core.util.LifecycleAccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;

public class LifecyclePhase {

    private final String name;
    private final LifecycleAccess lifecycle;
    private final Logger logger;
    private State state = State.PENDING;

    public LifecyclePhase(String name, LifecycleAccess lifecycle, Logger logger) {
        this.name = name;
        this.lifecycle = lifecycle;
        this.logger = logger;
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
        if (this.state == state) return;
        logger.info(String.format("%-20s  %-10s ==> %s", "Phase '" + this.name + "':", this.state, state));
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
