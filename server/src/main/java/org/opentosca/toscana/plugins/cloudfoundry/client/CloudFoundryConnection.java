package org.opentosca.toscana.plugins.cloudfoundry.client;

import java.util.List;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.services.ListServiceOfferingsRequest;
import org.cloudfoundry.operations.services.ServiceOffering;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;

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
            .apiHost(apiHost) //"api.run.pivotal.io"
            .build();

        TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
            .password(password) //secret
            .username(userName) //jmuell.dev@gmail.com
            .build();

        ReactorCloudFoundryClient reactorClient = ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();

        CloudFoundryOperations cloudFoundryOperations = DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(reactorClient)
            .organization(organization) // "stupro.toscana"
            .space(space) // "development"
            .build();

        return cloudFoundryOperations;
    }

    public List<ServiceOffering> getServices() {
        ListServiceOfferingsRequest serviceOfferingsRequest = ListServiceOfferingsRequest.builder().build();
        List<ServiceOffering> listServiceOfferings = cloudFoundryOperations.services().listServiceOfferings(serviceOfferingsRequest).collectList().block();
        return listServiceOfferings;
    }
}
