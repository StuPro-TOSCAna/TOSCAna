package org.opentosca.toscana.cli.restclient.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Csars {

    @SerializedName("csar")
    private List<Csar> csarList = new ArrayList<>();

    public Csars(List<Csar> csarList) {
        this.csarList = csarList;
    }

    public List<Csar> getCsar() {
        return csarList;
    }
}
