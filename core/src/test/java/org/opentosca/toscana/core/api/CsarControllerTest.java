package org.opentosca.toscana.core.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.api.utils.HALRelationUtils.validateRelations;
import static org.opentosca.toscana.core.testdata.ByteArrayUtils.assertHashesEqual;
import static org.opentosca.toscana.core.testdata.ByteArrayUtils.generateRandomByteArray;
import static org.opentosca.toscana.core.testdata.ByteArrayUtils.getSHA256Hash;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CsarControllerTest extends BaseSpringTest {

    private static final String[] MOCK_CSAR_NAMES = {"windows-server", "apache"};
    private static final Map<String, String> relations = new HashMap<>();

    static {
        relations.put("self", "http://localhost/api/csars/%s");
        relations.put("transformations", "http://localhost/api/csars/%s/transformations/");
    }

    private CsarService service;

    private Random rnd = new Random(123456789);

    private byte[] dataRead;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        List<Csar> mockedCsars = new ArrayList<>();

        service = mock(CsarService.class);
        when(service.getCsars()).thenReturn(mockedCsars);
        when(service.getCsar(anyString())).thenReturn(Optional.empty());
        for (String name : MOCK_CSAR_NAMES) {
            Csar csar = new CsarImpl(name, mock(Log.class));
            when(service.getCsar(name)).thenReturn(Optional.of(csar));
            mockedCsars.add(csar);
        }

        when(service.submitCsar(anyString(), any(InputStream.class)))
            .thenAnswer(iom -> {
                //Check if the csar is already known
                if (service.getCsar(iom.getArguments()[0].toString()).isPresent()) {
                    return null;
                }

                //Copy "sent" data into a byte array
                InputStream in = (InputStream) iom.getArguments()[1];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                IOUtils.copy(in, out);
                dataRead = out.toByteArray();

                //Create Csar Mock
                return (Csar) new CsarImpl(iom.getArguments()[0].toString(), mock(Log.class));
            });

        CsarController controller = new CsarController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void listCsars() throws Exception {
        ResultActions resultActions = mvc.perform(
            get("/api/csars").accept("application/hal+json")
        ).andDo(print()).andExpect(status().is2xxSuccessful());
        resultActions.andExpect(jsonPath("$.links[0].rel").value("self"));
        resultActions.andExpect(jsonPath("$.links[0].href").value("http://localhost/api/csars/"));
        resultActions.andExpect(jsonPath("$.content").exists());
        resultActions.andExpect(jsonPath("$.content").isArray());
        resultActions.andExpect(jsonPath("$.content[2]").doesNotExist());
        resultActions.andReturn();
    }

    @Test
    public void uploadTest() throws Exception {
        byte[] data = generateRandomByteArray(rnd, 10 * 1024);
        byte[] hash = getSHA256Hash(data);

        String path = "/api/csars/rnd";

        MockMultipartHttpServletRequestBuilder builder = buildMockedMultipartUploadRequest(data, path);

        ResultActions resultActions = mvc.perform(
            builder
        ).andDo(print()).andExpect(status().is2xxSuccessful());
        resultActions.andReturn();

        assertNotNull(dataRead);
        assertEquals(data.length, dataRead.length);

        byte[] hashUpload = getSHA256Hash(this.dataRead);
        assertHashesEqual(hash, hashUpload);
    }

    @Test
    public void uploadTestArchiveAlreadyExists() throws Exception {
        //Generate 10 KiB of random data
        byte[] data = generateRandomByteArray(rnd, 10);

        String path = "/api/csars/apache";

        MockMultipartHttpServletRequestBuilder builder = buildMockedMultipartUploadRequest(data, path);

        ResultActions resultActions = mvc.perform(
            builder
        ).andDo(print()).andExpect(status().is(200));
        resultActions.andReturn();
    }

    @Test
    public void csarDetails() throws Exception {
        for (String name : MOCK_CSAR_NAMES) {
            ResultActions resultActions = mvc.perform(
                get("/api/csars/" + name).accept("application/hal+json")
            ).andDo(print()).andExpect(status().is2xxSuccessful());
            resultActions.andExpect(jsonPath("$.name").value(name));
            resultActions.andExpect(jsonPath("$.links").isArray());
            resultActions.andExpect(jsonPath("$.links[" + relations.size() + "]").doesNotExist());

            //Validate String result
            MvcResult result = resultActions.andReturn();
            JSONObject object = new JSONObject(result.getResponse().getContentAsString());
            validateRelations(object.getJSONArray("links"), relations, name);
        }
    }

    @Test
    public void csarDetails404() throws Exception {
        mvc.perform(
            get("/api/csars/not-a-csar").accept("application/hal+json")
        ).andDo(print()).andExpect(status().is(404));
    }

    public MockMultipartHttpServletRequestBuilder buildMockedMultipartUploadRequest(byte[] data, String path) {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "file",
            "null",
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            data
        );

        MockMultipartHttpServletRequestBuilder builder = fileUpload(path);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        builder = (MockMultipartHttpServletRequestBuilder) builder.file(mockMultipartFile)
            .contentType(MediaType.MULTIPART_FORM_DATA);
        return builder;
    }
}
