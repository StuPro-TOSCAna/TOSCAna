package org.opentosca.toscana.cli.restclient.model;

import com.google.gson.annotations.SerializedName;

public class TransformationsResponse {

    @SerializedName("_embedded")
    private Transformations transformations;

    public TransformationsResponse(Transformations transformations) {
        this.transformations = transformations;
    }

    public Transformations getAllTransformations() {
        return transformations;
    }
}
