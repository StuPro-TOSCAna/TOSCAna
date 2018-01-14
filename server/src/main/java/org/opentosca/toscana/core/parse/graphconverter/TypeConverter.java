package org.opentosca.toscana.core.parse.graphconverter;

import org.opentosca.toscana.model.requirement.Requirement;

public class TypeConverter {

    public static <T> T convert(String string, Class targetType) {
        if (targetType.getSimpleName().equals("String")) {
            return (T) string;
        } else if (targetType.getSimpleName().equals("Integer")) {
            return (T) Integer.valueOf(string);
        } else if (Requirement.class.isAssignableFrom(targetType)) {
            System.out.println("yippie");
            return null;
        } else {
            throw new UnsupportedOperationException(String.format(
                "Cannot convert value of type %s: currently unsupported", targetType.getSimpleName()));
        }
        // TODO support for Credential
    }
}
