package org.opentosca.toscana.plugins.cloudfoundry.filecreator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.plugins.cloudfoundry.application.Application;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 This class provides methods to organize and check the application.
 Maybe there exists "unreal" applications which are just dummy application e.g. services.
 The data from these dummy applications must be copied to the application which they belong to.
 */
class ApplicationHandler {

    private final static Logger logger = LoggerFactory.getLogger(ApplicationHandler.class);
    private List<Application> uncheckedApplications;

    ApplicationHandler(List<Application> applications) {
        this.uncheckedApplications = applications;
    }

    /**
     check applications if they are "real" applications or just dummies like services
     copies data from the dummy application to the application which it belongs to.

     @return a list with only real applications with all needed data
     */
    List<Application> handleApplications() {
        List<Application> checkedApplications = new ArrayList<>();
        List<Application> realApplications = uncheckedApplications.stream()
            .filter(Application::isRealApplication)
            .collect(Collectors.toList());

        //set new application number because the dummy applications are missing
        for (int i = 0; i < realApplications.size(); i++) {
            realApplications.get(i).setApplicationNumber(i + 1);
        }

        //sort checked applications by application number
        realApplications.sort(Comparator.comparing(Application::getApplicationNumber));

        for (Application application : uncheckedApplications) {
            if (!application.isRealApplication()) {
                Set<Application> parentApplications = application.getParentApplications();
                if (CollectionUtils.isNotEmpty(parentApplications)) {
                    parentApplications.forEach(parentApplication -> copyData(application, parentApplication));
                } else {
                    logger.error("There is a unreal application like a service, but no parent application");
                }
            } else {
                checkedApplications.add(application);
                logger.debug("Checked application {}", application.getName());
            }
        }
       
        return checkedApplications;
    }

    /**
     copy all datas from the dummy application to the real parent application
     */
    private void copyData(Application unrealApplication, Application parentApplication) {
        logger.debug("Copy data from unreal application to real parent application");
        //copy file paths
        unrealApplication.getFilePaths().forEach(parentApplication::addFilePath);

        //copy attributes
        unrealApplication.getAttributes().forEach(parentApplication::addAttribute);

        //copy executing files
        unrealApplication.getExecuteCommands().forEach(parentApplication::addExecuteFile);

        //copy mysql configure file
        unrealApplication.getConfigMysql().forEach(parentApplication::addConfigMysql);

        //copy environment variables
        unrealApplication.getEnvironmentVariables().forEach(parentApplication::addEnvironmentVariables);
    }
}
