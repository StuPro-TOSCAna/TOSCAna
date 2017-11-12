package org.opentosca.toscana.core.transformation.properties.validators;

public class StringValidator implements ValueValidator {

    private final String regex;

    public StringValidator() {
        this(null);
    }

    public StringValidator(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean isValid(String input) {
        return regex == null || input.matches(regex);
    }
}
