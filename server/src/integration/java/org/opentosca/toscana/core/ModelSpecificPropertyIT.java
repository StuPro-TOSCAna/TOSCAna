package org.opentosca.toscana.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.retrofit.TOSCAnaAPI;
import org.opentosca.toscana.retrofit.model.Platform;
import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.TransformationProperty;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 Tests setting model specific properties.
 Involved components are: REST API, core, rename.
 */
public class ModelSpecificPropertyIT extends BaseSpringIntegrationTest {

    private static final String[] KEYS = {"string-input", "boolean-input", "integer-input", "float-input"};
    private static final String[] TYPES = {"text", "boolean", "integer", "float"};
    private static final String[] DESCRIPTIONS = {"description1", "description2", "description3", "description4"};
    private static final String[] VALUES = {null, null, null, "default-value"};
    private static final boolean[] REQUIRED = {true, true, false, false};

    private TOSCAnaAPI api;

    private TransformationProperty property = new TransformationProperty();

    @Before
    public void setUp() {
        api = new TOSCAnaAPI(getHttpUrl());
    }

    /**
     Tests whether given CSAR's inputs are read and served correctly by the server
     */
    @Test
    public void getPropertiesTest() throws IOException, TOSCAnaServerException {
        String csarName = "test-csar";
        api.uploadCsar(csarName, TestCsars.VALID_INPUTS);
        List<Platform> platforms = api.getPlatforms().getContent();
        String platform = platforms.get(0).getId();
        api.createTransformation(csarName, platform);
        Transformation t = api.getTransformation(csarName, platform);
        List<TransformationProperty> properties = api.getProperties(csarName, platform).getProperties();
        // could be more than 4 properties: might contain platform properties in addition to the TOSCA inputs
        assertTrue(4 <= properties.size());
        Set<TransformationProperty> expectedProperties = buildExpectedProperties();
        for (TransformationProperty expected : expectedProperties) {
            boolean match = false;
            for (TransformationProperty property : properties) {
                if (expected.getKey().equals(property.getKey()) &&
                    expected.getDescription().equals(property.getDescription()) &&
                    expected.getType().equals(property.getType()) &&
                    expected.isRequired() == property.isRequired()) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                throw new IllegalStateException(String.format(
                    "SimpleProperty '%s' does not match expected properties (one of %s)",
                    property, expectedProperties));
            }
        }
    }

    public Set<TransformationProperty> buildExpectedProperties() {
        Set<TransformationProperty> properties = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            TransformationProperty property = new TransformationProperty();
            property.setKey(KEYS[i]);
            property.setType(TYPES[i]);
            property.setDescription(DESCRIPTIONS[i]);
            property.setRequired(REQUIRED[i]);
            property.setValue(VALUES[i]);
            properties.add(property);
        }
        return properties;
    }
}
