package org.opentosca.toscana.plugins.cloudformation.builder;

import java.util.HashMap;
import java.util.Map;

public class Parameter {

    public Map<String, String> values;

    public Parameter(String description, String type) {
        this.values = new HashMap<>();
        values.put("Description", description);
        values.put("Type", type);
    }
}
