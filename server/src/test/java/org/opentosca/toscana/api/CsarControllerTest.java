package org.opentosca.toscana.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.opentosca.toscana.api.utils.HALRelationUtils;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.TransformationState;

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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.testdata.ByteArrayUtils.assertHashesEqual;
import static org.opentosca.toscana.core.testdata.ByteArrayUtils.generateRandomByteArray;
import static org.opentosca.toscana.core.testdata.ByteArrayUtils.getSHA256Hash;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CsarControllerTest extends BaseSpringTest {

    private static final String LIST_CSARS_URL = "/api/csars";
    private static final String[] MOCK_CSAR_NAMES = {"windows-server", "apache"};
    private static final Map<String, String> relations = new HashMap<>();
    private static final String ACCEPTED_MIME_TYPE = "application/hal+json";
    private static final String CSAR_BASE_URL = LIST_CSARS_URL + "/";
    private static final String MULTIPART_FILE_UPLOAD_KEY = "file";
    private static final String MULTIPART_FILE_ORIGINAL_FILENAME = "null";
    private static final String INVALID_CSAR_NAME = "not-a-csar";
    private static final String INVALID_CSAR_URL = CSAR_BASE_URL + INVALID_CSAR_NAME;
    private static final String VALID_CSAR_NAME = "apache";
    private static final String DELETE_END_PATH = "/delete";
    private static final String DELETE_VALID_CSAR_URL = CSAR_BASE_URL + VALID_CSAR_NAME + DELETE_END_PATH;
    private static final String LIST_CSARS_SELF_URL = "http://localhost/api/csars/";

    static {
        relations.put("self", "http://localhost/api/csars/%s");
        relations.put("transformations", "http://localhost/api/csars/%s/transformations/");
        relations.put("delete", "http://localhost/api/csars/%s/delete/");
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
            Csar csar = spy(new CsarImpl(new File(""), name, logMock()));
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
                return new CsarImpl(new File(""), iom.getArguments()[0].toString(), logMock());
            });

        CsarController controller = new CsarController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void listCsars() throws Exception {
        ResultActions resultActions = mvc.perform(
            get(LIST_CSARS_URL).accept(ACCEPTED_MIME_TYPE)
        ).andDo(print()).andExpect(status().is2xxSuccessful());
        resultActions.andExpect(jsonPath("$.links[0].rel").value("self"));
        resultActions.andExpect(jsonPath("$.links[0].href").value(LIST_CSARS_SELF_URL));
        resultActions.andExpect(jsonPath("$.content").exists());
        resultActions.andExpect(jsonPath("$.content").isArray());
        resultActions.andExpect(jsonPath("$.content[2]").doesNotExist());
        resultActions.andReturn();
    }

    @Test
    public void uploadTest() throws Exception {
        byte[] data = generateRandomByteArray(rnd, 10 * 1024);
        byte[] hash = getSHA256Hash(data);

        String path = CSAR_BASE_URL + "rnd";

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

        String path = CSAR_BASE_URL + "apache";

        MockMultipartHttpServletRequestBuilder builder = buildMockedMultipartUploadRequest(data, path);

        ResultActions resultActions = mvc.perform(
            builder
        ).andDo(print()).andExpect(status().is(201));
        resultActions.andReturn();
    }

    @Test
    public void csarDetails() throws Exception {
        for (String name : MOCK_CSAR_NAMES) {
            ResultActions resultActions = mvc.perform(
                get(CSAR_BASE_URL + name).accept(ACCEPTED_MIME_TYPE)
            ).andDo(print()).andExpect(status().is2xxSuccessful());
            resultActions.andExpect(jsonPath("$.name").value(name));
            resultActions.andExpect(jsonPath("$.links").isArray());
            resultActions.andExpect(jsonPath("$.links[" + relations.size() + "]").doesNotExist());

            //Validate String result
            MvcResult result = resultActions.andReturn();
            JSONObject object = new JSONObject(result.getResponse().getContentAsString());
            HALRelationUtils.validateRelations(object.getJSONArray("links"), relations, name);
        }
    }

    @Test
    public void testDelete() throws Exception {
        //Mechanism to set this value to true once delete has been called
        final boolean[] executed = new boolean[]{false};
        doAnswer(iom -> executed[0] = true).when(service).deleteCsar(any(Csar.class));
        //Perform request
        mvc.perform(
            delete(DELETE_VALID_CSAR_URL).accept(ACCEPTED_MIME_TYPE)
        ).andDo(print())
            .andExpect(status().is(200))
            .andExpect(content().bytes(new byte[0]));
        //Check execution
        assertTrue("csarService.delete() did not get called!", executed[0]);
    }

    @Test
    public void testDeleteCsarBusy() throws Exception {
        //Add mock transformation to csar
        Csar csar = service.getCsar(VALID_CSAR_NAME).get();
        Transformation transformation = new TransformationImpl(csar, PLATFORM1, logMock(), modelMock());
        transformation.setState(TransformationState.TRANSFORMING);
        csar.getTransformations().put(PLATFORM1.id, transformation);
        //Perform request
        mvc.perform(
            delete(DELETE_VALID_CSAR_URL)
        ).andDo(print())
            .andExpect(status().is(400));
    }

    @Test
    public void csarDetails404() throws Exception {
        mvc.perform(
            get(INVALID_CSAR_URL).accept(ACCEPTED_MIME_TYPE)
        ).andDo(print()).andExpect(status().is(404));
    }

    @Test
    public void deleteCsar404() throws Exception {
        mvc.perform(
            delete(INVALID_CSAR_URL + DELETE_END_PATH).accept(ACCEPTED_MIME_TYPE)
        ).andDo(print()).andExpect(status().is(404));
    }

    public MockMultipartHttpServletRequestBuilder buildMockedMultipartUploadRequest(byte[] data, String path) {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            MULTIPART_FILE_UPLOAD_KEY,
            MULTIPART_FILE_ORIGINAL_FILENAME,
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
