package org.opentosca.toscana.retrofit.model.hal;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class HALEmbeddedResource<T extends HALResource>
    extends HALResource {

    private Map<String, List<T>> embeddedResources;

    public abstract String getResourcesName();

    @JsonProperty("_embedded")
    public Map<String, List<T>> getEmbeddedResources() {
        return embeddedResources;
    }

    @JsonProperty("_embedded")
    public void setEmbeddedResources(Map<String, List<T>> embeddedResources) {
        this.embeddedResources = embeddedResources;
    }

    @JsonIgnore
    public List<T> getContent() {
        if (embeddedResources != null) {
            return embeddedResources.get(getResourcesName());
        }
        return null;
    }
}
