package org.opentosca.toscana.plugins.cloudfoundry.filecreator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.plugins.cloudfoundry.application.Application;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 This class provides methods to organize and check the application.
 Maybe there exists "unreal" applications which are just dummy application e.g. services.
 The data from these dummy applications must be copied to the application which they belong to.
 */
public class ApplicationHandler {

    private final static Logger logger = LoggerFactory.getLogger(ApplicationHandler.class);
    private List<Application> uncheckedApplications;

    public ApplicationHandler(List<Application> applications) {
        this.uncheckedApplications = applications;
    }

    /**
     check applications if they are "real" applications or just dummies like services
     copies data from the dummy application to the application which it belongs to.

     @return a list with only real applications with all needed data
     */
    public List<Application> handleApplications() {
        List<Application> checkedApplications = new ArrayList<>();
        List<Integer> appNumberFromUnrealApps = new ArrayList<>();

        for (Application application : uncheckedApplications) {
            if (!application.isRealApplication()) {
                Set<Application> parentApplications = application.getParentApplications();
                if (CollectionUtils.isNotEmpty(parentApplications)) {
                    parentApplications.forEach(parentApplication -> copyData(application, parentApplication));
                    appNumberFromUnrealApps.add(application.getApplicationNumber());
                } else {
                    logger.error("There is a unreal application like a service, but no parent application");
                }
            } else {
                checkedApplications.add(application);
                logger.debug("Checked application {}", application.getName());
            }
        }

        //sort checked applications by application number
        checkedApplications.sort(Comparator.comparing(application -> application.getApplicationNumber()));

        //set new application number because the dummy applications are missing
        for (int i = 0; i < checkedApplications.size(); i++) {
            checkedApplications.get(i).setApplicationNumber(i + 1);
        }

        return checkedApplications;
    }

    /**
     copy all datas from the dummy application to the real parent application
     */
    private void copyData(Application unrealApplication, Application parentApplication) {
        logger.debug("Copy data from unreal application to real parent application");
        //copy file paths
        unrealApplication.getFilePaths().forEach(path -> parentApplication.addFilePath(path));

        //copy attributes
        unrealApplication.getAttributes().forEach((k, v) -> parentApplication.addAttribute(k, v));

        //copy executing files
        unrealApplication.getExecuteCommands().forEach((k, v) -> parentApplication.addExecuteFile(k, v));

        //copy mysql configure file
        unrealApplication.getConfigMysql().forEach(sqlFile -> parentApplication.addConfigMysql(sqlFile));

        //copy environment variables
        unrealApplication.getEnvironmentVariables().forEach((k, v) -> parentApplication.addEnvironmentVariables(k, v));
    }
}
