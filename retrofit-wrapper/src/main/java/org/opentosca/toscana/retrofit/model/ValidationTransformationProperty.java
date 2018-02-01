package org.opentosca.toscana.retrofit.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationTransformationProperty extends TransformationProperty {

    private boolean valid;

    public ValidationTransformationProperty(
        @JsonProperty("valid") boolean valid
    ) {
        this.valid = valid;
    }

    @JsonProperty("valid")
    public boolean isValid() {
        return valid;
    }
}
