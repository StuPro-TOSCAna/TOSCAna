package org.opentosca.toscana.core.api.model;

import org.opentosca.toscana.core.api.CsarController;
import org.opentosca.toscana.core.api.TransformationController;
import org.opentosca.toscana.core.api.docs.HiddenResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.core.Relation;

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
        add(linkTo(methodOn(CsarController.class)
            .getCSARInfo(name)).withSelfRel());
        add(linkTo(methodOn(CsarController.class).deleteCsar(name)).withRel("delete"));
        add(linkTo(methodOn(TransformationController.class)
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
