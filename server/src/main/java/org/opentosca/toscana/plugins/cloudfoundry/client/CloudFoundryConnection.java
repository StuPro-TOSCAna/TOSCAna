package org.opentosca.toscana.plugins.cloudfoundry.client;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
import org.slf4j.LoggerFactory;

import static java.lang.Boolean.TRUE;

/**
 implements java-cf-client
 create a connection to the cf provider
 */
public class CloudFoundryConnection {

    private final static Logger logger = LoggerFactory.getLogger(CloudFoundryConnection.class);
    
    private String userName;
    private String password;
    private String apiHost;
    private String organization;
    private String space;
    private CloudFoundryOperations cloudFoundryOperations;

    public CloudFoundryConnection(String username, String password,
                                  String apiHost, String organization,
                                  String space) {

        this.userName = username;
        this.password = password;
        this.apiHost = apiHost;
        this.organization = organization;
        this.space = space;

        this.cloudFoundryOperations = createCloudFoundryOperations();
    }

    private CloudFoundryOperations createCloudFoundryOperations() {
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

        CloudFoundryOperations cloudFoundryOperations = DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(reactorClient)
            .organization(organization)
            .space(space)
            .build();

        return cloudFoundryOperations;
    }

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
    public JSONObject getServiceCredentials(String serviceName, String applicationName) throws JSONException, JsonProcessingException {
        ApplicationEnvironments environments = cloudFoundryOperations.applications().getEnvironments(GetApplicationEnvironmentsRequest.builder()
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
     Creates the service
     Deploys the application with minimal attributes.
     */
    public void pushApplication(Path pathToApplication, String name, String serviceName, String plan,
                                String serviceInstanceName) throws InterruptedException {

        createService(serviceInstanceName,serviceName,plan);
        
        CountDownLatch latch = new CountDownLatch(1);

        cloudFoundryOperations.applications()
            .pushManifest(PushApplicationManifestRequest.builder()
                .manifest(ApplicationManifest.builder()
                    .path(pathToApplication)
                    .name(name)
                    .service(serviceInstanceName)
                    .build())
                .noStart(TRUE)
                .build())
            .doOnSubscribe(s -> logger.info("Deployment Started"))
            .doOnError(t -> this.logger.error("Deployment Failed", t))
            .doOnSuccess(v -> this.logger.info("Deployment Successful"))
            .subscribe(System.out::println, t -> latch.countDown(), latch::countDown);

        latch.await();
    }
    
    private void createService(String serviceInstanceName, String serviceName, String plan) throws InterruptedException {
        
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
