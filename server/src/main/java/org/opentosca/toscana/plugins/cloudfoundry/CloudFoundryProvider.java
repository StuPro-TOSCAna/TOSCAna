package org.opentosca.toscana.plugins.cloudfoundry;

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
    
    private List<CloudFoundryService> offeredService;
    private CloudFoundryProviderType name;
    
    public CloudFoundryProvider(CloudFoundryProviderType name) {
        this.name = name;
    }
    
    public void setOfferedService(List<CloudFoundryService> offeredService) {
        this.offeredService = offeredService;
    }
}
