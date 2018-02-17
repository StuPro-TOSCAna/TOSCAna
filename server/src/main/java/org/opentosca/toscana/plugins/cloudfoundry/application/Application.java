package org.opentosca.toscana.plugins.cloudfoundry.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;

import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.APPLICATION_FOLDER;

/**
 This class should describe a Application with all needed information to deploy it
 */
public class Application {

    private Logger logger;

    private String name;
    private int applicationNumber;
    private final ArrayList<String> configureSqlDatabase = new ArrayList<>();
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

     @param pathToFile must be the path inside the csar. The method will create a relative path from it
     */
    public void addConfigMysql(String pathToFile) {
        String relativePath = "../../" + APPLICATION_FOLDER + this.applicationNumber + "/" + pathToFile;

        logger.debug("Add a config mysql command to deploy script. Relative path to file is {}", relativePath);
        configureSqlDatabase.add(relativePath);
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


        logger.debug("Add python script to execute {} on cloud foundry warden container", pathToFile);

        executeCommand.put("../../" + APPLICATION_FOLDER + this.getApplicationNumber() + "/" + pathToFile,
            pathToFileOnContainer + pathToFile);
    }

    /**


        oldExecuteCommand.forEach((pathToFile, pathOnContainer) -> this.addExecuteFile(pathToFile, pathOnContainer));

     returns a list with realtive paths which should be executed with the python script configMysql
     */
    public List<String> getConfigMysql() {
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

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getName() {
        return name;
    /**
     set the application name.
     all forbidden signs will be replaced by -
     */
    public void setName(String name) {
        String clearedUpName = name.replaceAll("[:/?#@$&'()*+,;=_]", "-");
        logger.debug("Replace all occurence of forbidden signs in the application name with \"-\"");
        this.name = clearedUpName;
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
                    this.pathToApplication = pathToApplication.substring(0, lastOccurenceOfBackslash);
                }
            }
        }
    }

    private boolean isValidApplicationSuffix(String suffix) {
        return !invalidApplicationSuffixes.contains(suffix);
     true if the application is an application
     false if the application is a service
    }

    public String getPathToApplication() {
        return pathToApplication;
    }
}
