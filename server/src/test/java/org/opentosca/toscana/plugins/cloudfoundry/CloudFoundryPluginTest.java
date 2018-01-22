package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.visitors.NodeVisitors;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.CLI_CREATE_SERVICE_DEFAULT;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.CLI_PATH_TO_MANIFEST;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.CLI_PUSH;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.DEPLOY_NAME;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.FILESUFFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.MANIFEST_NAME;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.MANIFEST_PATH;

public class CloudFoundryPluginTest extends BaseUnitTest {

    private File targetDir;
    private final String appName = "my_app";
    private final String appNameClearedUp = "my-app";
    private final ArrayList<String> paths = new ArrayList<>();
    private final String resourcesPath = "src/test/resources/";

    @Before
    public void setUp() throws Exception {
        Application myApp = new Application(appName, 1);
        NodeVisitors visitor = new NodeVisitors(myApp);
        EffectiveModel lamp = new EffectiveModel(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, log);
        File sourceDir = new File(resourcesPath, "csars/yaml/valid/lamp-noinput");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        Set<RootNode> nodes = lamp.getNodes();

        paths.add("app1/my_app/myphpapp.php");
        paths.add("app1/my_app/mysql-credentials.php");
        paths.add("app1/my_app/create_myphpapp.sh");
        paths.add("app1/my_app/configure_myphpapp.sh");
        paths.add("app1/my_db/createtable.sql");

        for (VisitableNode node : nodes) {
            node.accept(visitor);
        }
        myApp = visitor.getFilledApp();
        List<Application> applications = new ArrayList<>();
        applications.add(myApp);
        FileCreator fileCreator = new FileCreator(fileAccess, applications);
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
        File targetFile = new File(targetDir + "/output/scripts/", FILEPRAEFIX_DEPLOY + DEPLOY_NAME +
            FILESUFFIX_DEPLOY);
        String deployScript = FileUtils.readFileToString(targetFile);
        String expectedOutput = "#!/bin/sh\n" +
            "source util/*\n" +
            "check \"cf\"\n" +
            "cf create-service {plan} {service} my_db\n" +
            "check python\n" +
            "python replace.py ../../app1/my_app/configure_myphpapp.sh /var/www/html/ /home/vcap/app/htdocs/\n" +
            "python replace.py ../../app1/my_app/create_myphpapp.sh /var/www/html/ /home/vcap/app/htdocs/\n" +
            "cf push my-app -f ../manifest.yml\n" +
            "python executeCommand.py my-app /home/vcap/app/htdocs/app1/my_app/configure_myphpapp.sh\n" +
            "python executeCommand.py my-app /home/vcap/app/htdocs/app1/my_app/create_myphpapp.sh\n" +
            "python configureMysql.py ../../app1/my_db/createtable.sql\n";
        
        //String expectedOutput = String.format("#!/bin/sh\nsource util/*\ncheck \"cf\"\n%smy_db\n%s%s%s%s\n",
        //    CLI_CREATE_SERVICE_DEFAULT, CLI_PUSH, appNameClearedUp, CLI_PATH_TO_MANIFEST, MANIFEST_NAME);

        assertEquals(expectedOutput, deployScript);
    }
}
