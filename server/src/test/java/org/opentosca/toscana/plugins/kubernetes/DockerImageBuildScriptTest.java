package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.plugins.model.DockerApp;
import org.opentosca.toscana.plugins.testdata.MockDockerApp;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DockerImageBuildScriptTest extends BaseJUnitTest {
    private MockDockerApp mockDockerApp;

    @Before
    public void setUp() {
        mockDockerApp = new MockDockerApp();

    }

    @Test
    public void generateValidBuildScript() throws IOException, DockerImageBuildScript.DockerImageBuildScriptException {
        DockerApp app = mockDockerApp.validDockerApp();
        generateBuildScript(app);
    }

    private void generateBuildScript(DockerApp app) throws IOException, DockerImageBuildScript.DockerImageBuildScriptException {
        DockerImageBuildScript buildScript = new DockerImageBuildScript();
        buildScript.addDockerApp(app);
        String expected = IOUtils.toString(this.getClass().getResource("/kubernetes/simple_task_app_build_script.sh"));
        assertEquals(expected, buildScript.generateBuildScript());
    }

    @Test(expected = DockerImageBuildScript.DockerImageBuildScriptException.class)
    public void generateInvalidBuildScript() throws IOException, DockerImageBuildScript.DockerImageBuildScriptException {
        DockerApp app = mockDockerApp.invalidDockerApp();
        generateBuildScript(app);
    }

}
