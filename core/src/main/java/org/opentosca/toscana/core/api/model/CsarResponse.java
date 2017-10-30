package org.opentosca.toscana.core.api.model;

import org.opentosca.toscana.core.api.CsarController;
import org.opentosca.toscana.core.api.TransformationController;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Relation(collectionRelation = "csar")
public class CsarResponse extends ResourceSupport {
    private final String name;

    public CsarResponse(
        @JsonProperty("name") String name
    ) {
        this.name = name;
        add(linkTo(methodOn(CsarController.class)
            .getCSARInfo(name)).withSelfRel());
        add(linkTo(methodOn(TransformationController.class)
            .getCSARTransformations(name)).withRel("transformations").expand(name));
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
}
