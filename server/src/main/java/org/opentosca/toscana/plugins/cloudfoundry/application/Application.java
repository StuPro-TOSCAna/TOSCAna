package org.opentosca.toscana.plugins.cloudfoundry.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;

import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.APPLICATION_FOLDER;

/**
 This class should describe a Application with all needed information to deploy it
 */
public class Application {

    private final Map<String, String> configureSqlDatabase = new HashMap<>();
    private final Map<String, String> executeCommand = new HashMap<>();
    private final ArrayList<String> filePaths = new ArrayList<>();
    private final Map<String, String> environmentVariables = new HashMap<>();
    private final Map<String, String> attributes = new HashMap<>();
    private final Map<String, ServiceTypes> services = new HashMap<>();
    private final ArrayList<Service> servicesMatchedToProvider = new ArrayList<>();
    private final ArrayList<String> invalidApplicationSuffixes = new ArrayList<>(Arrays.asList("sh", "sql"));
    private Logger logger;
    private String name;
    private int applicationNumber;
    private Provider provider;
    private String pathToApplication;
    private String applicationSuffix;
    private NodeStack stack;
    private boolean realApplication = true;
    private Set<Application> parentApplications = null;
    private boolean enablePathToApplication = false;

    private Connection connection;

    public Application(String name, int applicationNumber, TransformationContext context) {
        this.name = name;
        this.applicationNumber = applicationNumber;
        this.logger = context.getLogger(getClass());
    }

    public Application(int applicationNumber, TransformationContext context) {
        this.applicationNumber = applicationNumber;
        this.logger = context.getLogger(getClass());
    }

    /**
     add in the deploy script the command to execute a sql file to the connected mysql database
     must be a .sql File
     If there is a script file to configure the database you have to use the executeFile method

     @param serviceInstanceName is the name of the service
     @param pathToFile          must be the path inside the csar. The method will create a relative path from it
     */
    public void addConfigMysql(String serviceInstanceName, String pathToFile) {
        String path = pathToFile;
        if (pathToFile.contains("../../" + APPLICATION_FOLDER)) {
            String[] paths = pathToFile.split("../../" + APPLICATION_FOLDER + "[0-9]*/");
            path = paths[1];
        }
        String relativePath = "../../" + APPLICATION_FOLDER + this.applicationNumber + "/" + path;

        logger.debug("Add a config mysql command to deploy script. Relative path to file is {}", relativePath);
        configureSqlDatabase.put(serviceInstanceName, relativePath);
    }

    /**
     execute the given file on the warden container

     @param pathToFile    must be the path inside the csar. The method will create a path on the warden container
     @param parentTopNode Class of a RootNode which should be the top node of this stack. This is needed to get the
     right direction on the container
     */
    public void addExecuteFile(String pathToFile, RootNode parentTopNode) {
        String pathToFileOnContainer = "/home/vcap/app/";
        //TODO: expand with more NodeType
        if (parentTopNode instanceof WebApplication) {
            pathToFileOnContainer = "/home/vcap/app/htdocs/";
        }

        if (pathToFile.contains("../../" + APPLICATION_FOLDER)) {
            String[] paths = pathToFile.split("../../" + APPLICATION_FOLDER + "[0-9]*/");
            pathToFile = paths[1];
        }

        logger.debug("Add python script to execute {} on cloud foundry warden container", pathToFile);

        executeCommand.put("../../" + APPLICATION_FOLDER + this.getApplicationNumber() + "/" + pathToFile,
            pathToFileOnContainer + pathToFile);
    }

    /**
     execute the given file on the warden container

     @param pathToFile      must be the path inside the csar. The method will create a path on the warden container
     @param pathOnContainer must be the path inside the container
     */
    public void addExecuteFile(String pathToFile, String pathOnContainer) {
        String pathToFileNew = pathToFile;

        if (pathToFile.contains("../../" + APPLICATION_FOLDER)) {
            String[] paths = pathToFileNew.split("../../" + APPLICATION_FOLDER + "[0-9]*/");
            pathToFileNew = paths[1];
        }
        pathToFileNew = "../../" + APPLICATION_FOLDER + this.getApplicationNumber() + "/" + pathToFileNew;

        executeCommand.put(pathToFileNew, pathOnContainer);
    }

