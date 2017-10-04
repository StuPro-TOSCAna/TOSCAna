package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opentosca.toscana.core.api.CsarController;
import org.opentosca.toscana.core.api.CsarTransformationController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Relation(collectionRelation = "csar")
public class CsarResponse extends ResourceSupport {
	private String name;

	public CsarResponse(
		@JsonProperty("name") String name
	) {
		this.name = name;
		add(linkTo(methodOn(CsarController.class)
			.getCSARInfo(name)).withSelfRel());
		add(linkTo(methodOn(CsarController.class)
			.uploadCSAR(name, null)).withRel("archive"));
		add(linkTo(methodOn(CsarTransformationController.class)
			.getCSARTransformations(name)).withRel("transformations"));
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
