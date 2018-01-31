package org.opentosca.toscana.api.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opentosca.toscana.api.TransformationController;
import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel
public class SetPropertiesResponse extends HiddenResourceSupport {
    private final List<ValidationPropertyWrap> properties;

    public SetPropertiesResponse(
        String csarName,
        String platformName,
        List<PropertyWrap> properties,
        Map<String, Boolean> validInputs
    ) {
        this.properties = properties.stream()
            .map(e -> new ValidationPropertyWrap(
                    e,
                    validInputs.get(e.key)
                )
            ).collect(Collectors.toList());
        add(ControllerLinkBuilder.linkTo(methodOn(TransformationController.class)
            .getTransformationProperties(null, null))
            .withSelfRel().expand(csarName, platformName)
        );
    }

    @ApiModelProperty(
        required = true,
        dataType = "boolean",
        notes = "This value is true if the internal validation of the property was succesful, False otherwise"
    )
    @JsonProperty("properties")
    public List<ValidationPropertyWrap> getProperties() {
        return properties;
    }
}
