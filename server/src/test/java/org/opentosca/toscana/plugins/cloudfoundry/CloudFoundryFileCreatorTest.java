package org.opentosca.toscana.plugins.cloudfoundry;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.*;
import static org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryManifestAttribute.*;

public class CloudFoundryFileCreatorTest extends BaseUnitTest {
    private CloudFoundryFileCreator fileCreator;
    private CloudFoundryApplication testApp;

    @Mock
    private Log log;
    private File targetDir;
    private String appName;
    private final String outputPath = AbstractLifecycle.SCRIPTS_DIR_PATH;
    private final String expectedBuildpackcontent = "{\n" +
        "    \"PHP-EXTENSIONS\": [\n" +
        "        \"mysql\",\n" +
        "        \"mysqli\"\n" +
        "    ]\n" +
        "}";

    @Before
    public void setUp() {
        appName = "testApp";
        testApp = new CloudFoundryApplication();
        testApp.setName(appName);
        testApp.addBuildpack("mysql");
        testApp.addBuildpack("mysqli");
        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        fileCreator = new CloudFoundryFileCreator(fileAccess, testApp);
    }

    @Test
    public void createFiles() throws Exception {
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST);
        File deployFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + appName + FILESUFFIX_DEPLOY);
        File buildPackAdditions = new File(targetDir, BUILDPACK_FILEPATH_PHP);

        assertTrue(targetFile.exists());
        assertTrue(deployFile.exists());
        assertTrue(buildPackAdditions.exists());
    }

    @Test
    public void contentManifest() throws Exception {
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedManifestContent = String.format("---\n%s:\n- %s: %s\n  %s:\n  %s:\n",
            APPLICATIONS_SECTION.getName(), NAMEBLOCK, appName, ENVIRONMENT.getName(), SERVICE.getName());

        assertEquals(expectedManifestContent, manifestContent);
    }

    @Test
    public void contentDeploy() throws Exception {
        fileCreator.createFiles();
        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + appName + FILESUFFIX_DEPLOY);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedDeployContent = "#!/bin/sh\n" +
            "source util/*\ncheck \"cf\"\ncf push " + appName + "\n";
        assertEquals(expectedDeployContent, manifestContent);
    }

    @Test
    public void buildpackAdditons() throws Exception {
        fileCreator.createFiles();
        File targetFile = new File(targetDir, BUILDPACK_FILEPATH_PHP);
        String buildpackContent = FileUtils.readFileToString(targetFile);
        assertEquals(expectedBuildpackcontent, buildpackContent);
    }
}
