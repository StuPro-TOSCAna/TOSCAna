package org.opentosca.toscana.api.docs;

import java.util.List;

import org.opentosca.toscana.api.model.PlatformResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PlatformResources extends HiddenResourceSupport {

    @ApiModelProperty(required = true)
    @JsonProperty("_embedded")
    private PlatformsContainer embedded;

    @ApiModel
    private static class PlatformsContainer {

        @ApiModelProperty(
            required = true,
            notes = "The list of platforms supported by this system"
        )
        @JsonProperty("platform")
        public List<PlatformResponse> content;
    }
}
