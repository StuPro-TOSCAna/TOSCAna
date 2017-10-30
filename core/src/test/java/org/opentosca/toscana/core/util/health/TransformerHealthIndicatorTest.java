package org.opentosca.toscana.core.util.health;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.dummy.DummyCsar;
import org.opentosca.toscana.core.dummy.DummyTransformation;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.platform.Platform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM1;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM2;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORMS;

public class TransformerHealthIndicatorTest extends BaseJUnitTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private PluginService pluginService;
    private CsarDao repository;

    private TransformerHealthIndicator indicator;

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        pluginService = Mockito.mock(PluginService.class);
        repository = Mockito.mock(CsarDao.class);

        when(pluginService.getSupportedPlatforms()).thenReturn(new HashSet<>());
        when(repository.findAll()).thenReturn(new ArrayList<>());

        indicator = new TransformerHealthIndicator(repository, pluginService);
    }

    @Test
    public void testIndicatorNoPlatformsAndTransformations() throws Exception {
        String json = getHealthAsString();
        assertTrue(getPlatforms(json).isEmpty());
        assertTrue(getErroredTransformations(json).isEmpty());
        assertTrue(getRunningTransformations(json).isEmpty());
        assertEquals("UP", getStatus(json));
    }

    @Test
    public void testPlatformList() throws Exception {
        when(pluginService.getSupportedPlatforms()).thenReturn(PLATFORMS);
        String json = getHealthAsString();
        List<String> platforms = getPlatforms(json);
        assertEquals(PLATFORMS.size(), platforms.size());
        Map<String, Boolean> foundPlatforms = new HashMap<>();
        for (Platform platform : PLATFORMS) {
            foundPlatforms.put(platform.id, false);
            for (String p : platforms) {
                if (p.equals(platform.id)) {
                    log.info("Found platform {}", p);
                    foundPlatforms.put(p, true);
                }
            }
        }
        assertTrue("Could not find all platforms", foundPlatforms.values().stream().allMatch(e -> e));
    }

    @Test
    public void testErroredAndRunningTransformations() throws Exception {
        //initialize test environment (done here to test the rest seperately)
        initTestEnvironment();

        //Get Json Content
        String json = getHealthAsString();

        //Check platform size
        List<String> platforms = getPlatforms(json);
        assertEquals(2, platforms.size());

        //Check Running transformations
        checkCsarList(getRunningTransformations(json), PLATFORM1, "test");

        //Check Errored Transformations
        checkCsarList(getErroredTransformations(json), PLATFORM2, "test");
    }

    private void checkCsarList(List<Map<String, String>> running, Platform platform, String csarName) {
        assertEquals(1, running.size());
        Map<String, String> res = running.get(0);
        assertEquals(res.get("csar"), csarName);
        assertEquals(res.get("platform"), platform.id);
    }

    private void initTestEnvironment() {
        //Create Dummy Csar
        DummyCsar csar = new DummyCsar("test");

        //Create Running Transformation
        DummyTransformation transformationRunning = new DummyTransformation(PLATFORM1);
        transformationRunning.setCsar(csar);
        transformationRunning.setState(TransformationState.TRANSFORMING);
        csar.getTransformations().put(PLATFORM1.id, transformationRunning);

        //Create Errored Transformation
        DummyTransformation transformationErrored = new DummyTransformation(PLATFORM2);
        transformationErrored.setCsar(csar);
        transformationErrored.setState(TransformationState.ERROR);
        csar.getTransformations().put(PLATFORM2.id, transformationErrored);

        //Update Mockito
        //Platforms
        Set<Platform> platformSet = new HashSet<>();
        platformSet.addAll(Arrays.asList(PLATFORM1, PLATFORM2));
        when(pluginService.getSupportedPlatforms()).thenReturn(platformSet);

        //Repository
        when(repository.findAll()).thenReturn(Collections.singletonList(csar));
    }

    private String getStatus(String json) {
        return JsonPath.read(json, "$.status");
    }

    private List<Map<String, String>> getErroredTransformations(String json) {
        return JsonPath.read(json, "$.errored_transformations");
    }

    private List<Map<String, String>> getRunningTransformations(String json) {
        return JsonPath.read(json, "$.running_transformations");
    }

    private List<String> getPlatforms(String json) {
        return JsonPath.read(json, "$.installed_plugins");
    }

    private String getHealthAsString() throws JsonProcessingException {
        String json = mapper.writeValueAsString(indicator.health());
        log.info("Looking at JSON {}", json);
        return json;
    }
}
