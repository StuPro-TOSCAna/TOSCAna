package org.opentosca.toscana.retrofit.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationTransformationProperties
    extends AbstractTransformationProperties<ValidationTransformationProperty> {
    
    public ValidationTransformationProperties(
        @JsonProperty("properties") List<ValidationTransformationProperty> properties
    ) {
        super(properties);
    }
}
