package org.opentosca.toscana.plugins.cloudfoundry.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.plugins.cloudfoundry.client.CloudFoundryConnection;

/**
 This class should describe a CloudFoundryApplication with all needed information to deploy it
 */
public class CloudFoundryApplication {

    private String name;
    private final ArrayList<String> filePaths = new ArrayList<>();
    private final Map<String, String> environmentVariables = new HashMap<>();
    private final Map<String, String> attributes = new HashMap<>();
    private final Map<String, CloudFoundryServiceType> services = new HashMap<>();
    private final ArrayList<CloudFoundryService> servicesMatchedToProvider = new ArrayList<>();
    private final ArrayList<String> buildpackAdditions = new ArrayList<>();
    private CloudFoundryProvider provider;
    private String pathToApplication;
    private CloudFoundryConnection connection;

    public CloudFoundryApplication(String name) {
        this.name = name;
    }

    public CloudFoundryApplication() {
    }

    public void setConnection(CloudFoundryConnection connection) {
        this.connection = connection;
    }

    public CloudFoundryConnection getConnection() {
        return connection;
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
        if (value.isEmpty()) {
            this.addEnvironmentVariables(environmentVariableName);
        } else {
            this.environmentVariables.put(environmentVariableName, value);
        }
    }

    public void addEnvironmentVariables(String environmentVariableName) {
        this.environmentVariables.put(environmentVariableName, "TODO");
    }

    public Map<String, CloudFoundryServiceType> getServices() {
        return services;
    }

    public ArrayList<CloudFoundryService> getServicesMatchedToProvider() {
        return servicesMatchedToProvider;
    }

    public void addMatchedService(CloudFoundryService matchedService) {
        servicesMatchedToProvider.add(matchedService);
    }

    public void addService(String serviceName, CloudFoundryServiceType serviceType) {
        this.services.put(serviceName, serviceType);
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

    public void setPathToApplication(String pathToApplication) {
        int lastOccurenceOfBackslash = pathToApplication.lastIndexOf("/");

        if (lastOccurenceOfBackslash != -1) {
            this.pathToApplication = pathToApplication.substring(0, lastOccurenceOfBackslash);
        }
    }

    public String getPathToApplication() {
        return pathToApplication;
    }
}
