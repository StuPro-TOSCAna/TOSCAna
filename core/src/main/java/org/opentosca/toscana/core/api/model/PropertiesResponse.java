package org.opentosca.toscana.core.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class PropertiesResponse extends ResourceSupport {

	public static class PropertyWrap {
		private String key;
		private String type;

		public PropertyWrap(
			@JsonProperty("key") String key,
			@JsonProperty("type") String type
		) {
			this.key = key;
			this.type = type;
		}

		@JsonProperty("key")
		public String getKey() {
			return key;
		}

		@JsonProperty("type")
		public String getType() {
			return type;
		}
	}
}
