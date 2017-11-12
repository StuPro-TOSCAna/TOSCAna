package org.opentosca.toscana.retrofit.api;

import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.retrofit.util.LoggingMode;
import org.opentosca.toscana.retrofit.TOSCAnaAPI;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTOSCAnaAPITest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected MockWebServer server;

    protected TOSCAnaAPI api;

    @Before
    public void setUp() throws Exception {
        logger.info("Starting Mock Webserver");
        server = new MockWebServer();
        server.start();

        String baseURL = server.url("").toString();
        logger.info("Server Running on {}", baseURL);

        api = new TOSCAnaAPI(baseURL, LoggingMode.HIGH);
    }

    protected void enqueResponse(String resourcePath, int code, String mimeType) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        Buffer buffer = new Buffer();
        buffer.readFrom(in);

        MockResponse response = new MockResponse()
            .setResponseCode(code)
            .setBody(buffer).
                addHeader("Content-Type", mimeType);
        server.enqueue(response);
    }

    protected void enqueError(int code) throws IOException {
        enqueResponse("json/regular_error.json", code, "application/json");
    }

    protected void enqueResponse(int code) {
        MockResponse response = new MockResponse()
            .setResponseCode(code);
        server.enqueue(response);
    }
    
    @After
    public void tearDown() throws Exception {
        logger.info("Stopping server");
        server.shutdown();
    }
}
