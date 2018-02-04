package org.opentosca.toscana.retrofit.model.hal;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Resource {

    protected Map<String, Object> otherValues = new HashMap<>();

    @JsonAnyGetter
    public Object get(String key) {
        return otherValues.get(key);
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        this.otherValues.put(key, value);
    }
}
