package org.opentosca.toscana.api.docs;

import java.util.List;

import org.opentosca.toscana.api.model.CsarResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class CsarResources extends HiddenResourceSupport {

    @ApiModelProperty(required = true)
    @JsonProperty("_embedded")
    private CsarContainer embedded;

    @ApiModel
    private static class CsarContainer {

        @ApiModelProperty(
            required = true,
            notes = "The list of csars stored on this system"
        )
        @JsonProperty("csar")
        public List<CsarResponse> content;
    }
}
