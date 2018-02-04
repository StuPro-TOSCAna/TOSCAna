package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.TransformationController;
import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel
public class GetInputsResponse extends HiddenResourceSupport {

    private final List<PropertyWrap> inputs;

    public GetInputsResponse(
        String csarName,
        String platformName,
        @JsonProperty("inputs") List<PropertyWrap> inputs
    ) {
        this.inputs = inputs;
        add(ControllerLinkBuilder.linkTo(methodOn(TransformationController.class)
            .getInputs(null, null))
            .withSelfRel().expand(csarName, platformName)
        );
    }

    @ApiModelProperty(
        required = true,
        notes = "The list of properties associated with this transformation, if this list is empty, the transformation " +
            "doesn't have any properties to set."
    )
    @JsonProperty("inputs")
    public List<PropertyWrap> getInputs() {
        return inputs;
    }
}
