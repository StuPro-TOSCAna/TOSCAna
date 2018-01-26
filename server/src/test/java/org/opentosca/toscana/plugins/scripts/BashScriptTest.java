package org.opentosca.toscana.plugins.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.SCRIPTS_DIR_PATH;
import static org.opentosca.toscana.plugins.scripts.BashScript.SHEBANG;
import static org.opentosca.toscana.plugins.scripts.BashScript.SOURCE_UTIL_ALL;

public class BashScriptTest extends BaseUnitTest {

    private BashScript bashScript;
    private PluginFileAccess access;
    private File targetScriptFolder;
    private String fileName;

    @Before
    public void setUp() {
        bashScript = null;
        fileName = UUID.randomUUID().toString();
        access = new PluginFileAccess(tmpdir, tmpdir, mock(Log.class));
        try {
            bashScript = new BashScript(access, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        targetScriptFolder = new File(tmpdir, SCRIPTS_DIR_PATH);
    }

    @Test
    public void createScriptTest() throws IOException {
        File expectedGeneratedScript = new File(targetScriptFolder, fileName + ".sh");
        assertTrue(expectedGeneratedScript.exists());
        List<String> result = IOUtils.readLines(new FileInputStream(expectedGeneratedScript));
        assertEquals(SHEBANG, result.get(0));
        assertEquals(SOURCE_UTIL_ALL, result.get(1));
    }

    @Test
    public void appendTest() throws IOException {
        String string = UUID.randomUUID().toString();
        bashScript.append(string);
        File expectedGeneratedScript = new File(targetScriptFolder, fileName + ".sh");
        List<String> result = IOUtils.readLines(new FileInputStream(expectedGeneratedScript));
        assertEquals(string, result.get(result.size() - 1));
    }
}
