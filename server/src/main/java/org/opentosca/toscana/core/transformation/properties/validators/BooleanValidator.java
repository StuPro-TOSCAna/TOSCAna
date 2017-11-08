package org.opentosca.toscana.core.transformation.properties.validators;

public class BooleanValidator implements ValueValidator {
    @Override
    public boolean isValid(String input) {
        String lowerCase = input.toLowerCase();
        return "false".equals(lowerCase) || "true".equals(lowerCase);
    }
}
