package org.opentosca.toscana.plugins.cloudfoundry.application;

import java.util.List;

import org.cloudfoundry.operations.services.ServiceOffering;

/**
 Created by jensmuller on 05.12.17.
 */
public class Provider {

    // will be expanded with several cloud Providers
    public enum CloudFoundryProviderType {
        PIVOTAL;
    }

    private List<ServiceOffering> offeredService;
    private CloudFoundryProviderType name;

    public Provider(CloudFoundryProviderType name) {
        this.name = name;
    }

    public void setOfferedService(List<ServiceOffering> offeredService) {
        this.offeredService = offeredService;
    }

    public List<ServiceOffering> getOfferedService() {
        return offeredService;
    }

    public CloudFoundryProviderType getName() {
        return name;
    }
}
