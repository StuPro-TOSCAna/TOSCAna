package org.opentosca.toscana.retrofit.model;

import org.opentosca.toscana.retrofit.model.hal.HALResource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformationArtifact extends HALResource {
    private String url;

    public TransformationArtifact(
        @JsonProperty("access_url") String url
    ) {
        this.url = url;
    }

    @JsonProperty("access_url")
    public String getUrl() {
        return url;
    }
}
