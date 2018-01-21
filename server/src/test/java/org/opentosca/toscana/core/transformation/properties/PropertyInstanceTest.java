package org.opentosca.toscana.core.transformation.properties;

import java.util.HashSet;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.core.transformation.TransformationState.INPUT_REQUIRED;
import static org.opentosca.toscana.core.transformation.TransformationState.READY;

public class PropertyInstanceTest extends BaseUnitTest {

    private PropertyInstance instance;
    private Transformation transformation;

    @Before
    public void init() {
        HashSet<PlatformProperty> properties = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            properties.add(new PlatformProperty("p-" + i, PropertyType.INTEGER, "", i < 5));
        }
        Platform testPlatform = new Platform("test", "test", properties);

        transformation = new TransformationImpl(
            new CsarImpl("test", mock(Log.class)),
            testPlatform,
            mock(Log.class)
        );

        this.instance = new PropertyInstance(new HashSet<>(properties), transformation);
    }

    @Test
    public void checkStateNoPropsSet() {
        assertEquals(INPUT_REQUIRED, this.transformation.getState());
    }

    @Test
    public void checkStateAllRequiredPropsSet() {
        for (int i = 0; i < 5; i++) {
            assertEquals(INPUT_REQUIRED, this.transformation.getState());
            this.instance.setPropertyValue("p-" + i, "" + i);
        }
        assertTrue(this.instance.requiredPropertiesSet());
        assertFalse(this.instance.allPropertiesSet());
        assertEquals(READY, this.transformation.getState());
    }

    @Test
    public void checkAllPropsSet() {
        for (int i = 0; i < 10; i++) {
            if (i < 5) {
                assertEquals(INPUT_REQUIRED, this.transformation.getState());
            } else {
                assertEquals(READY, this.transformation.getState());
            }
            this.instance.setPropertyValue("p-" + i, "" + i);
        }
        assertTrue(this.instance.requiredPropertiesSet());
        assertTrue(this.instance.allPropertiesSet());
    }

    @Test
    public void checkSetInvalidProperty() {
        for (int i = 0; i < 4; i++) {
            assertEquals(INPUT_REQUIRED, this.transformation.getState());
            this.instance.setPropertyValue("p-" + i, "" + i);
        }
        try {
            this.instance.setPropertyValue("p-4", "achd");
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.out);
        }
        assertFalse(this.instance.requiredPropertiesSet());
        assertFalse(this.instance.allPropertiesSet());
        assertEquals(INPUT_REQUIRED, this.transformation.getState());
    }
}
