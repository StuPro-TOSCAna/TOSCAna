package org.opentosca.toscana.cli.restclient.model;

import com.google.gson.annotations.SerializedName;

public class TransformationArtifact {

    @SerializedName("access_url")
    private String accessUrl;

    /**
     *
     * @param accessUrl
     */
    public TransformationArtifact(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getAccessUrl() {
        return accessUrl;
    }
}
