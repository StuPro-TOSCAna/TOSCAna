package org.opentosca.toscana.plugins.cloudformation.builder;

import java.util.HashMap;
import java.util.Map;

public class Outputs {

    private Map<String, Output> outputs;

    public Outputs(){
        this.outputs = new HashMap<>();
    }

    public void addOutput(String name, Output output){
        this.outputs.put(name, output);
    }
}
