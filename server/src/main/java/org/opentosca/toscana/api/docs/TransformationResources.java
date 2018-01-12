package org.opentosca.toscana.api.docs;

import java.util.List;

import org.opentosca.toscana.api.model.TransformationResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class TransformationResources extends HiddenResourceSupport {

    @ApiModelProperty(required = true)
    @JsonProperty("_embedded")
    private TransformationsContainer embedded;

    @ApiModel
    private static class TransformationsContainer {

        @ApiModelProperty(
            required = true,
            notes = "The list of transformations for a csar"
        )
        @JsonProperty("transformation")
        public List<TransformationResponse> content;
    }
}
