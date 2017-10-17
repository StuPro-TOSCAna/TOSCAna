package org.opentosca.toscana.core.transformation.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opentosca.toscana.core.transformation.properties.validators.FloatValidator;
import org.opentosca.toscana.core.transformation.properties.validators.IntegerValidator;
import org.opentosca.toscana.core.transformation.properties.validators.StringValidator;
import org.opentosca.toscana.core.transformation.properties.validators.ValueValidator;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ValidatorTest {

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
			{new FloatValidator(), true, "10.1"}
		});
	}

	@Test
	public void testValidation() throws Exception {
		boolean result = validator.isValid(testValue);
		assertTrue(result == accept);
	}
}
