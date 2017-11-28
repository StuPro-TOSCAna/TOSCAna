package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.IOException;
import java.util.Map;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

public class CloudFoundryFileCreator {

    private static PluginFileAccess fileAccess;
    private CloudFoundryApplication app;

    public CloudFoundryFileCreator(PluginFileAccess fileAccess, CloudFoundryApplication app){
        this.fileAccess=fileAccess;
        this.app=app;
    }

    public void createFiles() throws IOException{
        createManifest();
        createBpConfig();
        createDeploySh();
    }

    private void createManifest() throws IOException{
        String manifestContent;
        manifestContent=createHeadMani();
        manifestContent+=createEnv();
        manifestContent+=createService();
        fileAccess.access("/manifest.yml").append(manifestContent).close();
    }

    private String createHeadMani(){
        return "applications: \n- name: "+app.getAppName();
    }

    private String createEnv(){
        String envBlock;
        envBlock="\n  env:";
        for (Map.Entry<String, String> entry : app.getEnvVariables().entrySet()){
            envBlock=envBlock+"\n    " +entry.getKey()+": "+entry.getValue();
        }
        return envBlock;
    }

    private String createService(){
        String serviceBlock;
        serviceBlock="\n  service:";
        for (String service : app.getServices()){
            serviceBlock+="\n    - "+service;
        }
        return serviceBlock;
    }


    private void createDeploySh() throws IOException{
        fileAccess.access("/deploy_"+app.getAppName()+".sh")
            .append("cf -push "+fileAccess.getAbsolutePath("/")+app.getAppName()).close();
    }

    private void createBpConfig() throws IOException{
        String buildPackAdditions="{\"PHP-EXTENSIONS\":[";
        for(String bp : app.getBpAdditions()){
            buildPackAdditions+="\""+bp+"\",";
        }
        buildPackAdditions+="]}";
        fileAccess.createDirectories("/.bp-config");
        fileAccess.access("/.bp-config/options.json").append(buildPackAdditions).close();
    }



}
