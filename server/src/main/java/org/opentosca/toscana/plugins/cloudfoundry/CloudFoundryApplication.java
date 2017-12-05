package org.opentosca.toscana.plugins.cloudfoundry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 This class should describe a CloudFoundryApplication with all needed information to deploy it
 */
public class CloudFoundryApplication {

    private String name;
    private final ArrayList<String> filePaths = new ArrayList<>();
    private final Map<String, String> environmentVariables = new HashMap<>();
    private final Map<String, String> attributes = new HashMap<>();
    private final Set<String> services = new HashSet<>();
    private final ArrayList<String> buildpackAdditions = new ArrayList<>();
    private CloudFoundryProvider provider;

    public CloudFoundryApplication(String name) {
        this.name = name;
    }

    public CloudFoundryApplication() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void addEnvironmentVariables(String environmentVariableName, String value) {
        this.environmentVariables.put(environmentVariableName, value);
    }

    public void addEnvironmentVariables(String environmentVariableName) {
        this.environmentVariables.put(environmentVariableName, "");
    }

    public Set<String> getServices() {
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

    public void addAttribute(String attributeName, String attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    public void addFilePath(String filePath) {
        filePaths.add(filePath);
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public CloudFoundryProvider getProvider() {
        return provider;
    }

    public void setProvider(CloudFoundryProvider provider) {
        this.provider = provider;
    }
    
}
