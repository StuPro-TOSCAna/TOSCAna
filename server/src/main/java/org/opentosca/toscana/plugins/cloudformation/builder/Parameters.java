package org.opentosca.toscana.plugins.cloudformation.builder;

import java.util.HashMap;
import java.util.Map;


public class Parameters {
    
    private Map<String, Parameter> parameters;

    public Parameters(){
        this.parameters = new HashMap<>();
    }
    
    public void addParameter(String name, Parameter param){
        this.parameters.put(name, param);
    }
}
