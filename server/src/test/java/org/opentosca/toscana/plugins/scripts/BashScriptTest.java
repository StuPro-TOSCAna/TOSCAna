package org.opentosca.toscana.plugins.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BashScriptTest extends BaseJUnitTest {

    private BashScript bashScript;
    private PluginFileAccess access;
    private File targetScriptFolder;

    @Before
    public void setUp() {
        bashScript = null;
        access = new PluginFileAccess(tmpdir, tmpdir, mock(Log.class));
        try {
            bashScript = new BashScript(access, "test");
        } catch (IOException e) {
            e.printStackTrace();
        }
        targetScriptFolder = new File(tmpdir, "content/scripts/");
    }

    @Test
    public void createScriptTest() throws IOException {
        runCreateScriptTest();
    }

    @Test
    public void overwriteExistingScript() throws IOException {
        bashScript = new BashScript(access, "test");
        runCreateScriptTest();
    }

    public void runCreateScriptTest() throws IOException {
        File expectedGeneratedScript = new File(targetScriptFolder, "test.sh");
        assertTrue(expectedGeneratedScript.exists());
        List<String> result = readFile(expectedGeneratedScript);
        assertEquals("#!/bin/sh", result.get(0));
        assertEquals("source util/*", result.get(1));
        File expectedUtilsFile = new File(targetScriptFolder, "util/environment-check.sh");
        assertTrue(expectedUtilsFile.exists());
    }

    @Test
    public void appendTest() throws IOException {
        bashScript.append("test");
        File expectedGeneratedScript = new File(targetScriptFolder, "test.sh");
        List<String> result = readFile(expectedGeneratedScript);
        assertEquals("test", result.get(result.size() - 1));
    }

    public List<String> readFile(File file) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String current;
        while ((current = reader.readLine()) != null) {
            result.add(current);
        }
        reader.close();
        return result;
    }
}
