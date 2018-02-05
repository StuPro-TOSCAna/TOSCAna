package org.opentosca.toscana.api.model;

import java.util.List;

import org.opentosca.toscana.api.CsarController;
import org.opentosca.toscana.api.TransformationController;
import org.opentosca.toscana.api.docs.HiddenResourceSupport;
import org.opentosca.toscana.core.plugin.lifecycle.LifecyclePhase;

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
    private final List<? extends LifecyclePhase> phases;

    public CsarResponse(
        @JsonProperty("name") String name,
        @JsonProperty("phases") List<? extends LifecyclePhase> phases
    ) {
        this.name = name;
        this.phases = phases;
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

    @ApiModelProperty(
        required = true,
        notes = "The lifecycle phases of this CSAR"
    )
    @JsonProperty("phases")
    public List<? extends LifecyclePhase> getPhases() {
        return phases;
    }
}
