package org.opentosca.toscana.core.transformation.properties.validators;

public class BooleanValidator implements ValueValidator {
    @Override
    public boolean isValid(String input) {
        String lowerCase = input.toLowerCase();
        return lowerCase.equals("false") || lowerCase.equals("true");
    }
}
