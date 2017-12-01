package org.opentosca.toscana.plugins.cloudformation.builder;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"AWSTemplateFormatVersion", "Description", "Parameters", "Resources", "Outputs"})
public class RootTemplate {
    
    @JsonProperty("AWSTemplateFormatVersion")
    private String version = "2010-09-09";
    
    @JsonProperty("Description")
    private String description = "";
    
    @JsonProperty()
    private Map<String, Parameters> parameters = new HashMap<>();
    
    @JsonProperty
    private Map<String, Resource> resources = new HashMap<>();
    
    @JsonProperty
    private Map<String, Outputs> outputs = new HashMap<>();
    
    
    
}
