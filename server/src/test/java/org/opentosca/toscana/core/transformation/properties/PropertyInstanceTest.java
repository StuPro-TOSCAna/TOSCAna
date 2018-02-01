package org.opentosca.toscana.core.transformation.properties;

import java.io.File;
import java.util.HashSet;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
            new CsarImpl(new File(""), "test", logMock()),
            testPlatform,
            logMock(),
            modelMock()
        );

        this.instance = new PropertyInstance(new HashSet<>(properties), transformation);
    }

    @Test
    public void checkStateNoPropsSet() {
        assertEquals(INPUT_REQUIRED, this.transformation.getState());
    }

    @Test
    public void checkStateAllRequiredPropsSet() throws NoSuchPropertyException {
        for (int i = 0; i < 5; i++) {
            assertEquals(INPUT_REQUIRED, this.transformation.getState());
            boolean result = this.instance.set("p-" + i, "" + i);
            assertTrue("i = " + i, result);
        }
        assertTrue(this.instance.isValid());
        assertEquals(READY, this.transformation.getState());
    }

    @Test
    public void checkSetInvalidProperty() throws NoSuchPropertyException {
        for (int i = 0; i < 4; i++) {
            assertEquals(INPUT_REQUIRED, this.transformation.getState());
            this.instance.set("p-" + i, "" + i);
        }
        boolean result = this.instance.set("p-4", "achd");
        assertFalse(result);
        assertFalse(this.instance.isValid());
        assertEquals(INPUT_REQUIRED, this.transformation.getState());
    }

    @Test
    public void setNullValueNotRequired() throws NoSuchPropertyException {
        for (int i = 0; i < 5; i++) {
            this.instance.set("p-" + i, "" + i);
        }
        boolean success = this.instance.set("p-6", null);
        assertTrue(success);
        assertTrue(instance.isValid());
    }

    @Test
    public void setNullValueRequired() throws NoSuchPropertyException {
        for (int i = 0; i < 4; i++) {
            this.instance.set("p-" + i, "" + i);
        }
        boolean success = this.instance.set("p-4", null);
        assertFalse(success);
        assertFalse(instance.isValid());
    }
}
