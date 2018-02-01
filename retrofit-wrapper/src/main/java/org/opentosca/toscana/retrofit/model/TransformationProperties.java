package org.opentosca.toscana.retrofit.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformationProperties extends AbstractTransformationProperties<TransformationProperty> {
    public TransformationProperties(
        @JsonProperty("properties") List<TransformationProperty> properties
    ) {
        super(properties);
    }
}
