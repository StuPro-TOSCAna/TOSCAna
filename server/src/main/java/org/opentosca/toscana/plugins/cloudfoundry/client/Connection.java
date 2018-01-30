package org.opentosca.toscana.plugins.cloudfoundry.client;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.cloudfoundry.application.Service;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationEnvironments;
import org.cloudfoundry.operations.applications.ApplicationManifest;
import org.cloudfoundry.operations.applications.GetApplicationEnvironmentsRequest;
import org.cloudfoundry.operations.applications.PushApplicationManifestRequest;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.ListServiceOfferingsRequest;
import org.cloudfoundry.operations.services.ServiceOffering;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import static java.lang.Boolean.TRUE;

/**
 implements java-cf-client
 create a connection to the cf provider
 */
public class Connection {

    private Logger logger;

    private String userName;
    private String password;
    private String apiHost;
    private String organization;
    private String space;
    private CloudFoundryOperations cloudFoundryOperations;

    /**
     creates a connection to a cloud foundry instance
     will throw a transformation failure exception when no connection could established

     @param username     username of the cloud foundry instance
     @param password     password of the cloud foundry instance
     @param apiHost      url to the provider
     @param organization organisation of the user instance
     @param space        space of the user instance
     */
    public Connection(String username, String password,
                      String apiHost, String organization,
                      String space, TransformationContext context) {

        this.userName = username;
        this.password = password;
        this.apiHost = apiHost;
        this.organization = organization;
        this.space = space;
        this.logger = context.getLogger(getClass());

        logger.info("Try to connect to CF Instance");
        this.cloudFoundryOperations = createCloudFoundryOperations();
    }

    /**
     create a Connection to a cloud foundry instance.
     A CLoudFoundryOperation could send cf commands
     */
    private CloudFoundryOperations createCloudFoundryOperations() {
        CloudFoundryOperations cloudFoundryOperations;
        try {
            DefaultConnectionContext connectionContext = DefaultConnectionContext.builder()
                .apiHost(apiHost)
                .build();

            TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
                .password(password)
                .username(userName)
                .build();

            ReactorCloudFoundryClient reactorClient = ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider)
                .build();

            cloudFoundryOperations = DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(reactorClient)
                .organization(organization)
                .space(space)
                .build();
        } catch (Exception e) {
            logger.error("Cant connect to Cloud Foundry instance");
            throw new TransformationFailureException("Could not connect to Cloud Foundry instance, Please check your credentials", e);
        }

        logger.info("Connect successfully to Cloud Foundry instance");
        return cloudFoundryOperations;
    }

    /**
     @return a list with all service offerings of the provider
     */
    public List<ServiceOffering> getServices() {
        ListServiceOfferingsRequest serviceOfferingsRequest = ListServiceOfferingsRequest.builder().build();
        List<ServiceOffering> listServiceOfferings = cloudFoundryOperations.services().listServiceOfferings(serviceOfferingsRequest).collectList().block();
        return listServiceOfferings;
    }

    /**
     Method to get the service credentials which depends on the CloudFoundry instance.
     Therefore a connection to a CF instance is needed
     and the application has to be deployed and binded to the given service
     You can get special credentials in this way: JSONObject.getString("port")

     @return JSONObject with credentials of the given serviceId
     */
    public JSONObject getServiceCredentials(String serviceName, String applicationName)
        throws JSONException, JsonProcessingException {

        ApplicationEnvironments environments = cloudFoundryOperations.applications()
            .getEnvironments(GetApplicationEnvironmentsRequest.builder()
                .name(applicationName)
                .build()).block();

        Object env = environments.getSystemProvided();
        ObjectMapper mapper = new ObjectMapper();
        String serviceCredentials = mapper.writeValueAsString(env);
        JSONObject jsonObject = new JSONObject(serviceCredentials).getJSONObject("VCAP_SERVICES");

        return jsonObject.getJSONArray(serviceName)
            .getJSONObject(0) //TODO: Check if always on the same index
            .getJSONObject("credentials");
    }

    /**
     Creates the services
     Deploys the application with minimal attributes and bind application to service.
     */
    public boolean pushApplication(Path pathToApplication, String name, List<Service> services)
        throws InterruptedException {

        boolean succeed = false;
        for (Service service : services) {
            createService(service.getServiceInstanceName(), service.getServiceName(), service.getPlan());
        }
        succeed = deployApplication(pathToApplication, name, services);

        return succeed;
    }

    /**
     deploys a small application to the cloud foundry instance.
     contains only default values, the application represents not an instance of the template model
     */
    private boolean deployApplication(Path pathToApplication, String name,
                                      List<Service> services) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        String[] serviceInstanceNames = new String[services.size()];
        for (int i = 0; i < services.size(); i++) {
            serviceInstanceNames[i] = services.get(i).getServiceInstanceName();
        }

        AtomicBoolean succeed = new AtomicBoolean(false);

        cloudFoundryOperations.applications()
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .path(pathToApplication)
                    .name(name)
                    .service(serviceInstanceNames)
                    .randomRoute(true)
                    .build())
                .noStart(TRUE)
                .build())
            .doOnSubscribe(s -> logger.info("Deployment Started"))
            .doOnError(t -> this.logger.error("Deployment Failed", t))
            .doOnSuccess(v -> this.logger.info("Deployment Successful"))
            .doOnSuccess(u -> succeed.set(true))
            .subscribe(System.out::println, t -> latch.countDown(), latch::countDown);

        latch.await();

        return succeed.get();
    }

    /**
     creates a service on the cloud foundry instance
     */
    private void createService(String serviceInstanceName, String serviceName, String plan)
        throws InterruptedException {

        CountDownLatch latchService = new CountDownLatch(1);

        cloudFoundryOperations.services()
            .createInstance(CreateServiceInstanceRequest.builder()
                .serviceInstanceName(serviceInstanceName)
                .serviceName(serviceName)
                .planName(plan)
                .build())
            .doOnSubscribe(s -> logger.info("Create Service Started"))
            .doOnError(t -> this.logger.error("Service creation Failed", t))
            .doOnSuccess(v -> this.logger.info("Service creation Successful"))
            .subscribe(System.out::println, t -> latchService.countDown(), latchService::countDown);

        latchService.await();
    }
}
