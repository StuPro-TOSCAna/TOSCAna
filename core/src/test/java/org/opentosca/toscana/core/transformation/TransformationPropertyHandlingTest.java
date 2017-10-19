package org.opentosca.toscana.core.transformation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.opentosca.toscana.core.api.dummy.DummyCsar;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.core.transformation.properties.RequirementType;

import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TransformationPropertyHandlingTest {

	private TransformationImpl transformation;

	@Before
	public void setUp() throws Exception {
		DummyCsar csar = new DummyCsar("dummy");
		HashSet<Property> props = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			props.add(new Property("prop-" + i, PropertyType.UNSIGNED_INTEGER, RequirementType.TRANSFORMATION));
		}
		for (int i = 0; i < 10; i++) {
			props.add(new Property("prop-deploy-" + i, PropertyType.UNSIGNED_INTEGER, RequirementType.DEPLOYMENT));
		}
		Platform p = new Platform("test", "Test Platform", props);
		transformation = new TransformationImpl(csar, p);
	}

	@Test
	public void setValidProperty() throws Exception {
		for (int i = 0; i < 10; i++) {
			transformation.setProperty("prop-" + i, "1");
		}
		Map<String, String> property = transformation.getProperties();
		for (int i = 0; i < 10; i++) {
			assertTrue(property.get("prop-" + i).equals("1"));
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
		assertFalse(transformation.isAllPropertiesSet(RequirementType.TRANSFORMATION));
	}

	@Test
	public void checkAllPropsSetTrue() throws Exception {
		for (int i = 0; i < 10; i++) {
			transformation.setProperty("prop-" + i, "1");
		}
		assertTrue(transformation.isAllPropertiesSet(RequirementType.TRANSFORMATION));
	}
}
