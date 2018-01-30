package org.opentosca.toscana.core.transformation.properties;

import java.util.Arrays;
import java.util.Collection;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.transformation.properties.validators.BooleanValidator;
import org.opentosca.toscana.core.transformation.properties.validators.FloatValidator;
import org.opentosca.toscana.core.transformation.properties.validators.IntegerValidator;
import org.opentosca.toscana.core.transformation.properties.validators.StringValidator;
import org.opentosca.toscana.core.transformation.properties.validators.ValueValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertSame;

@RunWith(Parameterized.class)
public class ValidatorTest extends BaseUnitTest {

    private final ValueValidator validator;
    private final boolean accept;
    private final String testValue;

    public ValidatorTest(ValueValidator validator, boolean accept, String testValue) {
        this.validator = validator;
        this.accept = accept;
        this.testValue = testValue;
    }

    @Parameterized.Parameters(name = "{index}: {0} of {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new StringValidator(), true, "Hello world"},
            {new StringValidator(), false, null},
            {new IntegerValidator(false), false, "Hello world"},
            {new IntegerValidator(false), true, "123456"},
            {new IntegerValidator(true), false, "-1"},
            {new IntegerValidator(true), true, "0"},
            {new IntegerValidator(true), true, "100"},
            {new IntegerValidator(true), false, null},
            {new FloatValidator(), false, "Not a number"},
            {new FloatValidator(), false, null},
            {new FloatValidator(), true, "10"},
            {new FloatValidator(), true, "10.1"},
            {new BooleanValidator(), true, "true"},
            {new BooleanValidator(), true, "TRUE"},
            {new BooleanValidator(), false, "23"},
            {new BooleanValidator(), false, null}
        });
    }

    @Test
    public void testValidation() throws Exception {
        boolean result = validator.isValid(testValue);
        assertSame(accept, result);
    }
}
