package org.opentosca.toscana.model.nodedefinition;

import java.util.HashMap;
import java.util.Map;

/**
 Abstract utility class for transforming TOSCA key names to java field names.
 */
public abstract class AbstractDefinition {

    /**
     If the tosca key name can not simply be converter to camel case in order to reflect the corresponding property name,
     a custom mapping can be defined here.
     */
    protected static final Map<String, String> MAPPINGS = new HashMap<>();

    /**
     Converts names following the underscore convention to camel case.
     */
    public static String resolve(String toscaPropertyName) {
        String javaPropertyName = MAPPINGS.getOrDefault(toscaPropertyName, toCamelCase(toscaPropertyName));
        return javaPropertyName;
    }

    private static String toCamelCase(String toscaPropertyName) {
        boolean shift = false;
        String javaPropertyName = "";
        for (char c : toscaPropertyName.toCharArray()) {
            if (c != '_') {
                String append = String.valueOf(c);
                if (shift) {
                    append = append.toUpperCase();
                    shift = false;
                }
                javaPropertyName += append;
            } else {
                shift = true;
            }
        }
        return javaPropertyName;
    }
}
