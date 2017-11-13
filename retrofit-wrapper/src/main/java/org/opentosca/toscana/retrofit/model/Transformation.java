package org.opentosca.toscana.retrofit.model;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transformation extends HALResource {
    private String status;
    private Long progress;
    private String platform;

    public Transformation(
        @JsonProperty("status") String status,
        @JsonProperty("progress") Long progress,
        @JsonProperty("platform") String platform
    ) {
        this.status = status;
        this.progress = progress;
        this.platform = platform;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("progress")
    public Long getProgress() {
        return progress;
    }

    @JsonProperty("platform")
    public String getPlatform() {
        return platform;
    }
}
