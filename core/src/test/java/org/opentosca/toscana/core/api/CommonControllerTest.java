package org.opentosca.toscana.core.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.StatusProvider;
import org.opentosca.toscana.core.util.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CommonController.class, secure = false)
@DirtiesContext(
	classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
public class CommonControllerTest {

	private static final Logger log = LoggerFactory.getLogger(CommonControllerTest.class);
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private FileSystem fileSystem;
	
	@MockBean
	private StatusProvider status;

	@Before
	public void setUp() throws Exception {
		when(status.getSystemStatus()).thenReturn(SystemStatus.IDLE);
		when(fileSystem.getAvailableSpace()).thenReturn(100L);
		when(fileSystem.getUsedSpace()).thenReturn(900L);
	}

	@Test
	public void retrieveStatusUpdate() throws Exception {
		mvc.perform(
			get("/status" ).accept(MediaType.APPLICATION_JSON)
		).andDo(print())
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.status").value("idle"))
			.andExpect(jsonPath("$.total_storage").value("1000"))
			.andExpect(jsonPath("$.available_storage").value("100"))
			.andExpect(jsonPath("$._links.self.href").isString())
			.andReturn();
		
	}
}
