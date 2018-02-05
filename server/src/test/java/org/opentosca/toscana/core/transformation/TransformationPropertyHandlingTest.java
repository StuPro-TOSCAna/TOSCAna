package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.util.HashSet;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PlatformInput;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransformationPropertyHandlingTest extends BaseUnitTest {
    private static final String MOCK_CSAR_NAME = "test";

    private TransformationImpl transformation;
    private PropertyInstance properties;

    @Mock
    private Log log;

    @Before
    public void setUp() throws Exception {
        Csar csar = new CsarImpl(new File(""), MOCK_CSAR_NAME, log);

        HashSet<PlatformInput> props = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            props.add(
                new PlatformInput(
                    "prop-" + i,
                    PropertyType.UNSIGNED_INTEGER,
                    "No real Description",
                    i < 5 //Only mark the first 5 properties as required
                )
            );
        }
        Platform p = new Platform("test", "Test Platform", props);
        transformation = new TransformationImpl(csar, p, log, modelMock());
        properties = transformation.getInputs();
    }

    @Test
    public void setValidProperty() throws Exception {
        for (int i = 0; i < 10; i++) {
            properties.set("prop-" + i, "1");
        }
        for (int i = 0; i < 10; i++) {
            assertEquals("1", properties.get("prop-" + i).get());
        }
    }

    @Test
    public void setInvalidPropertyValue() throws Exception {
        boolean success = properties.set("prop-1", "-13");
        assertFalse(success);
    }

    @Test(expected = NoSuchPropertyException.class)
    public void setInvalidPropertyKey() throws Exception {
        properties.set("prop-112", "-13");
    }

    @Test
    public void checkRequiredPropertiesSetTrue() throws Exception {
        for (int i = 0; i < 5; i++) {
            properties.set("prop-" + i, "1");
        }
        assertTrue(properties.isValid());
    }

    @Test
    public void checkAllRequiredPropertiesFalse() throws Exception {
        for (int i = 0; i < 4; i++) {
            properties.set("prop-" + i, "1");
        }
        assertFalse(properties.isValid());
    }

    @Test
    public void checkEmptyProperties() throws Exception {
        Csar csar = new CsarImpl(new File(""), MOCK_CSAR_NAME, log);
        this.transformation = new TransformationImpl(csar,
            new Platform("test", "test", new HashSet<>()), logMock(), modelMock());
        assertEquals(TransformationState.READY, transformation.getState());
        assertTrue(transformation.getInputs().isValid());
    }
}
