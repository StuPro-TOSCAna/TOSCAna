package org.opentosca.toscana.plugins.cloudfoundry.application;

/**
 this class represents a service which is offered by a provider and the application needs
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

    /**
     sets the service name. Should be the name of the service of the provider.
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceInstanceName() {
        return serviceInstanceName;
    }

    /**
     set the instance name of the service. is the name from the template
     */
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
