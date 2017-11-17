package org.opentosca.toscana.cli;

import java.io.IOException;

import org.opentosca.toscana.cli.commands.Constants;

import org.junit.After;
import org.junit.Before;
import picocli.CommandLine;

public class InputTest {

    private ApiController api = null;
    private CliMain cli = null;
    private CommandLine cmd = null;
    private TestHelper helper = null;
    private Constants con = null;

    @Before
    public void setUp() throws IOException {
        api = new ApiController(false, false);
        cli = new CliMain();
        cmd = new CommandLine(cli);
        helper = new TestHelper();
        helper.setUp();
        con = new Constants();
    }

    @After
    public void tearDown() throws IOException {
        helper.tearDown();
    }
/*
    @Test
    public void testInputList() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_LIST);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputTransInput() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_T_LIST);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputManual() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_MANUAL_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputManualTransInput() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_MANUAL_T_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputManualErrorEquals() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_MANUAL_NOT_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputManualError() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_MANUAL_ERROR);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputFile() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_FILE_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputFileTransInput() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_FILE_T_VALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputNoInput() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_NOINPUT);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testInputError() throws IOException {
        helper.setServerBody("transformationinputs");
        final List<Object> parsed = cmd.parseWithHandler(new CommandLine.RunLast(), System.err, helper.INPUT_T_NOTVALID);
        assertEquals(1, parsed.size());
    }

    @Test
    public void testTransformationInputs() throws IOException {
        helper.setServerBody("transformationinputs");
        assertTrue(api.inputList(helper.CSAR, helper.PLATFORM).contains(con.INPUT_LIST_SUCCESS));
    }

    @Test
    public void testFail400TransformationInputs() throws IOException {
        helper.server400Response();
        assertEquals(con.INPUT_LIST_ERROR400 + "\n", api.inputList(helper.CSAR, helper.PLATFORM));
    }

    @Test
    public void testFail404TransformationInputs() throws IOException {
        helper.server404Response();
        assertEquals(con.INPUT_LIST_ERROR404 + "\n", api.inputList(helper.CSAR, helper.PLATFORM));
    }

    @Test
    public void testSetInputs() throws IOException {
        helper.setServerBody("transformationresponse");
        TransformationInput in = new TransformationInput();
        List<String> list = new ArrayList<>();
        list.add("text_property=Hallo Welt");
        Map<String, String> input = in.inputManual(list);
        assertTrue(input.size() == 1);
        assertEquals(con.INPUT_SET_SUCCESS, api.placeInput(helper.CSAR, helper.PLATFORM, input));
    }

    @Test
    public void testFail404SetInputs() throws IOException {
        helper.server404Response();
        TransformationInput in = new TransformationInput();
        List<String> list = new ArrayList<>();
        list.add("text_property=Hallo Welt");
        Map<String, String> input = in.inputManual(list);
        assertTrue(input.size() == 1);
        assertEquals(con.INPUT_SET_ERROR404 + "\n", api.placeInput(helper.CSAR, helper.PLATFORM, input));
    }

    @Test
    public void testFail400SetInputs() throws IOException {
        helper.server400Response();
        TransformationInput in = new TransformationInput();
        List<String> list = new ArrayList<>();
        list.add("text_property=Hallo Welt");
        Map<String, String> input = in.inputManual(list);
        assertTrue(input.size() == 1);
        assertEquals(con.INPUT_SET_ERROR400 + "\n", api.placeInput(helper.CSAR, helper.PLATFORM, input));
    }

    @Test
    public void testSetInputsFile() throws IOException {
        helper.setServerBody("transformationresponse");
        TransformationInput in = new TransformationInput();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("responses/test.txt").getFile());
        Map<String, String> input = in.inputFile(file);
        assertTrue(input.size() == 4);
        assertEquals(con.INPUT_SET_SUCCESS, api.placeInput(helper.CSAR, helper.PLATFORM, input));
    }
    */
}
