package org.opentosca.toscana.core.api.model;

import org.opentosca.toscana.core.transformation.platform.Platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

/**
 Represents the platform response (<code>/platforms/{id}</code>) as a Java Object that gets converted to json using
 Jackson
 */
@Relation(collectionRelation = "platform")
public class PlatformResponse extends ResourceSupport {
    private final String identifier;
    private final String name;

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

    @JsonProperty("name")
    public String getName() {
        return name;
    }
}
