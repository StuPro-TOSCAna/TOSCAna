package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.TransformationController;
import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel
public class OutputResponse extends HiddenResourceSupport {

    private List<PropertyWrap> outputs;

    public OutputResponse(
        String csarName,
        String platformName,
        @JsonProperty("outputs") List<PropertyWrap> outputs
    ) {
        this.outputs = outputs;
        add(
            linkTo(
                methodOn(TransformationController.class).getOutputs(null, null)
            ).withSelfRel().expand(csarName, platformName)
        );
    }

    @ApiModelProperty(
        required = true,
        notes = "The list of outputs associated with this transformation, if this list is empty, the transformation " +
            "doesn't have any outputs."
    )
    @JsonProperty("outputs")
    public List<PropertyWrap> getOutputs() {
        return outputs;
    }
}
