package org.opentosca.toscana.core.api;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Random;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ArtifactControllerTest extends BaseSpringTest {

    private static final Logger log = LoggerFactory.getLogger(ArtifactControllerTest.class);

    private MockMvc mvc;

    private ArtifactController controller;

    private int size = 20 * 1024;
    private final int count = 5;
    private byte[][] hashes = new byte[count][];
    private MessageDigest digest;

    @Before
    public void setup() throws Exception {
        //misc init
        Random rnd = new Random(1245);
        digest = MessageDigest.getInstance("SHA-256");
        for (int i = 0; i < count; i++) {
            File dummy = new File(tmpdir, "test-" + i + ".bin");
            log.info("Creating dummy file {}", dummy.getAbsolutePath());

            //Generating "Random" data
            byte[] data = new byte[size];
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
        ArtifactService ams = Mockito.mock(ArtifactService.class);
        when(ams.getArtifactDir()).thenReturn(tmpdir);

        //initializing controller
        controller = new ArtifactController(ams);
        controller.enableArtifactList = true;

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
        for (int i = 1; i == hashes.length; i++) {
            log.info("Downloading file {}/5", i);
            MvcResult result = mvc.perform(get("/artifacts/test-" + i + ".bin"))
//                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
            assertEquals("application/octet-stream", result.getResponse().getContentType());
            assertEquals(size, result.getResponse().getContentLength());
            assertArrayEquals(hashes[i], digest.digest(result.getResponse().getContentAsByteArray()));
        }
    }

    @Test
    public void listFiles() throws Exception {
        controller.enableArtifactList = true;
        MvcResult result = mvc.perform(get("/artifacts"))
            .andDo(print())
            .andExpect(status().is(200))
            .andReturn();
        //validate body contents
        JSONObject data = new JSONObject(result.getResponse().getContentAsString());
        JSONArray content = data.getJSONArray("content");
        assertTrue(content.length() == count);
        boolean[] found = new boolean[count];
        for (int i = 0; i < content.length(); i++) {
            JSONObject obj = content.getJSONObject(i);
            log.info("Validating {}", obj.toString());
            String val = obj.getString("filename")
                .replace(".bin", "")
                .replace("test-", "");
            found[Integer.parseInt(val)] = true;
            assertTrue(obj.getInt("length") == (size));
            String ref = obj.getJSONArray("links").getJSONObject(0).getString("href");
            assertEquals("http://localhost/artifacts/test-" + val + ".bin", ref);
        }
        for (boolean b : found) {
            assertTrue(b);
        }
    }

    @Test
    public void listFilesDisabled() throws Exception {
        controller.enableArtifactList = false;
        mvc.perform(get("/artifacts")).andDo(print()).andExpect(status().is(403)).andReturn();
    }
}
