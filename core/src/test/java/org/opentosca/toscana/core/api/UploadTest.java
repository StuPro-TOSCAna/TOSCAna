package org.opentosca.toscana.core.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.opentosca.toscana.core.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class UploadTest {

    public static final String TEMPLATE_HASH
        = "d381ef5524ddbe0306a3e5034bed5ecaae66ac04134c474b7ac14d7e06a714cb";

    private File tempDir;
    private Thread springThread;

    @Before
    public void setUp() throws Exception {
        tempDir = new File("test-temp");
        delete(tempDir);
        System.out.println(tempDir.getAbsolutePath());
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IOException();
        }
        springThread = new Thread(() -> {
            Main.main(new String[]{"--datadir=" + tempDir.getAbsolutePath()});
        });
        springThread.start();
    }

    @Test
    public void testFileUpload() throws Exception {
        waitForServerToStart(HttpClients.createMinimal());
        System.err.println("Server started!");
        InputStream in =
            getClass().getClassLoader().getResourceAsStream("csars/yaml/valid/entrypoint_is_yml.csar");
        assertTrue(in != null);
        HttpPost post = new HttpPost("http://localhost:8080/csars/simple-task");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", in);
        post.setEntity(builder.build());
        System.err.println("Uploading");
        HttpResponse res = HttpClients.createMinimal().execute(post);
        System.err.println("Upload done");
        System.err.println("Server Responded " + res.getStatusLine().getStatusCode());
        File testFile = new File(tempDir, "simple-task/content/template.yml");
        System.err.println("Checking if " + testFile.getAbsolutePath() + " Exists");
        //assertTrue(testFile.exists() && testFile.isFile());
    }

    private void waitForServerToStart(HttpClient client) throws InterruptedException {
        long code = -1;
        while (code != 200) {
            System.err.println("Waiting for Server to start...");
            try {
                HttpGet get = new HttpGet("http://127.0.0.1:8080/status");
                HttpResponse res = client.execute(get);
                code = res.getStatusLine().getStatusCode();
            } catch (IOException e) {
                System.err.println("Server not ready!");
                code = -1;
            }
            Thread.sleep(1000);
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
