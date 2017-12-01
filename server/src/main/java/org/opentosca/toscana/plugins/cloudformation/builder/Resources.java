package org.opentosca.toscana.plugins.cloudformation.builder;

import java.util.HashMap;
import java.util.Map;

public class Resources {

    private Map<String, Resource> resources;

    public Resources(){
        this.resources = new HashMap<>();
    }

    public void addParameter(String name, Resource resource){
        this.resources.put(name, resource);
    }
}
