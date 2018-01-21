package org.opentosca.toscana.core.parse.converter;

import org.opentosca.toscana.core.transformation.properties.PropertyType;

/**
 Util class for converting parameters defined in a service template to intances of Parameter.class
 */
public class ParameterConverter {

    public static PropertyType convertType(String type) {
        switch (type) {
            case "string":
                return PropertyType.TEXT;
            case "integer":
                return PropertyType.INTEGER;
            case "float":
                return PropertyType.FLOAT;
            case "boolean":
                return PropertyType.BOOLEAN;
            default:
                throw new UnsupportedOperationException(String.format("'%s' is not a supported yaml type", type));
        }
    }
}
