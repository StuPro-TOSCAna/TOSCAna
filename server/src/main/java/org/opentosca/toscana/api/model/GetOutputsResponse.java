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
public class GetOutputsResponse extends HiddenResourceSupport {

    private List<OutputWrap> outputs;

    public GetOutputsResponse(
        String csarId,
        String platformId,
        @JsonProperty("outputs") List<OutputWrap> outputs
    ) {
        this.outputs = outputs;
        add(
            linkTo(
                methodOn(TransformationController.class).getOutputs(null, null)
            ).withSelfRel().expand(csarId, platformId)
        );
    }

    @ApiModelProperty(
        required = true,
        notes = "The list of outputs associated with this transformation, if this list is empty, the transformation " +
            "doesn't have any outputs."
    )
    @JsonProperty("outputs")
    public List<OutputWrap> getOutputs() {
        return outputs;
    }
}
