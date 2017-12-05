package org.opentosca.toscana.plugins.cloudfoundry;

import org.cloudfoundry.client.v2.serviceplans.ServicePlans;
import org.cloudfoundry.operations.services.ServicePlan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jensmuller on 05.12.17.
 */
public class CloudFoundryService {
    
    private String name;
    private String description;
    private ArrayList<ServicePlan> plans;
    
    public CloudFoundryService(String name, String descriptions, ArrayList<ServicePlan> plans) {
        this.name = name;
        this.description = descriptions;
        this.plans = plans;
    }
    
}
