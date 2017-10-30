package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.BaseTest;
import org.opentosca.toscana.core.testutils.CategoryAwareSpringRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(CategoryAwareSpringRunner.class)
@WebMvcTest(value = CommonController.class, secure = false)
@DirtiesContext(
    classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
@ActiveProfiles("controller_test")
public class CommonControllerTest extends BaseTest {

    @Autowired
    private MockMvc mvc;

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
            .andExpect(jsonPath("$._links.csars.href").value("http://localhost/api/csars/"));
    }
}
