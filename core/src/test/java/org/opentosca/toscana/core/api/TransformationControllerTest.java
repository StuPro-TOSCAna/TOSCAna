package org.opentosca.toscana.core.api;

import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.api.utils.HALRelationUtils;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.dummy.DummyCsarService;
import org.opentosca.toscana.core.dummy.DummyPlatformService;
import org.opentosca.toscana.core.dummy.DummyTransformation;
import org.opentosca.toscana.core.dummy.DummyTransformationService;
import org.opentosca.toscana.core.transformation.platform.PlatformService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.core.transformation.TransformationState.TRANSFORMING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransformationControllerTest extends BaseSpringTest {

    private static final String VALID_PROPERTY_INPUT = "{\n" +
        "\t\"properties\": {\n" +
        "\t\t\"text_property\":\"Hallo Welt\",\n" +
        "\t\t\"name_property\": \"hallo\",\n" +
        "\t\t\"secret_property\": \"I bims 1 geheimnis\",\n" +
        "\t\t\"unsigned_integer_property\": \"1337\"\n" +
        "\t}\n" +
        "}";
    private static final String INVALID_PROPERTY_INPUT = "{\n" +
        "\t\"properties\": {\n" +
        "\t\t\"text_property\":\"Meddl Loide\",\n" +
        "\t\t\"name_property\": \"meddl\",\n" +
        "\t\t\"secret_property\": \"I bims 1 geheimnis\",\n" +
        "\t\t\"unsigned_integer_property\": \"-1337\"\n" +
        "\t}\n" +
        "}";
    public static final String VALID_CSAR_NAME = "k8s-cluster";
    public static final String VALID_PLATFORM_NAME = "p-a";
    public static final String START_TRANSFORMATION_VALID_URL = "/csars/k8s-cluster/transformations/p-a/start";
    public static final String GET_PROPERTIES_VALID_URL = "/csars/k8s-cluster/transformations/p-a/properties";
    public static final String DEFAULT_CHARSET_HAL_JSON = "application/hal+json;charset=UTF-8";
    public static final String ARTIFACT_RESPONSE_EXPCETED_URL = "http://localhost/csars/k8s-cluster/transformations/p-a/artifact";
    public static final String GET_ARTIFACTS_VALID_URL = "/csars/k8s-cluster/transformations/p-a/artifact";
    public static final String GET_LOGS_AT_START_ZERO_VALID_URL = "/csars/k8s-cluster/transformations/p-a/logs?start=0";
    public static final String DELETE_TRANSFORMATION_VALID_URL = "/csars/k8s-cluster/transformations/p-a/delete";
    public static final String TRANSFORMATION_DETAILS_VALID_URL = "/csars/k8s-cluster/transformations/p-a";
    public static final String APPLICATION_HAL_JSON_MIME_TYPE = "application/hal+json";
    public static final String LIST_TRANSFORMATIONS_VALID_URL = "/csars/k8s-cluster/transformations/";
    public static final String LIST_TRANSFORMATIONS_EXPECTED_URL = "http://localhost/csars/k8s-cluster/transformations/";
    public static final String CREATE_CSAR_VALID_URL = "/csars/k8s-cluster/transformations/p-a/create";
    public static final String PLATFORM_NOT_FOUND_URL = "/csars/k8s-cluster/transformations/p-z";
    public static final String CSAR_NOT_FOUND_URL = "/csars/keinechtescsar/transformations";

    private TransformationController controller;
    private CsarService csarService;
    private DummyTransformationService transformationService;
    private PlatformService platformService;
    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        //Create Objects
        csarService = new DummyCsarService(tmpdir);
        transformationService = new DummyTransformationService();
        platformService = new DummyPlatformService();
        controller = new TransformationController(csarService, transformationService, platformService);

        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    //<editor-fold desc="Start transformation tests">

    @Test
    public void testStartTransformationSuccess() throws Exception {
        preInitNonCreationTests();
        transformationService.setStartReturnValue(true);
        mvc.perform(
            post(START_TRANSFORMATION_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_PROPERTY_INPUT)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
        assertEquals(TRANSFORMING,
            csarService.getCsar(VALID_CSAR_NAME).getTransformations().get(VALID_PLATFORM_NAME).getState());
    }

    @Test
    public void testStartTransformationFail() throws Exception {
        preInitNonCreationTests();
        transformationService.setStartReturnValue(false);
        mvc.perform(
            post(START_TRANSFORMATION_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_PROPERTY_INPUT)
        ).andDo(print())
            .andExpect(status().is(400))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
        assertNotEquals(TRANSFORMING,
            csarService.getCsar(VALID_CSAR_NAME).getTransformations().get(VALID_PLATFORM_NAME).getState());
    }

    //</editor-fold>

    //<editor-fold desc="Property tests">
    @Test
    public void setTransformationProperties() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            put(GET_PROPERTIES_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_PROPERTY_INPUT)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
    }

    @Test
    public void setTransformationPropertiesInvalidInput() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            put(GET_PROPERTIES_VALID_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(INVALID_PROPERTY_INPUT)
        ).andDo(print())
            .andExpect(status().is(406))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.valid_inputs.name_property").value(true))
            .andExpect(jsonPath("$.valid_inputs.text_property").value(true))
            .andExpect(jsonPath("$.valid_inputs.secret_property").value(true))
            .andExpect(jsonPath("$.valid_inputs.unsigned_integer_property").value(false))
            .andReturn();
    }

    @Test
    public void getTransformationProperties() throws Exception {
        preInitNonCreationTests();
        mvc.perform(
            get(GET_PROPERTIES_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andExpect(jsonPath("$.properties").isArray())
            .andExpect(jsonPath("$.properties").isNotEmpty())
            .andExpect(jsonPath("$.properties[0].key").isString())
            .andExpect(jsonPath("$.properties[0].type").isString())
            .andExpect(jsonPath("$.properties[0].description").isString())
            .andExpect(jsonPath("$.properties[0].required").isBoolean())
            .andExpect(jsonPath("$.properties[0].value").doesNotExist()) //Check that the value is null
            .andExpect(jsonPath("$.links[0].rel").value("self"))
            .andExpect(jsonPath("$.links[0].href")
                .value("http://localhost/csars/k8s-cluster/transformations/p-a/properties"))
            .andReturn();
    }

    @Test
    public void getTransformationPropertyValues() throws Exception {
        preInitNonCreationTests();
        //Set a Property value
        csarService.getCsar(VALID_CSAR_NAME)
            .getTransformations().get(VALID_PLATFORM_NAME)
            .getProperties().setPropertyValue("secret_property", "geheim");
        //Perform a request
        MvcResult result = mvc.perform(
            get(GET_PROPERTIES_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andReturn();
        // Check if only one value is set (the one that has been set above) and the others are not!
        JSONArray obj = new JSONObject(result.getResponse().getContentAsString()).getJSONArray("properties");
        boolean valueFound = false;
        boolean restNull = true;
        for (int i = 0; i < obj.length(); i++) {
            JSONObject content = obj.getJSONObject(i);
            if (content.getString("key").equals("secret_property")) {
                valueFound = content.getString("value").equals("geheim");
            } else {
                restNull = restNull && content.isNull("value");
            }
        }
        assertTrue("Could not find valid value in property list", valueFound);
        assertTrue("Not all other values in property list are null", restNull);
    }

    //</editor-fold>
    
    //<editor-fold desc="Test Artifact Retrieval">
    @Test
    public void retrieveArtifact() throws Exception {
        preInitNonCreationTests();
        ((DummyTransformation) csarService.getCsar(VALID_CSAR_NAME).getTransformations().get(VALID_PLATFORM_NAME))
            .setReturnTargetArtifact(true);
        mvc.perform(
            get(GET_ARTIFACTS_VALID_URL)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().contentType(DEFAULT_CHARSET_HAL_JSON))
            .andExpect(jsonPath("$.access_url").isString())
            .andExpect(jsonPath("$.links").isArray())
            .andExpect(jsonPath("$.links[0].rel").value("self"))
            .andExpect(jsonPath("$.links[0].href")
                .value(ARTIFACT_RESPONSE_EXPCETED_URL))
            .andReturn();
    }

    @Test
    public void retrieveArtifactNotFinished() throws Exception {
        preInitNonCreationTests();
        ((DummyTransformation) csarService.getCsar(VALID_CSAR_NAME).getTransformations().get(VALID_PLATFORM_NAME))
            .setReturnTargetArtifact(false);
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

    //</editor-fold>
    
    //<editor-fold desc="Delete Transformation Tests">
    @Test
    public void deleteTransformation() throws Exception {
        preInitNonCreationTests();
        //Set the return value of the delete method
        ((DummyTransformationService) transformationService).setDeleteReturnValue(true);
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
        ((DummyTransformationService) transformationService).setDeleteReturnValue(false);
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
            .andExpect(jsonPath("$.progress").value(0))
            .andExpect(jsonPath("$.platform").value(VALID_PLATFORM_NAME))
            .andExpect(jsonPath("$.status").value("INPUT_REQUIRED"))
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
        map.put("self", "http://localhost/csars/%s/transformations/%s");
        map.put("logs", "http://localhost/csars/%s/transformations/%s/logs?start=0");
        map.put("platform", "http://localhost/platforms/p-a");
        map.put("artifact", "http://localhost/csars/%s/transformations/%s/artifact");
        map.put("properties", "http://localhost/csars/%s/transformations/%s/properties");
        map.put("delete", "http://localhost/csars/%s/transformations/%s/delete");
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
        assertTrue(csarService.getCsar(VALID_CSAR_NAME).getTransformations().entrySet().size() == 0);
        //Call creation Request
        mvc.perform(put(CREATE_CSAR_VALID_URL))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().bytes(new byte[0]))
            .andReturn();
        //Check if the transformation has been added to the archive
        assertTrue(csarService.getCsar(VALID_CSAR_NAME).getTransformations().entrySet().size() == 1);
        assertTrue(csarService.getCsar(VALID_CSAR_NAME).getTransformations().get(VALID_PLATFORM_NAME) != null);
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
        mvc.perform(get(PLATFORM_NOT_FOUND_URL + "/properties"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationPropertiesPutPlatformNotFound() throws Exception {
        mvc.perform(
            put(PLATFORM_NOT_FOUND_URL + "/properties")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"properties\": {}}")
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
    public void transformationPropertiesGetCsarNotFound() throws Exception {
        mvc.perform(get(CSAR_NOT_FOUND_URL + "/p-a/properties"))
            .andDo(print()).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void transformationPropertiesPutCsarNotFound() throws Exception {
        mvc.perform(
            put(CSAR_NOT_FOUND_URL + "/p-a/properties")
                .content("{\"properties\": {}}")
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
    public void preInitNonCreationTests() throws PlatformNotFoundException {
        //add a transformation
        Csar csar = csarService.getCsar(VALID_CSAR_NAME);
        transformationService.createTransformation(csar, platformService.findPlatformById(VALID_PLATFORM_NAME));
        transformationService.createTransformation(csar, platformService.findPlatformById("p-b"));
    }
    //</editor-fold>
}
