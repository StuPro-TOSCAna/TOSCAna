package org.opentosca.toscana.plugins.cloudfoundry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFoundryApplication {
    //private final static Logger logger = LoggerFactory.getLogger(CloudFoundryApplication.class);
    private String appName;
    private Map<String,String> envVariables = new HashMap<>();
    private ArrayList<String> services = new ArrayList<>();
    private ArrayList<String> bpAdditions = new ArrayList<>();

    public CloudFoundryApplication(String appName){
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Map<String, String> getEnvVariables() {
        return envVariables;
    }

    public void addEnvVar(String envName, String value) {
        this.envVariables.put(envName, value);
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public void addService(String serviceName) {
        this.services.add(serviceName);
    }

    public ArrayList<String> getBpAdditions() {
        return bpAdditions;
    }

    public void addBp(String buildPack) {
        this.bpAdditions.add(buildPack);
    }


}
