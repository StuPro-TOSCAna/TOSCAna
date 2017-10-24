package org.opentosca.toscana.core.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opentosca.toscana.core.util.FileUtils;
import org.opentosca.toscana.core.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(
    classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
)
public class ArtifactControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private MockMvc mvc;

    private Preferences preferences;

    private ArtifactController controller;

    private File testdir = new File("test-temp");

    private byte[][] hashes = new byte[25][];

    private MessageDigest digest;

    @Before
    public void setUp() throws Exception {
        //Cleanup
        FileUtils.delete(testdir);
        //Recreation
        testdir.mkdirs();
        //misc init
        Random rnd = new Random(1245);
        digest = MessageDigest.getInstance("SHA-256");
        for (int i = 0; i < 25; i++) {
            File dummy = new File(testdir, "test-" + i + ".bin");
            log.info("Creating dummy file {}", dummy.getAbsolutePath());

            //Generating "Random" data
            byte[] data = new byte[1024 * 1024 * 20];
            rnd.nextBytes(data);

            //Getting sha hash
            hashes[i] = digest.digest(data);

            //Writing data to disk
            FileOutputStream out = new FileOutputStream(dummy);
            out.write(data);
            out.flush();
            out.close();
        }

        //Mocking preferences
        preferences = Mockito.mock(Preferences.class);
        when(preferences.getArtifactDir()).thenReturn(testdir);

        //initalizing controller
        this.controller = new ArtifactController(preferences);
        this.controller.enableArtifactList = true;

        //Building MockMvc
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void downloadMissingFile() throws Exception {
        mvc.perform(get("/artifacts/test-1337.bin"))
            .andDo(print())
            .andExpect(status().is(404))
            .andReturn();
    }

    @Test
    public void downloadValidFiles() throws Exception {
        for (int i = 0; i < hashes.length; i++) {
            log.info("Downloading file {}/25", i);
            MvcResult result = mvc.perform(get("/artifacts/test-" + i + ".bin"))
//                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
            assertEquals("application/octet-stream", result.getResponse().getContentType());
            assertEquals(1024 * 1024 * 20, result.getResponse().getContentLength());
            assertArrayEquals(hashes[i], digest.digest(result.getResponse().getContentAsByteArray()));
        }
    }

    @Test
    public void listFiles() throws Exception {
        MvcResult result = mvc.perform(get("/artifacts"))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();
        //validate body contents
        JSONObject data = new JSONObject(result.getResponse().getContentAsString());
        JSONArray content = data.getJSONArray("content");
        assertTrue(content.length() == 25);
        boolean[] found = new boolean[25];
        for (int i = 0; i < content.length(); i++) {
            JSONObject obj = content.getJSONObject(i);
            log.info("Validating {}", obj.toString());
            String val = obj.getString("filename")
                .replace(".bin", "")
                .replace("test-", "");
            found[Integer.parseInt(val)] = true;
            assertTrue(obj.getInt("length") == (1024 * 1024 * 20));
            String ref = obj.getJSONArray("links").getJSONObject(0).getString("href");
            assertEquals("http://localhost/artifacts/test-" + val + ".bin", ref);
        }
        for (boolean b : found) {
            assertTrue(b);
        }
    }

    @Test
    public void listFilesDisabled() throws Exception {
        this.controller.enableArtifactList = false;
        mvc.perform(get("/artifacts")).andDo(print()).andExpect(status().is(403)).andReturn();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.delete(testdir);
    }
}
