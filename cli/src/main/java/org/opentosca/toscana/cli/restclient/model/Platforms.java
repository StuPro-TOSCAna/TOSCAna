package org.opentosca.toscana.cli.restclient.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Platforms {

    @SerializedName("platform")
    private List<Platform> platformList = new ArrayList<>();

    public Platforms(List<Platform> platformList) {
        this.platformList = platformList;
    }

    public List<Platform> getPlatform() {
        return platformList;
    }
}
