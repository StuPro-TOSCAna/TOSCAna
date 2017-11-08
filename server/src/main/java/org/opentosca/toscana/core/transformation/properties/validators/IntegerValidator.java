package org.opentosca.toscana.core.transformation.properties.validators;

public class IntegerValidator implements ValueValidator {

    private final boolean onlyPositive;

    public IntegerValidator(boolean onlyPositive) {
        this.onlyPositive = onlyPositive;
    }

    @Override
    public boolean isValid(String input) {
        try {
            int i = Integer.parseInt(input);
            return !onlyPositive || i >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}
