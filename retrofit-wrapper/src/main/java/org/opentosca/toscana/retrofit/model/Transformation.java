package org.opentosca.toscana.retrofit.model;

import java.util.List;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transformation extends HALResource {
    private String status;
    private String platform;
    private List<ExecutionPhase> phases;

    public Transformation(
        @JsonProperty("status") String status,
        @JsonProperty("phases") List<ExecutionPhase> phases,
        @JsonProperty("platform") String platform
    ) {
        this.status = status;
        this.phases = phases;
        this.platform = platform;
    }

    @JsonProperty("status")
    public TransformationState getStatus() {
        return TransformationState.valueOf(status);
    }

    @JsonProperty("phases")
    public List<ExecutionPhase> getPhases() {
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
