package org.opentosca.toscana.retrofit.model;

import java.util.List;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transformation extends HALResource {
    private TransformationState state;
    private String platform;
    private List<LifecyclePhase> phases;

    public Transformation(
        @JsonProperty("state") TransformationState state,
        @JsonProperty("phases") List<LifecyclePhase> phases,
        @JsonProperty("platform") String platform
    ) {
        this.state = state;
        this.phases = phases;
        this.platform = platform;
    }

    @JsonProperty("state")
    public TransformationState getState() {
        return state;
    }

    @JsonProperty("phases")
    public List<LifecyclePhase> getPhases() {
        return phases;
    }

    @JsonProperty("platform")
    public String getPlatform() {
        return platform;
    }

    public enum TransformationState {
        READY,
        INPUT_REQUIRED,
        TRANSFORMING,
        DONE,
        ERROR
    }
}
