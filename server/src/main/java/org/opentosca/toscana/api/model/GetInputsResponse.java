package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.TransformationController;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel
public class GetInputsResponse extends InputsResponse {

    public GetInputsResponse(
        String csarName,
        String platformName,
        @JsonProperty("inputs") List<InputWrap> inputs
    ) {
        super(inputs);
        add(ControllerLinkBuilder.linkTo(methodOn(TransformationController.class)
            .getInputs(null, null))
            .withSelfRel().expand(csarName, platformName)
        );
    }
}
