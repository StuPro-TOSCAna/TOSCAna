package org.opentosca.toscana.plugins.cloudfoundry.application.deployment;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Provider;
import org.opentosca.toscana.plugins.cloudfoundry.application.Service;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.scripts.BashScript;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import org.cloudfoundry.operations.services.ServiceOffering;
import org.cloudfoundry.operations.services.ServicePlan;
import org.slf4j.Logger;

import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.CLI_CREATE_SERVICE;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.CLI_CREATE_SERVICE_DEFAULT;

/**
 this class provides methods to handle the services
 */
public class ServiceHandler {

    private Logger logger;

    private Application application;
    private BashScript deploymentScript;

    public ServiceHandler(Application application, BashScript deploymentScript, TransformationContext context) {
        this.application = application;
        this.deploymentScript = deploymentScript;
        this.logger = context.getLogger(getClass());
    }

    /**
     Add the needed services to the deploymentscript and try to match a suitable service from the provider.

     @param showAllServiceOfferings if yes then adds all service offerings to deploymentscript
     */
    public void addServiceCommands(Boolean showAllServiceOfferings) {
        try {
            logger.info("Try to read service offerings of provider");
            readProviderServices();

            if (showAllServiceOfferings) {
                if (application.getProvider() != null && !application.getServices().isEmpty()
                    && application.getConnection() != null) {
                    logger.debug("List all possible services in the deploy script");
                    addProviderServiceOfferings();
                }
            }
        } catch (IOException e) {
            throw new TransformationFailureException("Fail to add services to deployment Script", e);
        }
    }

    /**
     reads the offered services from the provider
     tries to find a suitable service for the needed purpose.
     always add a service with a free plan
     */
    private void readProviderServices() throws IOException {
        if (application.getProvider() != null && !application.getServices().isEmpty() && application.getConnection() != null) {
            Provider provider = application.getProvider();
            logger.debug("Read service offerings from provider");
            provider.setOfferedService(application.getConnection().getServices());

            for (Map.Entry<String, ServiceTypes> service : application.getServices().entrySet()) {
                String description = service.getValue().getName();
                List<ServiceOffering> services = provider.getOfferedService();
                boolean isSet;

                //checks if a offered service of the provider contains the description of the needed service
                //if yes then add the service to the script with a free plan
                logger.info("Try to find a suitable service from provider which matches to the requested service");
                isSet = addMatchedServices(services, deploymentScript, description, service);

                //if not then add the default create command to the deploy script
                if (!isSet) {
                    logger.error("Could not find a suitable service, add the default value {}. Please adapt the line in the deploy script!", CLI_CREATE_SERVICE_DEFAULT);
                    deploymentScript.append(CLI_CREATE_SERVICE_DEFAULT + service);
                }
            }
        } else {
            for (Map.Entry<String, ServiceTypes> service : application.getServices().entrySet()) {
                logger.error("Could not find a suitable service, add the default value {}. Please adapt the line in the deploy script!", CLI_CREATE_SERVICE_DEFAULT);
                deploymentScript.append(CLI_CREATE_SERVICE_DEFAULT + service.getKey());
            }
        }
    }

    /**
     checks if a service of a provider matches the needed service
     */
    private boolean addMatchedServices(List<ServiceOffering> services,
                                       BashScript deployScript,
                                       String description,
                                       Map.Entry<String, ServiceTypes> service) throws IOException {
        boolean isSet = false;

        for (ServiceOffering offeredService : services) {
            if (offeredService.getDescription().toLowerCase().indexOf(description.toLowerCase()) != -1) {
                for (ServicePlan plan : offeredService.getServicePlans()) {
                    if (plan.getFree()) {
                        String serviceName = offeredService.getLabel();
                        String planName = plan.getName();
                        String serviceInstanceName = service.getKey();
                        logger.info("A suitable service could be found, named {}. Add a free plan named {}, you could adpat the plan in the deploy script", serviceName, planName);
                        deployScript.append(String.format("%s%s %s %s", CLI_CREATE_SERVICE,
                            serviceName, planName, serviceInstanceName));
                        application.addMatchedService(
                            new Service(serviceName, serviceInstanceName, planName, service.getValue()));
                        isSet = true;
                        break;
                    }
                }
            }
        }
        return isSet;
    }

    /**
     adds all offered services from the provider to the deploy script
     */
    private void addProviderServiceOfferings() throws IOException {
        Provider provider = application.getProvider();
        List<ServiceOffering> services = provider.getOfferedService();

        deploymentScript.append("# following services you could choose:");
        deploymentScript.append(String.format("# %-20s %-40s %-50s\n", "Name", " Plans", "Description"));

        for (ServiceOffering service : services) {
            String plans = "";
            for (ServicePlan plan : service.getServicePlans()) {
                String currentPlan;
                if (plan.getFree()) {
                    currentPlan = plan.getName();
                } else {
                    currentPlan = plan.getName() + "*";
                }

                plans = String.format("%s %s ", plans, currentPlan);
            }
            deploymentScript.append(String.format("# %-20s %-40s %-50s ", service.getLabel(), plans, service.getDescription()));
        }
        deploymentScript.append("\n# * These service plans have an associated cost. Creating a service instance will incur this cost.\n");
    }
}
