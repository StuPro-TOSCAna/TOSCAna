package org.opentosca.toscana.model;

import java.io.File;
import java.util.Map;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.graphconverter.ServiceModel;
import org.opentosca.toscana.core.transformation.properties.Property;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServiceModelTest extends BaseUnitTest {

    public static File INPUTS = new File("src/test/resources/csars/yaml/valid/inputs/inputs.yaml");

    @Test
    public void inputTest() {
        ServiceModel model = new ServiceModel(INPUTS, log);
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
