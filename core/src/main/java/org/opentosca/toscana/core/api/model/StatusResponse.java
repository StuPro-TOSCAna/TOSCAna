package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Represents the status response as a Java Object that gets converted to json using Jackson
 */
public class StatusResponse extends ResourceSupport {
    private String status;
    private Long availableStorage;
    private Long totalStorage;

    @JsonCreator
    public StatusResponse(
        @JsonProperty("status") String status,
        @JsonProperty("available_storage") Long availableStorage,
        @JsonProperty("total_storage") Long totalStorage
    ) {
        this.status = status;
        this.availableStorage = availableStorage;
        this.totalStorage = totalStorage;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("available_storage")
    public Long getAvailableStorage() {
        return availableStorage;
    }

    @JsonProperty("total_storage")
    public Long getTotalStorage() {
        return totalStorage;
    }
}
