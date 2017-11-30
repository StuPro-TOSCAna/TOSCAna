package org.opentosca.toscana.plugins.kubernetes.docker.mapper.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 This class represents a whole repository and its corresponding images (tags)
 <p>
 This class is only used to fetch the data from the registry.
 <p>
 Only the needed fields get stored in a attribute. the rest is put into the AdditionalProperties map
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageTags {

    @JsonProperty("next")
    private Object next;
    @JsonProperty("results")
    private List<ImageTag> imageTags = null;

    /**
     This field and its corresponding getter/setter is needed to parse the data, it contains all the fields that 
     have not been asigned to any field explicitly, because it is not needed.
     */
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonProperty("next")
    public Object getNext() {
        return next;
    }

    @JsonProperty("results")
    public List<ImageTag> getImageTags() {
        return imageTags;
    }
}