    /**
     to update the paths if the applicationnumber changes
     */
    private void updateExecuteFiles() {
        Map<String, String> oldExecuteCommand = new HashMap<>(executeCommand);
        executeCommand.clear();
        oldExecuteCommand.forEach((pathToFile, pathOnContainer) -> this.addExecuteFile(pathToFile, pathOnContainer));
    }

    /**
     returns a map with realtive paths which should be executed with the python script configMysql
     */
    public Map<String, String> getConfigMysql() {
        return configureSqlDatabase;
    }

    /**
     returns a list with paths which should be executed with the python script executeCommand

     @return Key is the path to File and value is path to file on container
     */
    public Map<String, String> getExecuteCommands() {
        return executeCommand;
    }

    public int getApplicationNumber() {
        return this.applicationNumber;
    }

    public void setApplicationNumber(int applicationNumber) {
        this.applicationNumber = applicationNumber;
        this.updateExecuteFiles();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    /**
     set the application name.
     all forbidden signs will be replaced by -
     */
    public void setName(String name) {
        String clearedUpName = name.replaceAll("[:/?#@$&'()*+,;=_]", "-");
        logger.debug("Replace all occurence of forbidden signs in the application name with \"-\"");
        this.name = clearedUpName;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void addEnvironmentVariables(String environmentVariableName, String value) {
        if (value.isEmpty()) {
            this.addEnvironmentVariables(environmentVariableName);
            logger.debug("Add environment variable {} to manifest", environmentVariableName);
        } else {
            this.environmentVariables.put(environmentVariableName, value);
            logger.debug("Add environment variable {} with value {} to manifest", environmentVariableName, value);
        }
    }

    public void addStack(NodeStack stack) {
        this.stack = stack;
    }

    public NodeStack getStack() {
        return stack;
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

    public void addAttribute(ManifestAttributes attributeName, String attributeValue) {
        addAttribute(attributeName.getName(), attributeValue);
    }

    public void addAttribute(String attributeName, String attributeValue) {
        attributes.put(attributeName, attributeValue);
        logger.debug("Add attribute variable {} with value {} to manifest", attributeName, attributeValue);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     adds a file to the output folder

     @param filePath path in the csar
     */
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

    private boolean isValidApplicationSuffix(String suffix) {
        return !invalidApplicationSuffixes.contains(suffix);
    }

    public String getPathToApplication() {
        return pathToApplication;
    }

    /**
     sets the path to the main application which should be executed
     */
    public void setPathToApplication(String pathToApplication) {

        int lastOccurenceOfBackslash = pathToApplication.lastIndexOf("/");
        int lastOccurenceOfDot = pathToApplication.lastIndexOf(".");

        if (lastOccurenceOfDot != -1) {
            String suffix = pathToApplication.substring(lastOccurenceOfDot + 1, pathToApplication.length());
            if (isValidApplicationSuffix(suffix)) {
                this.applicationSuffix = pathToApplication.substring(lastOccurenceOfDot + 1, pathToApplication.length());

                if (lastOccurenceOfBackslash != -1) {
                    this.pathToApplication = pathToApplication;
                }
            }
        }
    }

    /**
     true if the application is a real application
     false if the application is a dummy application e.g. a service
     default is true
     */
    public boolean isRealApplication() {
        return realApplication;
    }

    /**
     if the application is a dummy application e.g. a service
     default is true

     @param parentApplications a set of applications to which this application belongs to
     */
    public void applicationIsNotReal(Set<Application> parentApplications) {
        this.realApplication = false;
        this.parentApplications = parentApplications;
    }

    /**
     @return the application to which this dummy application belongs to. Null if there is no parent.
     */
    public Set<Application> getParentApplications() {
        return parentApplications;
    }

    /**
     enable the path to the application file
     should use if only one file is for the application like .jar

     @param enablePathToApplication default is false. True means that there is no special path to application
     */
    public void setEnablePathToApplication(boolean enablePathToApplication) {
        this.enablePathToApplication = enablePathToApplication;
    }

    /**
     if true the application has one file which should be added to the path attribute
     */
    public boolean isEnablePathToApplication() {
        return enablePathToApplication;
    }
}
