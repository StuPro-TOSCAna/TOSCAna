package org.opentosca.toscana.plugins.cloudfoundry.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;

/**
 This class should describe a Application with all needed information to deploy it
 */
public class Application {

    private String name;
    private final ArrayList<String> filePaths = new ArrayList<>();
    private final Map<String, String> environmentVariables = new HashMap<>();
    private final Map<String, String> attributes = new HashMap<>();
    private final Map<String, ServiceTypes> services = new HashMap<>();
    private final ArrayList<Service> servicesMatchedToProvider = new ArrayList<>();
    private final ArrayList<String> invalidApplicationSuffixes = new ArrayList<>(Arrays.asList("sh", "sql"));
    private Provider provider;
    private String pathToApplication;
    private String applicationSuffix;

    private Connection connection;

    public Application(String name) {
        this.name = name;
    }

    public Application() {
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
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

    public Map<String, ServiceTypes> getServices() {
        return services;
    }

    public List<Service> getServicesMatchedToProvider() {
        return Collections.unmodifiableList(servicesMatchedToProvider);
    }

    public void addMatchedService(Service matchedService) {
        servicesMatchedToProvider.add(matchedService);
    }

    public void addService(String serviceName, ServiceTypes serviceType) {
        this.services.put(serviceName, serviceType);
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

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getApplicationSuffix() {
        return applicationSuffix;
    }

    public void setPathToApplication(String pathToApplication) {
        int lastOccurenceOfBackslash = pathToApplication.lastIndexOf("/");
        int lastOccurenceOfDot = pathToApplication.lastIndexOf(".");

        if (lastOccurenceOfDot != -1) {
            String suffix = pathToApplication.substring(lastOccurenceOfDot + 1, pathToApplication.length());
            if (isValidApplicationSuffix(suffix)) {
                this.applicationSuffix = pathToApplication.substring(lastOccurenceOfDot + 1, pathToApplication.length());

                if (lastOccurenceOfBackslash != -1) {
                    this.pathToApplication = pathToApplication.substring(0, lastOccurenceOfBackslash);
                }
            }
        }
    }

    private boolean isValidApplicationSuffix(String suffix) {
        return !invalidApplicationSuffixes.contains(suffix);
    }

    public String getPathToApplication() {
        return pathToApplication;
    }
}
