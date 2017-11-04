package org.opentosca.toscana.core.integration.upload;

import org.opentosca.toscana.core.integration.BaseIntegrationTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testutils.TestCategories;
import org.opentosca.toscana.core.testutils.TestCategory;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.junit.Assert.fail;

public class UploadTest extends BaseIntegrationTest {

    private TOSCAnaUploadInterface api;

    @Before
    public void setUp() throws Exception {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getHttpUrl()).build();

        api = retrofit.create(TOSCAnaUploadInterface.class);
    }

    @TestCategory(TestCategories.FAST)
    @Test(timeout = 30000)
    public void testFileUpload() throws Exception {
        System.err.println("Server started!");

        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"),
            TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);

        MultipartBody.Part p = MultipartBody.Part.createFormData("file", "test.csar", file);

        Response<ResponseBody> response = api.upload(p, "test-archive").execute();
        if (response.code() != 200) {
            ResponseBody b = response.errorBody();
            System.out.println(b.string());
            fail();
        }
    }

    @TestCategory(TestCategories.FAST)
    @Test(timeout = 30000)
    public void testFileUploadFail() throws Exception {
        System.err.println("Server started");

        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"),
            TestCsars.CSAR_YAML_INVALID_ENTRYPOINT_AMBIGUOUS);

        MultipartBody.Part p = MultipartBody.Part.createFormData("file", "test.csar", file);

        Response<ResponseBody> response = api.upload(p, "test-archive-fail").execute();
        if (response.code() != 200) {
            ResponseBody b = response.errorBody();
            System.out.println(b.string());
        } else fail();
    }
}
