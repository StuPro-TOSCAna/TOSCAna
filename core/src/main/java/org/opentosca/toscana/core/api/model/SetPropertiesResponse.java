package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SetPropertiesResponse {
	private Map<String, Boolean> validInputs;

	public SetPropertiesResponse(
		@JsonProperty("valid_inputs") Map<String, Boolean> validInputs
	) {
		this.validInputs = validInputs;
	}

	@JsonProperty("valid_inputs")
	public Map<String, Boolean> getValidInputs() {
		return validInputs;
	}
}
