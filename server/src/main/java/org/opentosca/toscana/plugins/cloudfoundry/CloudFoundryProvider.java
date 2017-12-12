package org.opentosca.toscana.plugins.cloudfoundry;

import org.cloudfoundry.operations.services.ServiceOffering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jensmuller on 05.12.17.
 */
public class CloudFoundryProvider {

    public enum CloudFoundryProviderType {
        PIVOTAL;
    }

    private List<ServiceOffering> offeredService;
    private CloudFoundryProviderType name;

    public CloudFoundryProvider(CloudFoundryProviderType name) {
        this.name = name;
    }

    public void setOfferedService(List<ServiceOffering> offeredService) {
        this.offeredService = offeredService;
    }

    public List<ServiceOffering> getOfferedService() {
        return offeredService;
    }
}
