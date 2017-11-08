package org.opentosca.toscana.cli.restclient.model;

import com.google.gson.annotations.SerializedName;

public class PlatformsResponse {

    @SerializedName("_embedded")
    private Platforms platforms;

    public PlatformsResponse(Platforms platforms) {
        this.platforms = platforms;
    }

    public Platforms getAllPlatforms() {
        return platforms;
    }
}

