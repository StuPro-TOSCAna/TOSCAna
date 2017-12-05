package org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryClient;

import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.client.v2.serviceplans.ServicePlans;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.services.ListServiceOfferingsRequest;
import org.cloudfoundry.operations.services.ServiceOffering;
import org.cloudfoundry.operations.services.ServicePlan;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryService;

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

    public List<CloudFoundryService> getServices() {
        ListServiceOfferingsRequest serviceOfferingsRequest = ListServiceOfferingsRequest.builder().build();
        List<ServiceOffering> list = cloudFoundryOperations.services().listServiceOfferings(serviceOfferingsRequest).collectList().block();
        ArrayList<CloudFoundryService> services = new ArrayList<>();
        for (ServiceOffering service : list) {
            ArrayList<ServicePlan> plans = new ArrayList<>();
            for (ServicePlan plan : service.getServicePlans()) {
                plans.add(plan);
            }
            services.add(new CloudFoundryService(service.getLabel(), service.getDescription(), plans));
        }
        return services;
    }
}
