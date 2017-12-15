package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudfoundry.visitors.CloudFoundryNodeVisitor;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.CLI_CREATE_SERVICE;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.CLI_PUSH;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILESUFFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.MANIFEST;

public class CloudFoundryPluginTest extends BaseUnitTest {

    private final static EffectiveModel lamp = new TestEffectiveModels().getLampModel();
    private static CloudFoundryApplication myApp = new CloudFoundryApplication();
    private final static CloudFoundryNodeVisitor visitor = new CloudFoundryNodeVisitor(myApp);
    @Mock
    private Log log;
    private File targetDir;
    private final String appName = "my_app";
    private final ArrayList<String> paths = new ArrayList<>();
    private final String resourcesPath = "src/test/resources/";

    @Before
    public void setUp() throws Exception {
        File sourceDir = new File(resourcesPath + "csars/yaml/valid/lamp-input/");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        when(log.getLogger(any(Class.class))).thenReturn(LoggerFactory.getLogger("Test logger"));
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        Set<RootNode> nodes = lamp.getNodes();

        paths.add("my_app/myphpapp.php");
        paths.add("my_app/mysql-credentials.php");
        paths.add("my_app/create_myphpapp.sh");
        paths.add("my_app/configure_myphpapp.sh");
        paths.add("mysql_dbms/mysql_dbms_configure.sh");

        for (VisitableNode node : nodes) {
            node.accept(visitor);
        }
        myApp = visitor.getFilledApp();
        CloudFoundryFileCreator fileCreator = new CloudFoundryFileCreator(fileAccess, myApp);
        fileCreator.createFiles();
    }

    @Test
    public void getName() {
        assertEquals(appName, myApp.getName());
    }

    @Test
    public void getManifest() throws Exception {
        File targetFile = new File(targetDir, MANIFEST);
        String manifestContent = FileUtils.readFileToString(targetFile);

        File expectedFile = new File(resourcesPath + "cloudFoundry/lamp-manifest.yml");
        String expectedManifestContent = FileUtils.readFileToString(expectedFile);

        assertEquals(expectedManifestContent, manifestContent);
    }

    @Test
    public void getFiles() {
        for (String path : paths) {
            File targetFile = new File(targetDir, path);
            assertTrue(targetFile.exists());
        }
    }

    @Test
    public void getDeployScript() throws Exception {
        File targetFile = new File(targetDir + "/output/scripts/", FILEPRAEFIX_DEPLOY + appName +
            FILESUFFIX_DEPLOY);
        String deployScript = FileUtils.readFileToString(targetFile);
        String expectedOutput = String.format("#!/bin/sh\nsource util/*\ncheck \"cf\"\n%smy_db\n%s%s\n",
            CLI_CREATE_SERVICE, CLI_PUSH, appName);

        assertEquals(expectedOutput, deployScript);
    }
}
