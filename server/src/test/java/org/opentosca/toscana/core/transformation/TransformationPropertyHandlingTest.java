package org.opentosca.toscana.core.transformation;

import java.util.HashSet;
import java.util.Map;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PlatformProperty;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TransformationPropertyHandlingTest extends BaseUnitTest {
    private static final String MOCK_CSAR_NAME = "test";

    private TransformationImpl transformation;

    @Mock
    private Log log;

    @Before
    public void setUp() throws Exception {
        Csar csar = new CsarImpl(MOCK_CSAR_NAME, log);

        HashSet<PlatformProperty> props = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            props.add(
                new PlatformProperty(
                    "prop-" + i,
                    PropertyType.UNSIGNED_INTEGER,
                    "No real Description",
                    i < 5 //Only mark the first 5 properties as required
                )
            );
        }
        Platform p = new Platform("test", "Test Platform", props);
        transformation = new TransformationImpl(csar, p, log);
    }

    @Test
    public void setValidProperty() throws Exception {
        for (int i = 0; i < 10; i++) {
            transformation.setProperty("prop-" + i, "1");
        }
        Map<String, String> property = transformation.getProperties().getPropertyValues();
        for (int i = 0; i < 10; i++) {
            assertEquals("1", property.get("prop-" + i));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidPropertyValue() throws Exception {
        transformation.setProperty("prop-1", "-13");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidPropertyKey() throws Exception {
        transformation.setProperty("prop-112", "-13");
    }

    @Test
    public void checkAllPropsSetFalse() throws Exception {
        for (int i = 0; i < 9; i++) {
            transformation.setProperty("prop-" + i, "1");
        }
        assertFalse(transformation.allPropertiesSet());
    }

    @Test
    public void checkAllPropsSetTrue() throws Exception {
        for (int i = 0; i < 10; i++) {
            transformation.setProperty("prop-" + i, "1");
        }
        assertTrue(transformation.allPropertiesSet());
    }

    @Test
    public void checkAllRequiredPropertiesTrue() throws Exception {
        for (int i = 0; i < 5; i++) {
            transformation.setProperty("prop-" + i, "1");
        }
        assertTrue(transformation.allRequiredPropertiesSet());
        assertFalse(transformation.allPropertiesSet());
    }

    @Test
    public void checkAllRequiredPropertiesFalse() throws Exception {
        for (int i = 0; i < 4; i++) {
            transformation.setProperty("prop-" + i, "1");
        }
        assertFalse(transformation.allRequiredPropertiesSet());
        assertFalse(transformation.allPropertiesSet());
    }

    @Test
    public void checkEmptyProperties() throws Exception {
        Csar csar = new CsarImpl(MOCK_CSAR_NAME, log);
        this.transformation = new TransformationImpl(csar, new Platform("test", "test", new HashSet<>()), mock(Log.class));
        assertTrue(transformation.allRequiredPropertiesSet());
        assertTrue(transformation.allPropertiesSet());
    }
}
