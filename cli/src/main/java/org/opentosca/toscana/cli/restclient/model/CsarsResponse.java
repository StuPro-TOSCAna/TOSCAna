package org.opentosca.toscana.cli.restclient.model;

import com.google.gson.annotations.SerializedName;

public class CsarsResponse {

    @SerializedName("_embedded")
    private Csars csar;

    public CsarsResponse(Csars csars) {
        this.csar = csars;
    }

    public Csars getAllCsars() {
        return csar;
    }
}
