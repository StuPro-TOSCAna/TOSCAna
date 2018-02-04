package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class InputsResponse extends HiddenResourceSupport {

    private final List<InputWrap> inputs;

    public InputsResponse(@JsonProperty("inputs") List<InputWrap> inputs) {
        this.inputs = inputs;
    }

    @ApiModelProperty(
        required = true,
        notes = "The list of properties associated with this transformation, if this list is empty, the transformation " +
            "doesn't have any properties to set."
    )
    @JsonProperty("inputs")
    public List<InputWrap> getInputs() {
        return inputs;
    }
}
