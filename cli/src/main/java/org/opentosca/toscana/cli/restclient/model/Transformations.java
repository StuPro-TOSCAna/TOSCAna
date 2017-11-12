package org.opentosca.toscana.cli.restclient.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Transformations {

    @SerializedName("transformation")
    private List<Transformation> transformationList = new ArrayList<>();

    public Transformations(List<Transformation> transformationList) {
        this.transformationList = transformationList;
    }

    public List<Transformation> getTransformation() {
        return transformationList;
    }
}
