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
    private final ArrayList<String> services = new ArrayList<>();
    private final ArrayList<String> bpAdditions = new ArrayList<>();

    public CloudFoundryApplication(String appName) {
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
        return bpAdditions;
    }

    public void addBuildpack(String buildPack) {
        this.bpAdditions.add(buildPack);
    }
}
