package org.opentosca.toscana.core.transformation.properties;

import java.util.HashSet;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.core.dummy.DummyTransformation;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.core.transformation.TransformationState.INPUT_REQUIRED;
import static org.opentosca.toscana.core.transformation.TransformationState.READY;

public class PropertyInstanceTest extends BaseJUnitTest {

    private PropertyInstance instance;
    private DummyTransformation transformation;

    @Before
    public void init() throws Exception {
        HashSet<Property> properties = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            properties.add(new Property("p-" + i, PropertyType.INTEGER, "", i < 5));
        }
        this.transformation = new DummyTransformation(new Platform("test", "test", properties));
        this.instance = transformation.getProperties();
    }

    @Test
    public void checkStateNoPropsSet() throws Exception {
        assertEquals(INPUT_REQUIRED, this.transformation.getState());
    }

    @Test
    public void checkStateAllRequiredPropsSet() throws Exception {
        for (int i = 0; i < 5; i++) {
            assertEquals(INPUT_REQUIRED, this.transformation.getState());
            this.instance.setPropertyValue("p-" + i, "" + i);
        }
        assertTrue(this.instance.allRequiredPropertiesSet());
        assertFalse(this.instance.allPropertiesSet());
        assertEquals(READY, this.transformation.getState());
    }

    @Test
    public void checkAllPropsSet() throws Exception {
        for (int i = 0; i < 10; i++) {
            if (i < 5) {
                assertEquals(INPUT_REQUIRED, this.transformation.getState());
            } else {
                assertEquals(READY, this.transformation.getState());
            }
            this.instance.setPropertyValue("p-" + i, "" + i);
        }
        assertTrue(this.instance.allRequiredPropertiesSet());
        assertTrue(this.instance.allPropertiesSet());
    }

    @Test
    public void checkSetInvalidProperty() throws Exception {
        for (int i = 0; i < 4; i++) {
            assertEquals(INPUT_REQUIRED, this.transformation.getState());
            this.instance.setPropertyValue("p-" + i, "" + i);
        }
        try {
            this.instance.setPropertyValue("p-4", "achd");
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.out);
        }
        assertFalse(this.instance.allRequiredPropertiesSet());
        assertFalse(this.instance.allPropertiesSet());
        assertEquals(INPUT_REQUIRED, this.transformation.getState());
    }
}
