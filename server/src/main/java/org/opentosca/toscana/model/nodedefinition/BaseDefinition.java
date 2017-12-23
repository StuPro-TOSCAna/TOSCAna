package org.opentosca.toscana.model.nodedefinition;

import java.util.HashMap;
import java.util.Map;

/**
 Abstract utility class for transforming TOSCA key names to java field names.
 */
public class BaseDefinition {

    /**
     If the tosca key name can not simply be converter to camel case in order to reflect the corresponding property name,
     a custom mapping can be defined here.
     */
    protected final Map<String, String> mappings = new HashMap<>();

    /**
     Converts names following the underscore convention to camel case.
     */
    public String resolve(String toscaPropertyName) {
        return mappings.getOrDefault(toscaPropertyName, toCamelCase(toscaPropertyName));
    }

    private static String toCamelCase(String toscaPropertyName) {
        boolean shift = false;
        StringBuilder builder = new StringBuilder();
        for (char c : toscaPropertyName.toCharArray()) {
            if (c != '_') {
                String appendix = String.valueOf(c);
                if (shift) {
                    appendix = appendix.toUpperCase();
                    shift = false;
                }
                builder.append(appendix);
            } else {
                shift = true;
            }
        }
        return builder.toString();
    }
}
