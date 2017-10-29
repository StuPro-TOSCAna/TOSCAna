package org.opentosca.toscana.core.transformation.properties;

import java.util.Arrays;
import java.util.Collection;

import org.opentosca.toscana.core.BaseJUnitTest;
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
public class ValidatorTest extends BaseJUnitTest {

    private ValueValidator validator;
    private boolean accept;
    private String testValue;

    public ValidatorTest(ValueValidator validator, boolean accept, String testValue) {
        this.validator = validator;
        this.accept = accept;
        this.testValue = testValue;
    }

    @Parameterized.Parameters(name = "{index}: {0} of {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new StringValidator(), true, "Hallo Welt"},
            {new IntegerValidator(false), false, "Hallo Welt"},
            {new IntegerValidator(false), true, "123456"},
            {new IntegerValidator(true), false, "-1"},
            {new IntegerValidator(true), true, "0"},
            {new IntegerValidator(true), true, "100"},
            {new FloatValidator(), false, "Keine Zahl"},
            {new FloatValidator(), true, "10"},
            {new FloatValidator(), true, "10.1"},
            {new BooleanValidator(), true, "true"},
            {new BooleanValidator(), true, "TRUE"},
            {new BooleanValidator(), false, "23"}
        });
    }

    @Test
    public void testValidation() throws Exception {
        boolean result = validator.isValid(testValue);
        assertSame(accept, result);
    }
}
