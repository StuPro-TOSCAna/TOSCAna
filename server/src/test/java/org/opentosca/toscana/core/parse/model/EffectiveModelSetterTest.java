package org.opentosca.toscana.core.parse.model;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.TestTemplates;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.node.SoftwareComponent;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 Tests the functionality of setters of tosca elements.
 */
public class EffectiveModelSetterTest extends BaseUnitTest {

    private SoftwareComponent component;

    @Before
    public void setUp() {
        EffectiveModel model = new EffectiveModelFactory().create(TestTemplates.Nodes.SOFTWARE_COMPONENT, logMock());
        component = (SoftwareComponent) model.getNodeMap().get("software_component");
    }

    @Test
    public void firstTimeSetterTest() {
        String expected = "test-description";
        component.setDescription(expected);
        assertEquals(expected, component.getDescription().get());
    }

    @Test
    public void setAgainTest() {
        String expected = "test-description";
        component.setDescription(expected);
        expected = "another-test-description";
        component.setDescription(expected);
        assertEquals(expected, component.getDescription().get());
    }
}
