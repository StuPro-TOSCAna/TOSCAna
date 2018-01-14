package org.opentosca.toscana.core.parse.graphconverter;

import org.apache.commons.lang3.EnumUtils;

public class TypeConverter {

    public static <T> T convert(String string, Class targetType) {
        if (targetType.getSimpleName().equals("String")) {
            return (T) string;
        } else if (targetType.getSimpleName().equals("Integer")) {
            return (T) Integer.valueOf(string);
        } else if (targetType.isEnum()){
            T result = (T) EnumUtils.getEnum(targetType, string);
            // TODO handle wrong values
            System.out.println(result);
            return result;
        } else {
            throw new UnsupportedOperationException(String.format(
                "Cannot convert value of type %s: currently unsupported", targetType.getSimpleName()));
        }
        // TODO support for Credential
    }
}
