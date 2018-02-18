package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class OutputsResponse extends HiddenResourceSupport {

    private final List<OutputWrap> outputs;

    public OutputsResponse(@JsonProperty("outputs") List<OutputWrap> outputs) {
        this.outputs = outputs;
    }

    @ApiModelProperty(
        required = true,
        notes = "The list of properties associated with this transformation, if this list is empty, the transformation " +
            "doesn't have any properties to set."
    )
    @JsonProperty("outputs")
    public List<OutputWrap> getOutputs() {
        return outputs;
    }
}
