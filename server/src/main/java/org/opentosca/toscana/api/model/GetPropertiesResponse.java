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
public class GetPropertiesResponse extends HiddenResourceSupport {

    private final List<PropertyWrap> properties;

    public GetPropertiesResponse(
        String csarName,
        String platformName,
        @JsonProperty("properties") List<PropertyWrap> properties
    ) {
        this.properties = properties;
        add(ControllerLinkBuilder.linkTo(methodOn(TransformationController.class)
            .getTransformationProperties(null, null))
            .withSelfRel().expand(csarName, platformName)
        );
    }

    @ApiModelProperty(
        required = true,
        notes = "The list of properties associated with this transformation, if this list is empty, the transformation " +
            "doesn't have any properties to set."
    )
    @JsonProperty("properties")
    public List<PropertyWrap> getProperties() {
        return properties;
    }
}
