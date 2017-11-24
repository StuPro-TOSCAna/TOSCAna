package org.opentosca.toscana.cli;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.cli.commands.Constants;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.FileUtils;

final class TestHelper {

    final String CSAR = "mongo-db";
    final String PLATFORM = "p-a";
    final String[] EMPTY = {};
    final String[] HELP = {"help"};
    final String[] STATUS = {"status"};
    final String[] CSAR_AR = {"csar", "-v"};
    final String[] CSAR_DELETE = {"csar", "delete", "-c", CSAR, "-m"};
    final String[] CSAR_INFO = {"csar", "info", "-c", CSAR};
    final String[] CSAR_UPLOAD = {"csar", "upload", "-f", "arch.csar"};
    final String[] CSAR_LIST = {"csar", "list"};
    final String[] PLATFORM_AR = {"platform"};
    final String[] PLATFORM_LIST = {"platform", "list"};
    final String[] PLATFORM_INFO = {"platform", "info", "-p", PLATFORM};
    final String[] PLATFORM_STATUS = {"platform", "status", "-p", PLATFORM};
    final String[] TRANSFORMATION_AR = {"transformation"};
    final String[] TRANSFORMATION_DELETE = {"transformation", "delete", "-c", CSAR, "-p", PLATFORM, "-m"};
    final String[] TRANSFORMATION_DELETE_NOINPUT = {"transformation", "delete"};
    final String[] TRANSFORMATION_DOWNLOAD = {"transformation", "download", "-c", CSAR, "-p", PLATFORM};
    final String[] TRANSFORMATION_DOWNLOAD_NOINPUT = {"transformation", "download"};
    final String[] TRANSFORMATION_INFO = {"transformation", "info", "-c", CSAR, "-p", PLATFORM};
    final String[] TRANSFORMATION_INFO_NOINPUT = {"transformation", "info"};
    final String[] TRANSFORMATION_LIST = {"transformation", "list", "-c", CSAR};
    final String[] TRANSFORMATION_LOGS = {"transformation", "logs", "-c", CSAR, "-p", PLATFORM, "-v"};
    final String[] TRANSFORMATION_LOGS_NOINPUT = {"transformation", "logs"};
    final String[] TRANSFORMATION_START = {"transformation", "start", "-c", CSAR, "-p", PLATFORM};
    final String[] TRANSFORMATION_START_NOINPUT = {"transformation", "start"};
    final String[] TRANSFORMATION_STATUS = {"transformation", "status", "-c", CSAR, "-p", PLATFORM};
    final String[] TRANSFORMATION_STATUS_NOINPUT = {"transformation", "status"};
    final String[] TRANSFORMATION_STOP = {"transformation", "stop", "-c", CSAR, "-p", PLATFORM};
    final String[] TRANSFORMATION_STOP_NOINPUT = {"transformation", "stop"};
    final String[] INPUT_LIST = {"input", "-c", CSAR, "-p", PLATFORM};
    final String[] INPUT_MANUAL_VALID = {"input", "-c", CSAR, "-p", PLATFORM, "test=test"};
    final String[] INPUT_MANUAL_NOT_VALID = {"input", "-c", CSAR, "-p", PLATFORM, "test==test"};
    final String[] INPUT_MANUAL_ERROR = {"input", "-c", CSAR, "-p", PLATFORM, "test="};
    final String[] INPUT_FILE_VALID = {"input", "-c", CSAR, "-p", PLATFORM, "-f", "src/test/resources/responses/test.txt"};
    final String[] INPUT_NOINPUT = {"input"};
    private final String TRANSFORMATION_VALID = "mongo-db/p-a";
    final String[] TRANSFORMATION_DELETE_T_VALID = {"transformation", "delete", "-t", TRANSFORMATION_VALID};
    final String[] TRANSFORMATION_DOWNLOAD_T_VALID = {"transformation", "download", "-t", TRANSFORMATION_VALID};
    final String[] TRANSFORMATION_INFO_T_VALID = {"transformation", "info", "-t", TRANSFORMATION_VALID};
    final String[] TRANSFORMATION_LOGS_T_VALID = {"transformation", "logs", "-t", TRANSFORMATION_VALID};
    final String[] TRANSFORMATION_START_T_VALID = {"transformation", "start", "-t", TRANSFORMATION_VALID};
    final String[] TRANSFORMATION_STATUS_T_VALID = {"transformation", "status", "-t", TRANSFORMATION_VALID};
    final String[] TRANSFORMATION_STOP_T_VALID = {"transformation", "stop", "-t", TRANSFORMATION_VALID};
    final String[] INPUT_T_LIST = {"input", "-t", TRANSFORMATION_VALID};
    final String[] INPUT_MANUAL_T_VALID = {"input", "-t", TRANSFORMATION_VALID, "test=test"};
    final String[] INPUT_FILE_T_VALID = {"input", "-t", TRANSFORMATION_VALID, "-f", "src/test/resources/responses/test.txt"};
    private final String TRANSFORMATION_NOTVALID = "mongo-db//p-a";
    final String[] TRANSFORMATION_DELETE_T_NOTVALID = {"transformation", "delete", "-t", TRANSFORMATION_NOTVALID};
    final String[] TRANSFORMATION_DOWNLOAD_T_NOTVALID = {"transformation", "download", "-t", TRANSFORMATION_NOTVALID};
    final String[] TRANSFORMATION_INFO_T_NOTVALID = {"transformation", "info", "-t", TRANSFORMATION_NOTVALID};
    final String[] TRANSFORMATION_LOGS_T_NOTVALID = {"transformation", "logs", "-t", TRANSFORMATION_NOTVALID};
    final String[] TRANSFORMATION_START_T_NOTVALID = {"transformation", "start", "-t", TRANSFORMATION_NOTVALID};
    final String[] TRANSFORMATION_STATUS_T_NOTVALID = {"transformation", "status", "-t", TRANSFORMATION_NOTVALID};
    final String[] TRANSFORMATION_STOP_T_NOTVALID = {"transformation", "stop", "-t", TRANSFORMATION_NOTVALID};
    final String[] INPUT_T_NOTVALID = {"input", "-t", TRANSFORMATION_NOTVALID};
    private MockWebServer server = null;
    private MockResponse response = null;
    private Map<String, String> helpMap = null;
    private Constants con = null;

