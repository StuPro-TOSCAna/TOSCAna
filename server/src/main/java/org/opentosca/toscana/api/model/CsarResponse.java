package org.opentosca.toscana.api.model;

import org.opentosca.toscana.api.CsarController;
import org.opentosca.toscana.api.TransformationController;
import org.opentosca.toscana.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Relation(collectionRelation = "csar")
@ApiModel
public class CsarResponse extends HiddenResourceSupport {
    private final String name;

    public CsarResponse(
        @JsonProperty("name") String name
    ) {
        this.name = name;
        add(ControllerLinkBuilder.linkTo(methodOn(CsarController.class)
            .getCSARInfo(name)).withSelfRel());
        add(linkTo(methodOn(CsarController.class).deleteCsar(name)).withRel("delete"));
        add(ControllerLinkBuilder.linkTo(methodOn(TransformationController.class)
            .getCSARTransformations(name)).withRel("transformations").expand(name));
    }

    @ApiModelProperty(
        required = true,
        example = "test",
        notes = "This value represents the name (Identifier) of this CSAR"
    )
    @JsonProperty("name")
    public String getName() {
        return name;
    }
}
