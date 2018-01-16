package org.opentosca.toscana.plugins.cloudfoundry.application;

/**
 Created by jensmuller on 03.01.18.
 represents a service which is offered by a provider and the application needs
 */
public class Service {

    private String serviceName;
    private String serviceInstanceName;
    private String plan;
    private ServiceTypes serviceType;

    public Service(String serviceName, String serviceInstanceName, String plan, ServiceTypes serviceType) {
        this.serviceName = serviceName;
        this.serviceInstanceName = serviceInstanceName;
        this.plan = plan;
        this.serviceType = serviceType;
    }

    public ServiceTypes getServiceType() {
        return serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceInstanceName() {
        return serviceInstanceName;
    }

    public void setServiceInstanceName(String serviceInstanceName) {
        this.serviceInstanceName = serviceInstanceName;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }
}