    final void setUp() throws IOException {
        final String SYSTEM_STATUS = FileUtils.readFileToString(new File("src/test/resources/responses/systemStatus.txt"), "UTF-8");
        final String PLATFORM_LIST = FileUtils.readFileToString(new File("src/test/resources/responses/platformList.txt"), "UTF-8");
        final String PLATFORM_INFO = FileUtils.readFileToString(new File("src/test/resources/responses/platformInfo.txt"), "UTF-8");
        final String CSAR_LIST = FileUtils.readFileToString(new File("src/test/resources/responses/csarList.txt"), "UTF-8");
        final String CSAR_INFO = FileUtils.readFileToString(new File("src/test/resources/responses/csarInfo.txt"), "UTF-8");
        final String TRANSFORMATION_LIST = FileUtils.readFileToString(new File("src/test/resources/responses/transformationList.txt"), "UTF-8");
        final String TRANSFORMATION_INFO = FileUtils.readFileToString(new File("src/test/resources/responses/transformationInfo.txt"), "UTF-8");
        final String TRANSFORMATION_LOGS = FileUtils.readFileToString(new File("src/test/resources/responses/transformationLogs.txt"), "UTF-8");
        final String TRANSFORMATION_ARTIFACT = FileUtils.readFileToString(new File("src/test/resources/responses/transformationArtifact.txt"), "UTF-8");
        final String TRANSFORMATION_INPUTS = FileUtils.readFileToString(new File("src/test/resources/responses/transformationInputs.txt"), "UTF-8");
        final String TRANSFORMATION_RESPONSE = FileUtils.readFileToString(new File("src/test/resources/responses/transformationResponse.txt"), "UTF-8");

        server = new MockWebServer();
        response = new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache");

        helpMap = new HashMap<>();
        helpMap.put("systemstatus", SYSTEM_STATUS);
        helpMap.put("platformlist", PLATFORM_LIST);
        helpMap.put("platforminfo", PLATFORM_INFO);
        helpMap.put("csarlist", CSAR_LIST);
        helpMap.put("csarinfo", CSAR_INFO);
        helpMap.put("transformationlist", TRANSFORMATION_LIST);
        helpMap.put("transformationinfo", TRANSFORMATION_INFO);
        helpMap.put("transformationlogs", TRANSFORMATION_LOGS);
        helpMap.put("transformationartifact", TRANSFORMATION_ARTIFACT);
        helpMap.put("transformationinputs", TRANSFORMATION_INPUTS);
        helpMap.put("transformationresponse", TRANSFORMATION_RESPONSE);
        
        con = new Constants();
    }

    final void tearDown() throws IOException {
        server.shutdown();
    }

    final void serverEnqueue() throws IOException {
        server.enqueue(response);
        server.start(con.API_PORT);
    }

    final void server200Response() throws IOException {
        response.setResponseCode(200);
        server.enqueue(response);
        server.start(con.API_PORT);
    }

    final void server201Response() throws IOException {
        response.setResponseCode(201);
        server.enqueue(response);
        server.start(con.API_PORT);
    }

    final void server400Response() throws IOException {
        response.setResponseCode(400);
        server.enqueue(response);
        server.start(con.API_PORT);
    }

    final void server404Response() throws IOException {
        response.setResponseCode(404);
        server.enqueue(response);
        server.start(con.API_PORT);
    }

    final void server500Response() throws IOException {
        response.setResponseCode(500);
        server.enqueue(response);
        server.start(con.API_PORT);
    }

    final void setServerBody(String help) throws IOException {
        if (helpMap.containsKey(help)) {
            response.setBody(helpMap.get(help));
            server.enqueue(response);
            server.start(con.API_PORT);
        }
    }
}
