package org.opentosca.toscana.plugins.cloudfoundry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 This class should describe a CloudFoundryApplication with all needed information to deploy it
 */
public class CloudFoundryApplication {
    
    private String appName;
    private final Map<String, String> environmentVariables = new HashMap<>();
    private final Map<String, String> attributes = new HashMap<>();
    private final ArrayList<String> services = new ArrayList<>();
    private final ArrayList<String> buildpackAdditions = new ArrayList<>();

    public CloudFoundryApplication(String appName) {
        this.appName = appName;
    }

    public CloudFoundryApplication() {
        
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    public String getAppName() {
        return appName;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void addEnvironmentVariables(String environmentVariableName, String value) {
        this.environmentVariables.put(environmentVariableName, value);
    }

    public List<String> getServices() {
        return services;
    }

    public void addService(String serviceName) {
        this.services.add(serviceName);
    }

    public List<String> getBuildpackAdditions() {
        return buildpackAdditions;
    }

    public void addBuildpack(String buildPack) {
        this.buildpackAdditions.add(buildPack);
    }

    public void addAttribute(String attributeName, String attributeValue){
        attributes.put(attributeName, attributeValue);
    }

    public Map<String, String> getAttributes(){
        return attributes;
    }

}
