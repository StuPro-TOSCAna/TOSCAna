package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.BaseTest;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.testutils.CategoryAwareSpringRunner;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(CategoryAwareSpringRunner.class)
@WebMvcTest(value = PlatformController.class)
@ActiveProfiles({"controller_test"})
@DirtiesContext(
    classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
public class PlatformControllerTest extends BaseTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PluginService prov;

    @Test
    public void listPlatforms() throws Exception {
        ResultActions resultActions = mvc.perform(
            get("/api/platforms")
        ).andDo(print()).andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType("application/hal+json;charset=UTF-8"));
        resultActions.andExpect(jsonPath("$._embedded.platform").isArray());
        resultActions.andExpect(jsonPath("$._embedded.platform[3]").doesNotExist());
        for (int i = 0; i < 3; i++) {
            resultActions.andExpect(jsonPath("$._embedded.platform[" + i + "].id").isString());
            resultActions.andExpect(jsonPath("$._embedded.platform[" + i + "].name").isString());
        }
        resultActions.andReturn();
    }

    @Test
    public void platformDetails() throws Exception {
        for (Platform platform : prov.getSupportedPlatforms()) {
            ResultActions resultActions = mvc.perform(
                get("/api/platforms/" + platform.id)
            ).andDo(print());
            resultActions.andExpect(jsonPath("$.id").value(platform.id));
            resultActions.andExpect(jsonPath("$.name").value(platform.name));
            resultActions.andExpect(jsonPath("$._links.self.href").isString());
            resultActions.andReturn();
        }
    }

    @Test
    public void platformDetails404() throws Exception {
        ResultActions resultActions = mvc.perform(
            get("/api/platforms/not-a-platform")
        ).andDo(print());
        resultActions.andExpect(status().isNotFound());
        resultActions.andReturn();
    }
}
