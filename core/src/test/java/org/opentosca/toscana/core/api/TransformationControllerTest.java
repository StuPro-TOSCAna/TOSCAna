package org.opentosca.toscana.core.api;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.toscana.core.api.dummy.DummyCsarService;
import org.opentosca.toscana.core.api.dummy.DummyPlatformService;
import org.opentosca.toscana.core.api.dummy.DummyTransformation;
import org.opentosca.toscana.core.api.dummy.DummyTransformationService;
import org.opentosca.toscana.core.api.utils.HALRelationUtils;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(
	classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
public class TransformationControllerTest {
	
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
	
	private TransformationController controller;
	private CsarService csarService;
	private TransformationService transformationService;
	private PlatformService platformService;
	private MockMvc mvc;

	@Before
	public void setUp() throws Exception {
		//Create Objects
		csarService = new DummyCsarService();
		transformationService = new DummyTransformationService();
		platformService = new DummyPlatformService();
		controller = new TransformationController();
		//Inject Dependencies
		controller.csarService = csarService;
		controller.transformationService = transformationService;
		controller.platformService = platformService;
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	//<editor-fold desc="Property tests">
	@Test
	public void setTransformationProperties() throws Exception {
		preInitNonCreationTests();
		mvc.perform(
			put("/csars/k8s-cluster/transformations/p-a/properties")
			.contentType(MediaType.APPLICATION_JSON)
			.content(VALID_PROPERTY_INPUT)
		).andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.valid_inputs.name_property").value(true))
			.andExpect(jsonPath("$.valid_inputs.text_property").value(true))
			.andExpect(jsonPath("$.valid_inputs.secret_property").value(true))
			.andExpect(jsonPath("$.valid_inputs.unsigned_integer_property").value(true))
			.andReturn();
	}
	@Test
	public void setTransformationPropertiesInvalidInput() throws Exception {
		preInitNonCreationTests();
		mvc.perform(
			put("/csars/k8s-cluster/transformations/p-a/properties")
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
			get("/csars/k8s-cluster/transformations/p-a/properties")
		).andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().contentType("application/hal+json;charset=UTF-8"))
			.andExpect(jsonPath("$.properties").isArray())
			.andExpect(jsonPath("$.properties").isNotEmpty())
			.andExpect(jsonPath("$.properties[0].key").isString())
			.andExpect(jsonPath("$.properties[0].type").isString())
			.andExpect(jsonPath("$.links[0].rel").value("self"))
			.andExpect(jsonPath("$.links[0].href")
				.value("http://localhost/csars/k8s-cluster/transformations/p-a/properties"))
			.andReturn();
	}

	@Test
	public void getTransformationPropertiesInvalidState() throws Exception {
		preInitNonCreationTests();
		((DummyTransformation) csarService.getCsar("k8s-cluster").getTransformations().get("p-a"))
			.setState(TransformationState.QUEUED);
		mvc.perform(
			get("/csars/k8s-cluster/transformations/p-a/properties")
		).andDo(print())
			.andExpect(status().is(400))
			.andReturn();
	}
	//</editor-fold>
	//<editor-fold desc="Test Artifact Retrieval">
	@Test
	public void retrieveArtifact() throws Exception {
		preInitNonCreationTests();
		((DummyTransformation) csarService.getCsar("k8s-cluster").getTransformations().get("p-a"))
			.setReturnTargetArtifact(true);
		mvc.perform(
			get("/csars/k8s-cluster/transformations/p-a/artifact")
		).andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().contentType("application/hal+json;charset=UTF-8"))
			.andExpect(jsonPath("$.access_url").isString())
			.andExpect(jsonPath("$.links").isArray())
			.andExpect(jsonPath("$.links[0].rel").value("self"))
			.andExpect(jsonPath("$.links[0].href")
				.value("http://localhost/csars/k8s-cluster/transformations/p-a/artifact"))
			.andReturn();
	}

	@Test
	public void retrieveArtifactNotFinished() throws Exception {
		preInitNonCreationTests();
		((DummyTransformation) csarService.getCsar("k8s-cluster").getTransformations().get("p-a"))
			.setReturnTargetArtifact(false);
		mvc.perform(
			get("/csars/k8s-cluster/transformations/p-a/artifact")
		).andDo(print())
			.andExpect(status().is(404))
			.andReturn();
	}

	//</editor-fold>
	//<editor-fold desc="Test Transformation Logs">
	@Test
	public void retrieveTransformationLogs() throws Exception {
		preInitNonCreationTests();
		mvc.perform(
			get("/csars/k8s-cluster/transformations/p-a/logs?start=0")
		).andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().contentType("application/hal+json;charset=UTF-8"))
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
		((DummyTransformationService) transformationService).setReturnValue(true);
		//Execute Request
		mvc.perform(
			delete("/csars/k8s-cluster/transformations/p-a/delete")
		).andDo(print())
			.andExpect(status().is(200))
			.andReturn();
	}

	@Test
	public void deleteTransformationServerError() throws Exception {
		preInitNonCreationTests();
		//Set the return value of the delete method
		((DummyTransformationService) transformationService).setReturnValue(false);
		//Execute Request
		mvc.perform(
			delete("/csars/k8s-cluster/transformations/p-a/delete")
		).andDo(print())
			.andExpect(status().is(500))
			.andReturn();
	}

	//</editor-fold>
	//<editor-fold desc="Transformation Details Test">
	@Test
	public void transformationDetails() throws Exception {
		preInitNonCreationTests();
		MvcResult result = mvc.perform(
			get("/csars/k8s-cluster/transformations/p-a").accept("application/hal+json")
		)
			.andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().contentType("application/hal+json;charset=UTF-8"))
			.andExpect(jsonPath("$.progress").value(0))
			.andExpect(jsonPath("$.platform").value("p-a"))
			.andExpect(jsonPath("$.status").value("INPUT_REQUIRED"))
			.andReturn();
		JSONObject object = new JSONObject(result.getResponse().getContentAsString());
		HALRelationUtils.validateRelations(
			object.getJSONArray("links"),
			getLinkRelationsForTransformationDetails(),
			"k8s-cluster",
			"p-a"
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
			get("/csars/k8s-cluster/transformations/").accept("application/hal+json")
		)
			.andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().contentType("application/hal+json;charset=UTF-8"))
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0]").exists())
			.andExpect(jsonPath("$.content[1]").exists())
			.andExpect(jsonPath("$.content[2]").doesNotExist())
			.andExpect(jsonPath("$.links[0].href")
				.value("http://localhost/csars/k8s-cluster/transformations/"))
			.andExpect(jsonPath("$.links[0].rel").value("self"))
			.andExpect(jsonPath("$.links[1]").doesNotExist())
			.andReturn();
	}

	@Test
	public void listEmptyTransformations() throws Exception {
		mvc.perform(
			get("/csars/k8s-cluster/transformations/").accept("application/hal+json;charset=UTF-8")
		)
			.andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().contentType("application/hal+json;charset=UTF-8"))
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0]").doesNotExist())
			.andExpect(jsonPath("$.links[0].href")
				.value("http://localhost/csars/k8s-cluster/transformations/"))
			.andExpect(jsonPath("$.links[0].rel").value("self"))
			.andExpect(jsonPath("$.links[1]").doesNotExist())
			.andReturn();
	}

	//</editor-fold>
	//<editor-fold desc="Create Transformation Tests">
	@Test
	public void createTransformation() throws Exception {
		//Make sure no previous transformations are present
		assertTrue(csarService.getCsar("k8s-cluster").getTransformations().entrySet().size() == 0);
		//Call creation Request
		mvc.perform(put("/csars/k8s-cluster/transformations/p-a/create"))
			.andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().bytes(new byte[0]))
			.andReturn();
		//Check if the transformation has been added to the archive
		assertTrue(csarService.getCsar("k8s-cluster").getTransformations().entrySet().size() == 1);
		assertTrue(csarService.getCsar("k8s-cluster").getTransformations().get("p-a") != null);
	}

	@Test
	public void createTransformationTwice() throws Exception {
		//call the first time
		mvc.perform(put("/csars/k8s-cluster/transformations/p-a/create"))
			.andDo(print())
			.andExpect(status().is(200))
			.andExpect(content().bytes(new byte[0]))
			.andReturn();
		//Call the second time
		mvc.perform(put("/csars/k8s-cluster/transformations/p-a/create"))
			.andDo(print())
			.andExpect(status().is(400))
			.andExpect(content().bytes(new byte[0]))
			.andReturn();
	}

	//</editor-fold>
	//<editor-fold desc="Platform Not found Tests">
	@Test
	public void newTransformationPlatformNotFound() throws Exception {
		mvc.perform(put("/csars/k8s-cluster/transformations/p-z/create"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationInfoPlatformNotFound() throws Exception {
		mvc.perform(get("/csars/k8s-cluster/transformations/p-z"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationLogsPlatformNotFound() throws Exception {
		mvc.perform(get("/csars/k8s-cluster/transformations/p-z/logs"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationArtifactPlatformNotFound() throws Exception {
		mvc.perform(get("/csars/k8s-cluster/transformations/p-z/artifacts"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationPropertiesGetPlatformNotFound() throws Exception {
		mvc.perform(get("/csars/k8s-cluster/transformations/p-z/properties"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationPropertiesPutPlatformNotFound() throws Exception {
		mvc.perform(
			put("/csars/k8s-cluster/transformations/p-z/properties")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"properties\": {}}")
		).andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationDeletePlatformNotFound() throws Exception {
		mvc.perform(delete("/csars/k8s-cluster/transformations/p-z/delete"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	//</editor-fold>
	//<editor-fold desc="CSAR Not found Tests">
	@Test
	public void listTransformationsCsarNotFound() throws Exception {
		mvc.perform(get("/csars/keinechtescsar/transformations"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void newTransformationCsarNotFound() throws Exception {
		mvc.perform(put("/csars/keinechtescsar/transformations/p-a/create"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationInfoCsarNotFound() throws Exception {
		mvc.perform(get("/csars/keinechtescsar/transformations/p-a"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationLogsCsarNotFound() throws Exception {
		mvc.perform(get("/csars/keinechtescsar/transformations/p-a/logs"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationArtifactCsarNotFound() throws Exception {
		mvc.perform(get("/csars/keinechtescsar/transformations/p-a/artifacts"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationPropertiesGetCsarNotFound() throws Exception {
		mvc.perform(get("/csars/keinechtescsar/transformations/p-a/properties"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationPropertiesPutCsarNotFound() throws Exception {
		mvc.perform(
			put("/csars/keinechtescsar/transformations/p-a/properties")
				.content("{\"properties\": {}}")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationDeleteCsarNotFound() throws Exception {
		mvc.perform(delete("/csars/keinechtescsar/transformations/p-a/delete"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	//</editor-fold>
	//<editor-fold desc="Util Methods">
	public void preInitNonCreationTests() {
		//add a transformation
		Csar csar = csarService.getCsar("k8s-cluster");
		transformationService.createTransformation(csar, platformService.findById("p-a"));
		transformationService.createTransformation(csar, platformService.findById("p-b"));
	}
	//</editor-fold>
}
