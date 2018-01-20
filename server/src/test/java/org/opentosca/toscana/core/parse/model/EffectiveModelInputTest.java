package org.opentosca.toscana.core.parse.model;

import java.util.Map;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.EffectiveModel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EffectiveModelInputTest extends BaseUnitTest {

    @Test
    public void inputTest() {
        EffectiveModel model = new EffectiveModel(TestCsars.VALID_INPUTS_TEMPLATE, log);
        Map<String, Property> inputs = model.getInputs();
        assertNotNull(inputs);
        assertEquals(4, inputs.size());
        Property input = inputs.get("string-input");
        assertNotNull(input);
        assertTrue(input.getDescription().isPresent());
        assertEquals("description1", input.getDescription().get());
        assertTrue(input.isRequired());
    }
}
