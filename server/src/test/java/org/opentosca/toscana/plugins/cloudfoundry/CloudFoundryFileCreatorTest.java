package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryServiceType;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.CLI_PATH_TO_MANIFEST;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILESUFFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.MANIFEST_NAME;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.MANIFEST_PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.NAMEBLOCK;
import static org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryManifestAttribute.APPLICATIONS_SECTION;
import static org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryManifestAttribute.ENVIRONMENT;
import static org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryManifestAttribute.PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryManifestAttribute.SERVICE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.CloudFoundryBuildpackDetection.BUILDPACK_FILEPATH_PHP;
import static org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.CloudFoundryBuildpackDetection.BUILDPACK_OBJECT_PHP;

public class CloudFoundryFileCreatorTest extends BaseUnitTest {
    private CloudFoundryFileCreator fileCreator;
    private CloudFoundryApplication testApp;

    @Mock
    private Log log;
    private File targetDir;
    private String appName;
    private final String outputPath = AbstractLifecycle.SCRIPTS_DIR_PATH;
    private final String buildPack1 = "mysql";
    private final String buildPack2 = "mysqli";
    private final String envVariable1 = "ENVTEST1";
    private final String envVariable2 = "ENVTEST2";
    private final String envValue = "TESTVALUE";
    private final String service1 = "cleardb";
    private final String service2 = "p-mysql";
    private final String mainApplicationPath = "myapp/main/myphpapp.php";

    @Before
    public void setUp() {
        appName = "testApp";
        testApp = new CloudFoundryApplication();
        testApp.setName(appName);
        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        fileCreator = new CloudFoundryFileCreator(fileAccess, testApp);
    }

    @Test
    public void createFiles() throws Exception {
        testApp.setPathToApplication(mainApplicationPath);
        testApp.addService(service1, CloudFoundryServiceType.MYSQL);
        testApp.setPathToApplication(mainApplicationPath);
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        File deployFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + appName + FILESUFFIX_DEPLOY);
        File buildPackAdditions = new File(targetDir, "/myapp/main" + "/" + BUILDPACK_FILEPATH_PHP);

        assertTrue(targetFile.exists());
        assertTrue(deployFile.exists());
        assertTrue(buildPackAdditions.exists());
    }

    @Test
    public void contentManifest() throws Exception {
        testApp.setPathToApplication(mainApplicationPath);
        String expectedPath = "../myapp/main";
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedManifestContent = String.format("---\n%s:\n- %s: %s\n  %s: %s\n",
            APPLICATIONS_SECTION.getName(), NAMEBLOCK, appName, PATH.getName(), expectedPath);

        assertEquals(expectedManifestContent, manifestContent);
    }

    @Test
    public void environmentVariables() throws Exception {
        testApp.addEnvironmentVariables(envVariable1);
        testApp.addEnvironmentVariables(envVariable2, envValue);
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedManifestContent = String.format("---\n%s:\n- %s: %s\n  %s:\n    %s: %s\n    %s: %s\n",
            APPLICATIONS_SECTION.getName(), NAMEBLOCK, appName,
            ENVIRONMENT.getName(),
            envVariable1, "TODO",
            envVariable2, envValue);

        assertEquals(expectedManifestContent, manifestContent);
    }

    @Test
    public void contentDeploy() throws Exception {
        fileCreator.createFiles();
        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + appName + FILESUFFIX_DEPLOY);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedDeployContent = "#!/bin/sh\n" +
            "source util/*\ncheck \"cf\"\ncf push " + appName + CLI_PATH_TO_MANIFEST + MANIFEST_NAME + "\n";
        assertEquals(expectedDeployContent, manifestContent);
    }

    @Test
    public void buildpackAdditons() throws Exception {
        String expectedPath = "/myapp/main" + "/" + BUILDPACK_FILEPATH_PHP;
        testApp.setPathToApplication(mainApplicationPath);
        testApp.addService(service1, CloudFoundryServiceType.MYSQL);
        String expectedBuildpackcontent = "{\n" +
            "    \"" + BUILDPACK_OBJECT_PHP + "\": [\n" +
            "        \"" + buildPack1 + "\",\n" +
            "        \"" + buildPack2 + "\",\n" +
            "        \"" + "bz2" + "\",\n" +
            "        \"" + "zlib" + "\",\n" +
            "        \"" + "curl" + "\",\n" +
            "        \"" + "mcrypt" + "\"\n" +
            "    ]\n" +
            "}";

        fileCreator.createFiles();
        File targetFile = new File(targetDir, expectedPath);
        String buildpackContent = FileUtils.readFileToString(targetFile);
        assertEquals(expectedBuildpackcontent, buildpackContent);
    }

    @Test
    public void services() throws Exception {
        testApp.addService(service1, CloudFoundryServiceType.MYSQL);
        testApp.addService(service2, CloudFoundryServiceType.MYSQL);
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedManifestContent = String.format("---\n%s:\n- %s: %s\n  %s:\n    - %s\n    - %s\n",
            APPLICATIONS_SECTION.getName(), NAMEBLOCK, appName,
            SERVICE.getName(),
            service2,
            service1);

        assertEquals(expectedManifestContent, manifestContent);
    }
}
