package org.opentosca.toscana.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ValidationPropertyWrap extends PropertyWrap {

    private boolean valid;

    public ValidationPropertyWrap(
        PropertyWrap w,
        @JsonProperty("valid") boolean valid
    ) {
        super(w.key, w.type, w.description, w.value, w.required, w.defaultValue);
        this.valid = valid;
    }

    @ApiModelProperty(
        required = true,
        notes = "true if the property is valid, false otherwise"
    )
    @JsonProperty("valid")
    public boolean isValid() {
        return valid;
    }
}
