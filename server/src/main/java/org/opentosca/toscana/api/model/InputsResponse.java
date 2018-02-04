package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class InputsResponse extends HiddenResourceSupport {

    private final List<PropertyWrap> inputs;

    public InputsResponse(
        @JsonProperty("inputs") List<PropertyWrap> inputs
    ) {
        this.inputs = inputs;
    }

    @ApiModelProperty(
        required = true,
        notes = "The list of inputs associated with this transformation."
    )
    @JsonProperty("inputs")
    public List<PropertyWrap> getInputs() {
        return inputs;
    }
}
