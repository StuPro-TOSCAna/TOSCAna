package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.APPLICATION_FOLDER;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.CLI_PATH_TO_MANIFEST;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.FILESUFFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.MANIFEST_NAME;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.MANIFEST_PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.NAMEBLOCK;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.APPLICATIONS_SECTION;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.ENVIRONMENT;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.RANDOM_ROUTE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.SERVICE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.BuildpackDetector.BUILDPACK_FILEPATH_PHP;
import static org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.BuildpackDetector.BUILDPACK_OBJECT_PHP;

public class FileCreatorTest extends BaseUnitTest {
    private FileCreator fileCreator;
    private Application testApp;

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
        testApp = new Application("testApp", 1);
        testApp.setName(appName);
        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        fileCreator = new FileCreator(fileAccess, testApp);
    }

    @Test
    public void createFiles() throws Exception {
        testApp.setPathToApplication(mainApplicationPath);
        testApp.addService(service1, ServiceTypes.MYSQL);
        testApp.setPathToApplication(mainApplicationPath);
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        File deployFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + appName + FILESUFFIX_DEPLOY);
        File buildPackAdditions = new File(targetDir, "/" + APPLICATION_FOLDER + testApp.getApplicationNumber() + "/myapp/main" + "/" + BUILDPACK_FILEPATH_PHP);

        assertTrue(targetFile.exists());
        assertTrue(deployFile.exists());
        assertTrue(buildPackAdditions.exists());
    }

    @Test
    public void contentManifest() throws Exception {
        testApp.setPathToApplication(mainApplicationPath);
        String expectedPath = String.format("../%s%s", APPLICATION_FOLDER, testApp.getApplicationNumber());
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedManifestContent = String.format("---\n%s:\n- %s: %s\n  %s: %s\n  %s: %s\n",
            APPLICATIONS_SECTION.getName(), NAMEBLOCK, appName, PATH.getName(), expectedPath, RANDOM_ROUTE.getName(), "true");

        assertEquals(expectedManifestContent, manifestContent);
    }

    @Test
    public void environmentVariables() throws Exception {
        testApp.addEnvironmentVariables(envVariable1);
        testApp.addEnvironmentVariables(envVariable2, envValue);
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedManifestContent = String.format("---\n%s:\n- %s: %s\n  %s: ../%s%s\n  %s: %s\n  %s:\n    %s: %s\n    %s: %s\n",
            APPLICATIONS_SECTION.getName(), NAMEBLOCK, appName,
            PATH.getName(), APPLICATION_FOLDER, testApp.getApplicationNumber(),
            RANDOM_ROUTE.getName(), "true",
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
        String expectedPath = "/" + APPLICATION_FOLDER + testApp.getApplicationNumber() + "/myapp/main" + "/" + BUILDPACK_FILEPATH_PHP;
        testApp.setPathToApplication(mainApplicationPath);
        testApp.addService(service1, ServiceTypes.MYSQL);
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
        testApp.addService(service1, ServiceTypes.MYSQL);
        testApp.addService(service2, ServiceTypes.MYSQL);
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedManifestContent = String.format("---\n%s:\n- %s: %s\n  %s: ../%s%s\n  %s: %s\n  %s:\n    - %s\n    - %s\n",
            APPLICATIONS_SECTION.getName(), NAMEBLOCK, appName,
            PATH.getName(), APPLICATION_FOLDER, testApp.getApplicationNumber(),
            RANDOM_ROUTE.getName(), "true",
            SERVICE.getName(),
            service2,
            service1);

        assertEquals(expectedManifestContent, manifestContent);
    }
}
