package org.opentosca.toscana.api;

import org.opentosca.toscana.core.BaseTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.opentosca.toscana.core.testdata.TestProfiles.INTEGRATION_TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CommonController.class, secure = false)
@DirtiesContext(
    classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
@ActiveProfiles({INTEGRATION_TEST_PROFILE, "base-image-mapper"})
@ContextConfiguration(classes = CommonController.class)
public class CommonControllerTest extends BaseTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getStatusIndexPage() throws Exception {
        mvc.perform(get("/api/status"))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/status/"))
            .andExpect(jsonPath("$._links.metrics.href").value("http://localhost/api/status/metrics"))
            .andExpect(jsonPath("$._links.health.href").value("http://localhost/api/status/health"));
    }

    @Test
    public void getIndexPage() throws Exception {
        String url = "/api/";
        requestIndex(url);
    }

    @Test
    public void getIndexPageNoSlash() throws Exception {
        String url = "/api";
        requestIndex(url);
    }

    private void requestIndex(String url) throws Exception {
        mvc.perform(get(url))
            .andDo(print())
            .andExpect(status().is(200))
            .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/"))
            .andExpect(jsonPath("$._links.platforms.href").value("http://localhost/api/platforms/"))
            .andExpect(jsonPath("$._links.csars.href").value("http://localhost/api/csars/"))
            .andExpect(jsonPath("$._links.status.href").value("http://localhost/api/status/"));
    }
}
