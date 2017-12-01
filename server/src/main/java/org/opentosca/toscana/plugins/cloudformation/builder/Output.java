package org.opentosca.toscana.plugins.cloudformation.builder;

import java.util.HashMap;
import java.util.Map;

public class Output {
    public Map<String, String> values;

    public Output(String description, String value) {
        this.values = new HashMap<>();
        values.put("Description", description);
        values.put("Value", value);
    }
}
