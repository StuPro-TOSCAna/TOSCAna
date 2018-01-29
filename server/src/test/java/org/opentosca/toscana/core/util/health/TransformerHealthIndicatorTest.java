package org.opentosca.toscana.core.util.health;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM1;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM2;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORMS;
import static org.opentosca.toscana.core.transformation.TransformationState.ERROR;
import static org.opentosca.toscana.core.transformation.TransformationState.TRANSFORMING;

public class TransformerHealthIndicatorTest extends BaseUnitTest {

    //Name of the mocked csar
    private static final String MOCK_CSAR_NAME = "test";
    //Mock data to generate the transformations 
    //First index represents the platform
    //second index represents the Current transformation state
    private static final Object[][] MOCK_DATA = new Object[][]{
        {PLATFORM1, TRANSFORMING},
        {PLATFORM2, ERROR}
    };

    private final Logger log = LoggerFactory.getLogger(getClass());

    private PluginService pluginService;
    private CsarDao repository;

    private TransformerHealthIndicator indicator;

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        pluginService = mock(PluginService.class);
        repository = mock(CsarDao.class);

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
        checkCsarList(getRunningTransformations(json), PLATFORM1, MOCK_CSAR_NAME);

        //Check Errored Transformations
        checkCsarList(getErroredTransformations(json), PLATFORM2, MOCK_CSAR_NAME);
    }

    private void checkCsarList(List<Map<String, String>> running, Platform platform, String csarName) {
        assertEquals(1, running.size());
        Map<String, String> res = running.get(0);
        assertEquals(res.get("csar"), csarName);
        assertEquals(res.get("platform"), platform.id);
    }

    private void initTestEnvironment() {
        //Create Dummy Csar
        //DummyCsar csar = new DummyCsar("test");
        Csar csar = new CsarImpl(new File(""), MOCK_CSAR_NAME, mock(Log.class));
        csar = spy(csar);

        Map<String, Transformation> transformations = new HashMap<>();
        Set<Platform> platformSet = new HashSet<>();

        for (Object[] d : MOCK_DATA) {
            //Initialize transformation Mock
            Transformation transformation = new TransformationImpl(csar, (Platform) d[0], mock(Log.class), modelMock());
            transformation.setState((TransformationState) d[1]);
            transformations.put(((Platform) d[0]).id, transformation);

            //Add platform to supported platform list
            platformSet.add((Platform) d[0]);
        }

        //Add Transformations to csar
        when(csar.getTransformations()).thenReturn(transformations);

        //Platforms
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
