package org.opentosca.toscana.core.api.upload;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.opentosca.toscana.core.Main;
import org.opentosca.toscana.core.testdata.TestCsars;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class UploadTest {

    public static final String TEMPLATE_HASH
        = "d381ef5524ddbe0306a3e5034bed5ecaae66ac04134c474b7ac14d7e06a714cb";

    private File tempDir;
    private Thread springThread;

    private Retrofit retrofit;
    private TOSCAnaUploadInterface api;

    @Before
    public void setUp() throws Exception {
        tempDir = new File("test-temp");
        delete(tempDir);
        System.out.println(tempDir.getAbsolutePath());
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IOException();
        }
        retrofit = new Retrofit.Builder().baseUrl("http://127.0.0.1:8080/").build();

        api = retrofit.create(TOSCAnaUploadInterface.class);

        springThread = new Thread(() -> {
            Main.main(new String[]{"--datadir=" + tempDir.getAbsolutePath()});
        });
        springThread.start();
    }

    @Test(timeout = 10000)
    public void testFileUpload() throws Exception {
        waitForServerToStart();
        System.err.println("Server started!");
        
        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"),
            TestCsars.CSAR_YAML_VALID_SIMPLETASK);

        MultipartBody.Part p = MultipartBody.Part.createFormData("file","test.csar",file);
        
        Response<ResponseBody> response = api.upload(p, "test-archive").execute();
        //assertTrue(response.code() == 200);
        if(response.code() != 200) {
            ResponseBody b = response.errorBody();
            System.out.println(b.string());
            fail();
        }
    }

    private void waitForServerToStart() throws Exception {
        int code = -1;
        while (code != 200) {
            try {
                code = api.getStatus().execute().code();
                Thread.sleep(100);
            } catch (IOException e) {
                code = -1;
            }

        }
    }

    @After
    public void tearDown() throws Exception {
        delete(tempDir);
        springThread.stop();
    }

    public static void delete(File f) {
        if (f.isFile()) {
            f.delete();
        } else if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                delete(file);
            }
            f.delete();
        }
    }
}
