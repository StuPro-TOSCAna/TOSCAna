package org.opentosca.toscana.plugins.cloudfoundry.client;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationEnvironments;
import org.cloudfoundry.operations.applications.GetApplicationEnvironmentsRequest;
import org.cloudfoundry.operations.services.ListServiceOfferingsRequest;
import org.cloudfoundry.operations.services.ServiceOffering;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.json.JSONException;
import org.json.JSONObject;

/**
 implements java-cf-client
 create a connection to the cf provider
 */
public class CloudFoundryConnection {

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
}
