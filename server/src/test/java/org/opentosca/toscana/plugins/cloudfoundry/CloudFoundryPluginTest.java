package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_API;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_ORGANIZATION;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_PASSWORD;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryPlugin.CF_PROPERTY_KEY_USERNAME;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.FILESUFFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.MANIFEST_PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.deploy_name;
import static org.opentosca.toscana.plugins.util.TestUtil.setUpMockTransformationContext;

public class CloudFoundryPluginTest extends BaseUnitTest {

    private final ArrayList<String> paths = new ArrayList<>();
    private final String resourcesPath = "src/test/resources/";

    @Mock
    private TransformationContext context;
    private File targetDir;

    private CloudFoundryLifecycle cfCycle;

    @Before
    public void setUp() throws Exception {
        EffectiveModel lamp = new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, logMock());
        context = setUpMockTransformationContext(lamp);

        String userName = System.getenv("TEST_CF_USER");
        String pw = System.getenv("TEST_CF_PW");
        String host = System.getenv("TEST_CF_HOST");
        String space = System.getenv("TEST_CF_SPACE");
        String orga = System.getenv("TEST_CF_ORGA");

        Assume.assumeNotNull(userName, pw, host, space, orga);

        when(context.getInputs()).thenReturn(mock(PropertyInstance.class));
        when(context.getInputs().getOrThrow(CF_PROPERTY_KEY_USERNAME)).thenReturn(userName);
        when(context.getInputs().getOrThrow(CF_PROPERTY_KEY_PASSWORD)).thenReturn(pw);
        when(context.getInputs().getOrThrow(CF_PROPERTY_KEY_API)).thenReturn(host);
        when(context.getInputs().getOrThrow(CF_PROPERTY_KEY_SPACE)).thenReturn(space);
        when(context.getInputs().getOrThrow(CF_PROPERTY_KEY_ORGANIZATION)).thenReturn(orga);

        cfCycle = new CloudFoundryLifecycle(context);
        cfCycle.checkModel();
        cfCycle.prepare();
        cfCycle.fillApplications();

        File sourceDir = new File(resourcesPath, "csars/yaml/valid/lamp-noinput");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, logMock());
        //  Set<RootNode> nodes = lamp.getNodes();
        when(context.getPluginFileAccess()).thenReturn(fileAccess);

        paths.add("app1/my_app/index.php");
        paths.add("app1/my_app/create_myphpapp.sh");
        paths.add("app1/my_db/createtable.sql");

        List<Application> applications = cfCycle.getFilledApplications();

        FileCreator fileCreator = new FileCreator(fileAccess, applications, context);
        fileCreator.createFiles();
    }

    @Test
    public void getManifest() throws Exception {
        File targetFile = new File(targetDir, MANIFEST_PATH);
        String manifestContent = FileUtils.readFileToString(targetFile);

        File expectedFile = new File(resourcesPath + "cloudFoundry/lamp-manifest.yml");
        String expectedManifestContent = FileUtils.readFileToString(expectedFile);

        assertEquals(expectedManifestContent, manifestContent);
    }

    @Test
    public void getFiles() {
        for (String path : paths) {
            for (File file : targetDir.listFiles()) {
                System.out.println(file);
            }
            File targetFile = new File(targetDir, path);
            System.out.println(targetFile);
            assertTrue(targetFile.exists());
        }
    }

    @Test
    public void getDeployScript() throws Exception {
        File targetFile = new File(targetDir + "/output/scripts/", FILEPRAEFIX_DEPLOY + deploy_name +
            FILESUFFIX_DEPLOY);
        String deployScript = FileUtils.readFileToString(targetFile);
        String expectedOutput =
            "check python\n" +
                "python replace.py ../../app1/my_app/create_myphpapp.sh /var/www/html/ /home/vcap/app/htdocs/\n" +
                "cf push my-app -f ../manifest.yml --no-start\n" +
                "python readCredentials.py my-app cleardb mysql my_db\n" +
                "python configureMysql.py ../../app1/my_db/createtable.sql\n" +
                "cf start my-app\n" +
                "python executeCommand.py my-app /home/vcap/app/htdocs/my_app/create_myphpapp.sh";

        assertTrue(deployScript.contains(expectedOutput));
        //assertEquals(expectedOutput, deployScript);
    }

    @Test
    public void checkModelVerification() {
        assertTrue(cfCycle.checkModel());
    }
}
