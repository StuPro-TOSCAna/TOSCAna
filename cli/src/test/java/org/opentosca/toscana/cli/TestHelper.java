package org.opentosca.toscana.cli;

import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.retrofit.util.LoggingMode;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

abstract class TestHelper {

    private static final String MIME_TYPE_JSON = "application/json";
    protected final String CSAR_JSON = "responses/csarInfo.json";
    protected final String CSARS_JSON = "responses/csarsList.json";
    protected final String PLATFORM = "p-a";
    protected final String CSAR = "kubernetes-cluster";
    protected final String PLATFORM_JSON = "responses/platformInfo.json";
    protected final String PLATFORMS_JSON = "responses/platformsList.json";
    protected final String TRANSFORMATION_JSON = "responses/transformationInfo.json";
    protected final String TRANSFORMATIONS_JSON = "responses/transformationsList.json";
    protected final String TRANSFORMATION_LOGS_JSON = "responses/transformationLogs.json";
    protected final String TRANSFORMATION_PROPERTIES_JSON = "responses/transformationProperties.json";
    protected final String LOGS_RESPONSE = "Hallo Welt";
    protected final String STATUS_HEALTH_JSON = "responses/statusHealth.json";
    protected final String[] CLI_HELP = {"help"};
    protected final String[] CLI_HELP_STATUS = {"help", "status"};
    protected final String[] CLI_CSAR_LIST = {"csar", "list"};
    protected final String[] CLI_STATUS = {"status"};
    protected final String[] CSAR_AR = {"csar", "-v"};
    protected final String[] CSAR_UPLOAD = {"csar", "upload", "-f", "arch.csar"};
    protected final String[] CSAR_LIST = {"csar", "list"};
    protected final String[] PLATFORM_AR = {"platform"};
    protected final String[] PLATFORM_LIST = {"platform", "list"};
    protected final String[] PLATFORM_INFO = {"platform", "info", "-p", PLATFORM};
    protected final String[] TRANSFORMATION_AR = {"transformation"};
    protected final String[] INPUT_NOINPUT = {"input"};
    protected final String[] CSAR_DELETE = {"csar", "delete", "-c", CSAR, "-m"};
    protected final String[] CSAR_INFO = {"csar", "info", "-c", CSAR};
    protected final String[] TRANSFORMATION_DELETE = {"transformation", "delete", "-c", CSAR, "-p", PLATFORM, "-m"};
    protected final String[] TRANSFORMATION_DOWNLOAD = {"transformation", "download", "-c", CSAR, "-p", PLATFORM};
    protected final String[] TRANSFORMATION_INFO = {"transformation", "info", "-c", CSAR, "-p", PLATFORM};
    protected final String[] TRANSFORMATION_LIST = {"transformation", "list", "-c", CSAR};
    protected final String[] TRANSFORMATION_LOGS = {"transformation", "logs", "-c", CSAR, "-p", PLATFORM, "-v"};
    protected final String[] TRANSFORMATION_START = {"transformation", "start", "-c", CSAR, "-p", PLATFORM};
    protected final String[] TRANSFORMATION_STOP = {"transformation", "stop", "-c", CSAR, "-p", PLATFORM};
    protected final String[] INPUT_LIST = {"input", "-c", CSAR, "-p", PLATFORM};
    protected final String[] INPUT_MANUAL_VALID = {"input", "-c", CSAR, "-p", PLATFORM, "test=test"};
    protected final String[] INPUT_MANUAL_NOT_VALID = {"input", "-c", CSAR, "-p", PLATFORM, "test==test"};
    protected final String[] INPUT_MANUAL_ERROR = {"input", "-c", CSAR, "-p", PLATFORM, "test="};
    protected final String[] INPUT_FILE_VALID = {"input", "-c", CSAR, "-p", PLATFORM, "-f", "src/test/resources/responses/test.txt"};
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected ApiController api;
    private MockWebServer server;
    private CommandLine cmd = null;

    @Before
    public void setUp() throws IOException {
        logger.info("Starting Mock Webserver");
        server = new MockWebServer();
        server.start();
        String baseURL = server.url("").toString();
        logger.info("Server Running on {}", baseURL);
        api = new ApiController(baseURL, LoggingMode.HIGH);
    }

    @After
    public void tearDown() throws IOException {
        logger.info("Stopping server");
        server.shutdown();
    }

    private void enqueResponse(String resourcePath, int code, String mimeType) throws IOException {
        logger.info("Loading Resource from Path {}", resourcePath);
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        Buffer buffer = new Buffer();
        buffer.readFrom(in);
        logger.info("Mocking Response");
        MockResponse response = new MockResponse()
            .setResponseCode(code)
            .setBody(buffer).
                addHeader("Content-Type", mimeType);
        server.enqueue(response);
    }

    protected void enqueError(int code) throws IOException {
        enqueResponse("responses/regularError.json", code, MIME_TYPE_JSON);
    }

    protected void apiSingleInput(String resource, int code) throws IOException {
        logger.info("Creating Response with CSAR: {} and Code: {}", CSAR, code);
        enqueResponse(resource, code, MIME_TYPE_JSON);
    }

    protected void apiDoubleInput(String csar, String plat, String resource, int code) throws IOException {
        logger.info("Creating Response with CSAR: {}, Platform: {}, Resource: {} and Code: {}", csar, plat, resource, code);
        enqueResponse(resource, code, MIME_TYPE_JSON);
    }
/*
    void apiInputError(String[] cliInput, int code) throws IOException {
        enqueError(code);
        List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, cliInput);
    } */
}
