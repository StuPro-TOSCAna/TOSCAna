package org.opentosca.toscana.api;

import java.util.Optional;

import org.opentosca.toscana.core.BaseTest;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORMS;
import static org.opentosca.toscana.core.testdata.TestProfiles.INTEGRATION_TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PlatformController.class)
@ActiveProfiles({INTEGRATION_TEST_PROFILE, "base-image-mapper"})
@DirtiesContext(
    classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
@ContextConfiguration(classes = PlatformController.class)
public class PlatformControllerTest extends BaseTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PluginService prov;

    @Before
    public void setUp() {
        Mockito.when(prov.getSupportedPlatforms()).thenReturn(PLATFORMS);
        Mockito.when(prov.findPlatformById(Mockito.anyString())).thenReturn(Optional.empty());
        PLATFORMS.forEach(e -> Mockito.when(prov.findPlatformById(e.id)).thenReturn(Optional.of(e)));
    }

    @Test
    public void listPlatforms() throws Exception {
        ResultActions resultActions = mvc.perform(
            get("/api/platforms")
        ).andDo(print()).andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType("application/hal+json;charset=UTF-8"));
        resultActions.andExpect(jsonPath("$._embedded.platform").isArray());
        resultActions.andExpect(jsonPath("$._embedded.platform[" + PLATFORMS.size() + "]").doesNotExist());
        for (int i = 0; i < PLATFORMS.size(); i++) {
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
