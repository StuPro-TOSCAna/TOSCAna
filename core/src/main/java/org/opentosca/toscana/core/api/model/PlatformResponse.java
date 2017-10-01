package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opentosca.toscana.core.transformation.Platform;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

@Relation(collectionRelation = "platform")
public class PlatformResponse extends ResourceSupport {
	private String identifier;
	private String name;

	public PlatformResponse(
		@JsonProperty("id") String identifier,
		@JsonProperty("name") String name
	) {
		this.identifier = identifier;
		this.name = name;
	}
	
	public PlatformResponse(Platform p) {
		this(p.id, p.name);
	}

	@JsonProperty("id")
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
