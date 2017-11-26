package org.opentosca.toscana.cli;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InputTest extends TestHelper {

    @Test
    public void testCliInput() throws IOException {
        CliMain.main(CSAR_DELETE);
        CliMain.main(CSAR_INFO);
        CliMain.main(CSAR_LIST);
        CliMain.main(CSAR_UPLOAD);
        CliMain.main(CSAR_AR);
        CliMain.main(PLATFORM_INFO);
        CliMain.main(PLATFORM_LIST);
        CliMain.main(PLATFORM_AR);
        CliMain.main(TRANSFORMATION_AR);
        CliMain.main(TRANSFORMATION_DELETE);
        CliMain.main(TRANSFORMATION_DOWNLOAD);
        CliMain.main(TRANSFORMATION_INFO);
        CliMain.main(TRANSFORMATION_LIST);
        CliMain.main(TRANSFORMATION_LOGS);
        CliMain.main(TRANSFORMATION_START);
        CliMain.main(TRANSFORMATION_STOP);
        CliMain.main(INPUT_LIST);
        CliMain.main(CLI_HELP);
        CliMain.main(CLI_HELP_STATUS);
        CliMain.main(CLI_CSAR_LIST);
        CliMain.main(CLI_STATUS);
        new CliMain().run();
    }

    @Test
    public void InputList() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_PROPERTIES_JSON, 200);
        assertTrue(api.inputList(CSAR, PLATFORM).contains("text"));
    }

    @Test
    public void InputListError() throws IOException {
        enqueError(400);
        assertEquals("", api.inputList(CSAR, PLATFORM));
    }
/*
    @Test
    public void InputPlace() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_PROPERTIES_JSON, 200);
        TransformationInput in = new TransformationInput();
        List<String> list = new ArrayList<>();
        list.add("text_property=Hallo Welt");
        Map<String, String> input = in.inputManual(list);
        assertTrue(input.size() == 1);
        assertEquals("", api.placeInput(CSAR, PLATFORM, input));
    }

    @Test
    public void InputPlaceFile() throws IOException {
        apiDoubleInput(CSAR, PLATFORM, TRANSFORMATION_PROPERTIES_JSON, 200);
        TransformationInput in = new TransformationInput();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("responses/test.txt").getFile());
        Map<String, String> input = in.inputFile(file);
        assertTrue(input.size() == 4);
        assertEquals("", api.placeInput(CSAR, PLATFORM, input));
    }
    */
}
