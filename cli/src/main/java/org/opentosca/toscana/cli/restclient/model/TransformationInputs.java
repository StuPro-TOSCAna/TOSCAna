package org.opentosca.toscana.cli.restclient.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TransformationInputs {

    @SerializedName("properties")
    private List<TransformationInput> inputList = new ArrayList<>();

    /**
     *
     * @param inputList
     */
    public TransformationInputs(List<TransformationInput> inputList) {
        this.inputList = inputList;
    }

    public List<TransformationInput> getProperties() {
        return inputList;
    }
}
