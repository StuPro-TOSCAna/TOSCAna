package org.opentosca.toscana.core.parse.model;

import java.util.Map;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EffectiveModelTest extends BaseUnitTest {

    @Test
    public void inputTest() {
        EffectiveModel model = new EffectiveModelFactory().create(TestCsars.VALID_INPUTS_TEMPLATE, logMock());
        Map<String, Property> inputs = model.getInputs();
        assertNotNull(inputs);
        assertEquals(4, inputs.size());
        Property input = inputs.get("string-input");
        assertNotNull(input);
        assertTrue(input.getDescription().isPresent());
        assertEquals("description1", input.getDescription().get());
        assertTrue(input.isRequired());
    }

    @Test
    public void outputTest() {
        EffectiveModel model = new EffectiveModelFactory().create(TestCsars.VALID_OUTPUTS_TEMPLATE, logMock());
        Map<String, Property> outputs = model.getOutputs();
        assertNotNull(outputs);
        assertEquals(1, outputs.size());
        Property linkedOutput = outputs.get("test_output_linked");
        assertNotNull(linkedOutput);
        assertTrue(linkedOutput.getDescription().isPresent());
        assertEquals("test-description2", linkedOutput.getDescription().get());
        assertTrue(linkedOutput.getValue().isPresent());
        assertEquals("8084", linkedOutput.getValue().get());
    }
}
