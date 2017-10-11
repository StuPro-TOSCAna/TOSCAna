package org.opentosca.toscana.core.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.toscana.core.api.dummy.DummyCsarService;
import org.opentosca.toscana.core.api.dummy.DummyPlatformProvider;
import org.opentosca.toscana.core.api.dummy.DummyTransformationService;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(
	classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
public class TransformationControllerTest {
	private TransformationController controller;
	private CsarService csarService;
	private TransformationService transformationService;
	private PlatformProvider platformProvider;
	private MockMvc mvc;

	@Before
	public void setUp() throws Exception {
		//Create Objects
		csarService = new DummyCsarService();
		transformationService = new DummyTransformationService();
		platformProvider = new DummyPlatformProvider();
		controller = new TransformationController();
		//Inject Dependencies
		controller.csarService = csarService;
		controller.transformationService = transformationService;
		controller.platformProvider = platformProvider;
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void listTransformations() throws Exception {
		preInitNonCreationTests();
		mvc.perform(get("/csars/k8s-cluster/transformations/"))
			.andDo(print())
//			.andExpect(status().is(200))
			.andReturn();
	}

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
		mvc.perform(put("/csars/k8s-cluster/transformations/p-z/properties"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
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
		mvc.perform(put("/csars/keinechtescsar/transformations/p-a/properties"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void transformationDeleteCsarNotFound() throws Exception {
		mvc.perform(delete("/csars/keinechtescsar/transformations/p-a/delete"))
			.andDo(print()).andExpect(status().isNotFound()).andReturn();
	}
	//</editor-fold>
	public void preInitNonCreationTests() {
		//add a transformation
		Csar csar = csarService.getCsar("k8s-cluster");
		transformationService.createTransformation(csar, platformProvider.findById("p-a"));
		transformationService.createTransformation(csar, platformProvider.findById("p-b"));
	}
}
