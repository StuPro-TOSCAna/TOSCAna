package org.opentosca.toscana.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.api.utils.HALRelationUtils;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.testdata.ByteArrayUtils;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogEntry;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.opentosca.toscana.core.transformation.properties.OutputProperty;
import org.opentosca.toscana.core.transformation.properties.PlatformInput;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import ch.qos.logback.classic.Level;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransformationControllerTest extends BaseSpringTest {

    //<editor-fold desc="Constant Definition">

    private final static String INPUTS_VALID = "{\n" +
        "    \"inputs\": [\n" +
        "        {\n" +
        "            \"key\": \"text_input\",\n" +
        "            \"type\": \"text\",\n" +
        "            \"required\": true,\n" +
        "            \"description\": \"\",\n" +
        "            \"value\": \"Hallo\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"key\": \"name_input\",\n" +
        "            \"type\": \"name\",\n" +
        "            \"required\": true,\n" +
        "            \"description\": \"\",\n" +
        "            \"value\": \"myname\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"_links\": {\n" +
        "        \"self\": {\n" +
        "            \"href\": \"http://localhost:8080/csars/mongo-db/transformations/p-a/inputs\"\n" +
        "        }\n" +
        "    } " +
        "}";
    private final static String INPUTS_INVALID = "{\n" +
        "    \"inputs\": [\n" +
        "        {\n" +
        "            \"key\": \"text_input\",\n" +
        "            \"type\": \"text\",\n" +
        "            \"required\": true,\n" +
        "            \"description\": \"\",\n" +
        "            \"value\": \"Hallo\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"key\": \"unsigned_integer\",\n" +
        "            \"type\": \"unsigned_integer\",\n" +
        "            \"required\": true,\n" +
        "            \"description\": \"\",\n" +
        "            \"value\": \"-111\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"_links\": {\n" +
        "        \"self\": {\n" +
        "            \"href\": \"http://localhost:8080/csars/mongo-db/transformations/p-a/inputs\"\n" +
        "        }\n" +
        "    } " +
        "}";
    private final static String INPUTS_MISSING_VALUE = "{\n" +
        "    \"inputs\": [\n" +
        "        {\n" +
        "            \"key\": \"text_input\",\n" +
        "            \"type\": \"text\",\n" +
        "            \"required\": true,\n" +
        "            \"description\": \"\",\n" +
        "        },\n" +
        "    ],\n" +
        "    \"_links\": {\n" +
        "        \"self\": {\n" +
        "            \"href\": \"http://localhost:8080/csars/mongo-db/transformations/p-a/inputs\"\n" +
        "        }\n" +
        "    } " +
        "}";

    private final static String VALID_CSAR_NAME = "kubernetes-cluster";
    private final static String VALID_PLATFORM_NAME = "p-a";
    private final static String START_TRANSFORMATION_VALID_URL = "/api/csars/kubernetes-cluster/transformations/p-a/start";
    private final static String INPUTS_VALID_URL = "/api/csars/kubernetes-cluster/transformations/p-a/inputs";
    private final static String DEFAULT_CHARSET_HAL_JSON = "application/hal+json;charset=UTF-8";
    private final static String ARTIFACT_RESPONSE_EXPECTED_URL = "http://localhost/api/csars/kubernetes-cluster/transformations/p-a/artifact";
    private final static String GET_ARTIFACTS_VALID_URL = "/api/csars/kubernetes-cluster/transformations/p-a/artifact";
    private final static String GET_LOGS_AT_START_ZERO_VALID_URL = "/api/csars/kubernetes-cluster/transformations/p-a/logs?start=0";
    private final static String GET_LOGS_NEGATIVE_START_URL = "/api/csars/kubernetes-cluster/transformations/p-a/logs?start=-1";
    private final static String DELETE_TRANSFORMATION_VALID_URL = "/api/csars/kubernetes-cluster/transformations/p-a/delete";
    private final static String TRANSFORMATION_DETAILS_VALID_URL = "/api/csars/kubernetes-cluster/transformations/p-a";
    private final static String APPLICATION_HAL_JSON_MIME_TYPE = "application/hal+json";
    private final static String LIST_TRANSFORMATIONS_VALID_URL = "/api/csars/kubernetes-cluster/transformations/";
    private final static String LIST_TRANSFORMATIONS_EXPECTED_URL = "http://localhost/api/csars/kubernetes-cluster/transformations/";
    private final static String CREATE_CSAR_VALID_URL = "/api/csars/kubernetes-cluster/transformations/p-a/create";
    private final static String PLATFORM_NOT_FOUND_URL = "/api/csars/kubernetes-cluster/transformations/p-z";
    private final static String GET_OUTPUT_URL = "/api/csars/kubernetes-cluster/transformations/p-a/outputs";
    private final static String CSAR_NOT_FOUND_URL = "/api/csars/keinechtescsar/transformations";
    private static final String[] CSAR_NAMES = new String[]{"kubernetes-cluster", "apache-test", "mongo-db"};
    private static final String SECOND_VALID_PLATFORM_NAME = "p-b";
    private static final String INPUT_TEST_DEFAULT_VALUE = "Test-Default-Value";
    private static final String PROPERTY_TEST_DEFAULT_VALUE_KEY = "default_value_property";
    //</editor-fold>

    private CsarService csarService;
    private TransformationService transformationService;
    private PlatformService platformService;
    private MockMvc mvc;

    //<editor-fold desc="Initialization">

    @Before
    public void setUp() throws Exception {
        //Create Objects
        mockCsarService();
        mockPlatformService();
        mockTransformationService();

        TransformationController controller = new TransformationController(csarService, transformationService, platformService);

        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void mockCsarService() {
        csarService = mock(CsarService.class);
        List<Csar> csars = new ArrayList<>();
        when(csarService.getCsar(anyString())).thenReturn(Optional.empty());
        for (String name : CSAR_NAMES) {
            Csar csar = new CsarImpl(TestCsars.VALID_EMPTY_TOPOLOGY_TEMPLATE, name, logMock());
            when(csarService.getCsar(name)).thenReturn(Optional.of(csar));
        }
        when(csarService.getCsars()).thenReturn(csars);
    }

    private void mockPlatformService() {
        platformService = mock(PlatformService.class);

        Set<Platform> platforms = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            HashSet<PlatformInput> inputs = new HashSet<>();
            for (PropertyType type : PropertyType.values()) {
                inputs.add(new PlatformInput(type.getTypeName() + "_input", type));
            }
            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            if (i == 0) {
                inputs.add(new PlatformInput(
                    PROPERTY_TEST_DEFAULT_VALUE_KEY,
                    PropertyType.TEXT,
                    "",
                    false,
                    INPUT_TEST_DEFAULT_VALUE
                ));
            }
            platforms.add(new Platform("p-" + chars[i], "platform-" + (i + 1), inputs));
        }
        when(platformService.getSupportedPlatforms()).thenReturn(platforms);
        when(platformService.isSupported(any(Platform.class))).thenReturn(false);
        when(platformService.findPlatformById(anyString())).thenReturn(Optional.empty());
        for (Platform platform : platforms) {
            when(platformService.findPlatformById(platform.id)).thenReturn(Optional.of(platform));
            when(platformService.isSupported(platform)).thenReturn(true);
        }
    }

    private void mockTransformationService() {
        transformationService = mock(TransformationService.class);
        when(transformationService.createTransformation(any(Csar.class), any(Platform.class))).then(iom -> {
            Csar csar = (Csar) iom.getArguments()[0];
            Platform platform = (Platform) iom.getArguments()[1];
            Transformation t = new TransformationImpl(csar, platform, logMock(), modelMock());
            csar.getTransformations().put(platform.id, t);
            return t;
        });
    }
    //</editor-fold>

    //<editor-fold desc="Output Tests">

    @Test
    public void testGetOutputs() throws Exception {
        List<Transformation> transformations = preInitNonCreationTests();
        Transformation t = transformations.get(0);
        when(t.getState()).thenReturn(TransformationState.DONE);
        String outputKey = "test_output";
        List<OutputProperty> outputs = Lists.newArrayList(new PlatformInput(
            outputKey,
            PropertyType.TEXT,
            "",
            true,
            "some value"
        ));
        when(t.getOutputs()).thenReturn(outputs);
        mvc.perform(get(GET_OUTPUT_URL))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.outputs").isArray())
            .andExpect(jsonPath("$.links[0].href").value("http://localhost" + GET_OUTPUT_URL))
            .andExpect(jsonPath("$.outputs[0].key").value(outputKey))
            .andReturn();
    }

    @Test
    public void testGetOutputsEmptyOutputs() throws Exception {
        List<Transformation> transformations = preInitNonCreationTests();
        Transformation t = transformations.get(0);
        when(t.getState()).thenReturn(TransformationState.DONE);
        when(t.getOutputs()).thenReturn(new ArrayList<>());
        mvc.perform(get(GET_OUTPUT_URL))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.outputs").isArray())
            .andExpect(jsonPath("$.links[0].href").value("http://localhost/api/csars/kubernetes-cluster/transformations/p-a/outputs"))
            .andReturn();
    }

    @Test
    public void testGetOutputsInvalidState() throws Exception {
        List<Transformation> transformations = preInitNonCreationTests();
        Transformation t = transformations.get(0);
        when(t.getState()).thenReturn(TransformationState.TRANSFORMING);
        mvc.perform(get(GET_OUTPUT_URL)).andDo(print()).andExpect(status().is(400)).andReturn();
    }

    @Test
    public void testOutputInvalidPlatform() throws Exception {
        mvc.perform(get(PLATFORM_NOT_FOUND_URL + "/outputs"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void testOutputInvalidCsar() throws Exception {
        mvc.perform(get(CSAR_NOT_FOUND_URL + "/p-a/outputs"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    //</editor-fold>

    //<editor-fold desc="Start transformation tests">

    @Test
    public void testStartTransformationSuccess() throws Exception {
        preInitNonCreationTests();

        when(transformationService.startTransformation(any(Transformation.class))).thenReturn(true);

        mvc.perform(
            post(START_TRANSFORMATION_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUTS_VALID)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
    }

    @Test
    public void testStartTransformationFail() throws Exception {
        preInitNonCreationTests();
        when(transformationService.startTransformation(any(Transformation.class))).thenReturn(false);
        mvc.perform(
            post(START_TRANSFORMATION_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUTS_VALID)
        ).andDo(print())
            .andExpect(status().is(400))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
        assertNotEquals(TransformationState.TRANSFORMING,
            csarService.getCsar(VALID_CSAR_NAME).get().getTransformation(VALID_PLATFORM_NAME).get().getState());
    }

    //</editor-fold>

    //<editor-fold desc="SimpleProperty tests">
    @Test
    public void setTransformationProperties() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            put(INPUTS_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUTS_VALID)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
    }

    @Test
    public void setTransformationPropertiesInvalidInput() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            put(INPUTS_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUTS_INVALID)
        ).andDo(print())
            .andExpect(status().is(406))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.inputs[0].valid").isBoolean())
            .andExpect(jsonPath("$.inputs[0].key").isString())
            .andExpect(jsonPath("$.inputs[1].valid").isBoolean())
            .andExpect(jsonPath("$.inputs[1].key").isString())
            .andReturn();
    }

    @Test
    public void setTransformationPropertiesMissingValueInvalidInput() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            put(INPUTS_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INPUTS_MISSING_VALUE)
        ).andDo(print())
            .andExpect(status().is(400))
            .andReturn();
    }

    @Test
    public void getInputsTest() throws Exception {
        preInitNonCreationTests();
        MvcResult result = mvc.perform(
            get(INPUTS_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andExpect(jsonPath("$.inputs").isArray())
            .andExpect(jsonPath("$.inputs").isNotEmpty())
            .andExpect(jsonPath("$.inputs[0].key").isString())
            .andExpect(jsonPath("$.inputs[0].type").isString())
            .andExpect(jsonPath("$.inputs[0].description").isString())
            .andExpect(jsonPath("$.inputs[0].required").isBoolean())
            .andExpect(jsonPath("$.links[0].rel").value("self"))
            .andExpect(jsonPath("$.links[0].href")
                .value("http://localhost/api/csars/kubernetes-cluster/transformations/p-a/inputs"))
            .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String responseJson = new String(response.getContentAsByteArray());
        String[] values = JsonPath.parse(responseJson).read("$.inputs[*].value", String[].class);
        long nullCount = Arrays.asList(values).stream().filter(Objects::isNull).count();
        long testCount = Arrays.asList(values).stream().filter(e -> e != null && e.equals(INPUT_TEST_DEFAULT_VALUE)).count();
        assertEquals(8, nullCount);
        assertEquals(1, testCount);
    }

    @Test
    public void getInputsTest2() throws Exception {
        preInitNonCreationTests();
        //Set a SimpleProperty value
        String inputKey = "secret_input";
        String inputValue = "geheim";
        csarService.getCsar(VALID_CSAR_NAME)
            .get().getTransformation(VALID_PLATFORM_NAME).get()
            .getInputs().set(inputKey, inputValue);
        //Perform a request
        MvcResult result = mvc.perform(
            get(INPUTS_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andReturn();
        // Check if only one value is set (the one that has been set above) and the others are not!
        JSONArray obj = new JSONObject(result.getResponse().getContentAsString()).getJSONArray("inputs");
        boolean valueFound = false;
        boolean restNull = true;
        for (int i = 0; i < obj.length(); i++) {
            JSONObject content = obj.getJSONObject(i);
            if (content.getString("key").equals(inputKey)) {
                valueFound = content.getString("value").equals(inputValue);
            } else {
                restNull = restNull && (content.isNull("value")
                    || content.getString("value").equals(INPUT_TEST_DEFAULT_VALUE));
            }
        }
        assertTrue("Could not find valid value in property list", valueFound);
        assertTrue("Not all other values in property list are null or equal to the default value", restNull);
    }

    //</editor-fold>

    //<editor-fold desc="Test Artifact Retrieval">
    @Test
    public void retrieveArtifact() throws Exception {
        preInitNonCreationTests();

        File dummyFile = new File(tmpdir, "test.bin");
        dummyFile.delete();
        byte[] data = ByteArrayUtils.generateRandomByteArray(new Random(123), 2048);
        FileUtils.writeByteArrayToFile(dummyFile, data);

        when(csarService.getCsar(VALID_CSAR_NAME).get().getTransformation(VALID_PLATFORM_NAME).get().getTargetArtifact())
            .thenReturn(Optional.of(new TargetArtifact(dummyFile)));

        mvc.perform(
            get(GET_ARTIFACTS_VALID_URL)
        )
            .andExpect(status().is(200))
            .andExpect(content().contentType("application/octet-stream"))
            .andExpect(content().bytes(data))
            .andReturn();
    }

    @Test
    public void retrieveArtifactNotFinished() throws Exception {
        preInitNonCreationTests();
        when(
            csarService.getCsar(VALID_CSAR_NAME).get()
                .getTransformation(VALID_PLATFORM_NAME).get().getTargetArtifact()
        ).thenReturn(Optional.empty());
        mvc.perform(
            get(GET_ARTIFACTS_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(400))
            .andReturn();
    }

    //</editor-fold>

    //<editor-fold desc="Test Transformation Logs">
    @Test
    public void retrieveTransformationLogs() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            get(GET_LOGS_AT_START_ZERO_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andExpect(jsonPath("$.start").value(0))
            .andExpect(jsonPath("$.end").isNumber())
            .andExpect(jsonPath("$.logs").isArray())
            .andExpect(jsonPath("$.logs[0]").exists())
            .andExpect(jsonPath("$.logs[0].timestamp").isNumber())
            .andExpect(jsonPath("$.logs[0].level").isString())
            .andExpect(jsonPath("$.logs[0].message").isString())
            .andReturn();
    }

    @Test
    public void retrieveLogsNegativeIndex() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            get(GET_LOGS_NEGATIVE_START_URL)
        ).andDo(print())
            .andExpect(status().is(400));
    }

    //</editor-fold>

    //<editor-fold desc="Delete Transformation Tests">
    @Test
    public void deleteTransformation() throws Exception {
        preInitNonCreationTests();
        //Set the return value of the delete method
        when(transformationService.deleteTransformation(any(Transformation.class))).thenReturn(true);
        //Execute Request
        mvc.perform(
            delete(DELETE_TRANSFORMATION_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(200))
            .andReturn();
    }

    @Test
    public void deleteTransformationStillRunning() throws Exception {
        preInitNonCreationTests();
        //Set the return value of the delete method
        when(transformationService.deleteTransformation(any(Transformation.class))).thenReturn(false);
        //Execute Request
        mvc.perform(
            delete(DELETE_TRANSFORMATION_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(400))
            .andReturn();
    }
    //</editor-fold>

    //<editor-fold desc="Transformation Details Test">
    @Test
    public void transformationDetails() throws Exception {
        preInitNonCreationTests();
        MvcResult result = mvc.perform(
            get(TRANSFORMATION_DETAILS_VALID_URL).accept(APPLICATION_HAL_JSON_MIME_TYPE)
        )
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andExpect(jsonPath("$.phases").isArray())
            .andExpect(jsonPath("$.phases").isEmpty())
            .andExpect(jsonPath("$.platform").value(VALID_PLATFORM_NAME))
            .andExpect(jsonPath("$.state").value("INPUT_REQUIRED"))
            .andReturn();
        JSONObject object = new JSONObject(result.getResponse().getContentAsString());
        HALRelationUtils.validateRelations(
            object.getJSONArray("links"),
            getLinkRelationsForTransformationDetails(),
            VALID_CSAR_NAME,
            VALID_PLATFORM_NAME
        );
    }

    private Map<String, String> getLinkRelationsForTransformationDetails() {
        HashMap<String, String> map = new HashMap<>();
        map.put("self", "http://localhost/api/csars/%s/transformations/%s");
        map.put("logs", "http://localhost/api/csars/%s/transformations/%s/logs?start=0");
        map.put("platform", "http://localhost/api/platforms/p-a");
        map.put("artifact", "http://localhost/api/csars/%s/transformations/%s/artifact");
        map.put("inputs", "http://localhost/api/csars/%s/transformations/%s/inputs");
        map.put("delete", "http://localhost/api/csars/%s/transformations/%s/delete");
        return map;
    }
    //</editor-fold>

    //<editor-fold desc="List Transformation Tests">
    @Test
    public void listTransformations() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            get(LIST_TRANSFORMATIONS_VALID_URL).accept(APPLICATION_HAL_JSON_MIME_TYPE)
        )
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0]").exists())
            .andExpect(jsonPath("$.content[1]").exists())
            .andExpect(jsonPath("$.content[2]").doesNotExist())
            .andExpect(jsonPath("$.links[0].href")
                .value(LIST_TRANSFORMATIONS_EXPECTED_URL))
            .andExpect(jsonPath("$.links[0].rel").value("self"))
            .andExpect(jsonPath("$.links[1]").doesNotExist())
            .andReturn();
    }

    @Test
    public void listEmptyTransformations() throws Exception {
        mvc.perform(
            get(LIST_TRANSFORMATIONS_VALID_URL).accept(DEFAULT_CHARSET_HAL_JSON)
        )
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0]").doesNotExist())
            .andExpect(jsonPath("$.links[0].href")
                .value(LIST_TRANSFORMATIONS_EXPECTED_URL))
            .andExpect(jsonPath("$.links[0].rel").value("self"))
            .andExpect(jsonPath("$.links[1]").doesNotExist())
            .andReturn();
    }
    //</editor-fold>

    //<editor-fold desc="Create Transformation Tests">
    @Test
    public void createTransformation() throws Exception {
        //Make sure no previous transformations are present
        assertEquals(0, csarService.getCsar(VALID_CSAR_NAME).get().getTransformations().size());
        //Call creation Request
        mvc.perform(put(CREATE_CSAR_VALID_URL))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
        //Check if the transformation has been added to the archive
        assertEquals(1, csarService.getCsar(VALID_CSAR_NAME).get().getTransformations().size());
        assertTrue(csarService.getCsar(VALID_CSAR_NAME).get().getTransformation(VALID_PLATFORM_NAME).isPresent());
    }

    @Test
    public void createTransformationTwice() throws Exception {
        //call the first time
        mvc.perform(put(CREATE_CSAR_VALID_URL))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
        //Call the second time
        mvc.perform(put(CREATE_CSAR_VALID_URL))
            .andDo(print())
            .andExpect(status().is(400))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
    }
    //</editor-fold>

    //<editor-fold desc="Platform Not found Tests">
    @Test
    public void newTransformationPlatformNotFound() throws Exception {
        mvc.perform(put(PLATFORM_NOT_FOUND_URL + "/create"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationInfoPlatformNotFound() throws Exception {
        mvc.perform(get(PLATFORM_NOT_FOUND_URL + ""))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationLogsPlatformNotFound() throws Exception {
        mvc.perform(get(PLATFORM_NOT_FOUND_URL + "/logs"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationArtifactPlatformNotFound() throws Exception {
        mvc.perform(get(PLATFORM_NOT_FOUND_URL + "/artifacts"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationPropertiesGetPlatformNotFound() throws Exception {
        mvc.perform(get(PLATFORM_NOT_FOUND_URL + "/inputs"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationInputsPutPlatformNotFound() throws Exception {
        mvc.perform(
            put(PLATFORM_NOT_FOUND_URL + "/inputs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"inputs\": [{}]}")
        ).andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationDeletePlatformNotFound() throws Exception {
        mvc.perform(delete(PLATFORM_NOT_FOUND_URL + "/delete"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationStartPlatformNotFound() throws Exception {
        mvc.perform(delete(PLATFORM_NOT_FOUND_URL + "/delete"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }
    //</editor-fold>

    //<editor-fold desc="CSAR Not found Tests">
    @Test
    public void listTransformationsCsarNotFound() throws Exception {
        mvc.perform(get(CSAR_NOT_FOUND_URL + ""))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void newTransformationCsarNotFound() throws Exception {
        mvc.perform(put(CSAR_NOT_FOUND_URL + "/p-a/create"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationInfoCsarNotFound() throws Exception {
        mvc.perform(get(CSAR_NOT_FOUND_URL + "/p-a"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationLogsCsarNotFound() throws Exception {
        mvc.perform(get(CSAR_NOT_FOUND_URL + "/p-a/logs"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationArtifactCsarNotFound() throws Exception {
        mvc.perform(get(CSAR_NOT_FOUND_URL + "/p-a/artifacts"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationInputsGetCsarNotFound() throws Exception {
        mvc.perform(get(CSAR_NOT_FOUND_URL + "/p-a/inputs"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationInputsPutCsarNotFound() throws Exception {
        mvc.perform(
            put(CSAR_NOT_FOUND_URL + "/p-a/inputs")
                .content("{\"inputs\": [{}]}")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationDeleteCsarNotFound() throws Exception {
        mvc.perform(delete(CSAR_NOT_FOUND_URL + "/p-a/delete"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationStartCsarNotFound() throws Exception {
        mvc.perform(post(CSAR_NOT_FOUND_URL + "/p-a/start"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }
    //</editor-fold>

    //<editor-fold desc="Util Methods">
    public List<Transformation> preInitNonCreationTests() throws PlatformNotFoundException {
        //add a transformation
        Optional<Csar> csar = csarService.getCsar(VALID_CSAR_NAME);
        assertTrue(csar.isPresent());
        String[] pnames = {VALID_PLATFORM_NAME, SECOND_VALID_PLATFORM_NAME};

        List<Transformation> transformations = new ArrayList<>();

        for (String pname : pnames) {

            LogEntry entry = new LogEntry(0, "Test Context", "Test Message", Level.DEBUG);
            Log mockLog = logMock();
            when(mockLog.getLogEntries(0)).thenReturn(Collections.singletonList(entry));

            Transformation transformation = new TransformationImpl(
                csar.get(),
                platformService.findPlatformById(pname).get(),
                mockLog, modelMock()
            );
            transformation = spy(transformation);
            transformations.add(transformation);
            csar.get().getTransformations().put(pname, transformation);
        }
        return transformations;
    }
    //</editor-fold>
}
