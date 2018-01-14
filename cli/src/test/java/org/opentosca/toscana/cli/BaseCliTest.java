package org.opentosca.toscana.cli;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public abstract class BaseCliTest {
    @ClassRule
    public static final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

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
    protected final String TRANSFORMATION_START_JSON = "responses/transformationStart.json";
    protected final String LOGS_RESPONSE = "Hallo Welt";
    protected final String STATUS_HEALTH_JSON = "responses/statusHealth.json";
    protected final String[] CLI_HELP = {"help"};
    protected final String[] CLI_HELP_STATUS = {"help", "status"};
    protected final String[] CLI_CSAR_LIST = {"csar", "list"};
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
    ApiController apiController;

    @Before
    public void setUp() {
        apiController = mock(ApiController.class);
    }
}
