package org.opentosca.toscana.api.model;

import org.opentosca.toscana.api.docs.HiddenResourceSupport;
import org.opentosca.toscana.core.transformation.platform.Platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.core.Relation;

/**
 Represents the platform response (<code>/platforms/{id}</code>) as a Java Object that gets converted to json using
 Jackson
 */
@ApiModel
@Relation(collectionRelation = "platform")
public class PlatformResponse extends HiddenResourceSupport {
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

    @ApiModelProperty(
        required = true,
        notes = "The identifier for this platform (plugin)",
        example = "kubernetes"
    )
    @JsonProperty("id")
    public String getIdentifier() {
        return identifier;
    }

    @ApiModelProperty(
        required = true,
        notes = "The name for this platform (plugin)",
        example = "Kubernetes"
    )
    @JsonProperty("name")
    public String getName() {
        return name;
    }
}
