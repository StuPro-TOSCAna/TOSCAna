package org.opentosca.toscana.retrofit.api;

import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.retrofit.ToscanaApi;
import org.opentosca.toscana.retrofit.util.LoggingMode;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("WeakerAccess")
public abstract class BaseToscanaApiTest {

    public static final String MIME_TYPE_JSON = "application/json";
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected ToscanaApi api;
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        logger.info("Starting Mock Webserver");
        server = new MockWebServer();
        server.start();

        String baseURL = server.url("").toString();
        logger.info("Server Running on {}", baseURL);

        api = new ToscanaApi(baseURL, LoggingMode.HIGH);
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
        enqueResponse("json/regular_error.json", code, MIME_TYPE_JSON);
    }
    
    @After
    public void tearDown() throws Exception {
        logger.info("Stopping server");
        server.shutdown();
    }
}
