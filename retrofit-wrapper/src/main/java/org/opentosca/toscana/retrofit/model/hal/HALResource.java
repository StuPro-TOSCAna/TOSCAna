package org.opentosca.toscana.retrofit.model.hal;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HALResource extends Resource {

    protected Map<String, ResourceLink> links;

    @JsonProperty("_links")
    public Map<String, ResourceLink> getLinks() {
        return links;
    }

    @JsonProperty("_links")
    public void setLinks(Map<String, ResourceLink> links) {
        this.links = links;
    }

    public static class ResourceLink extends Resource {
        private String reference;
        private Boolean templated;

        public ResourceLink(
            @JsonProperty("href") String reference
        ) {
            this.reference = reference;
        }

        @JsonProperty("templated")
        public Boolean getTemplated() {
            return templated;
        }

        @JsonProperty("templated")
        public void setTemplated(Boolean templated) {
            this.templated = templated;
        }

        @JsonProperty("href")
        public String getReference() {
            return reference;
        }
    }
}
