package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;

import org.jgrapht.Graph;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

public class CloudFoundryPluginTest extends BaseUnitTest {

    private final String appName = "my_app";
    private final String appNameClearedUp = "my-app";
    private final ArrayList<String> paths = new ArrayList<>();
    private final String resourcesPath = "src/test/resources/";
    @Mock
    public TransformationContext context;
    EffectiveModel lamp;
    private File targetDir;
    private Graph<RootNode, RootRelationship> graph;
    private Map<RootNode, Application> nodeApplicationMap = new HashMap<>();
    Logger logger;

    @Test
    public void test() {
    }
/*
    @Before
    public void setUp() throws Exception {
        Application myApp = new Application(appName, 1);

        graph = lamp.getTopology();
        for (int i = 0; i < myApp.getStack().getNodes().size(); i++) {
            nodeApplicationMap.put(myApp.getStack().getNodes().get(i).getNode(), myApp);
        }
        
        NodeVisitor visitor = new NodeVisitor(myApp, nodeApplicationMap, graph, logger);
        EffectiveModel effectiveModel = new EffectiveModel(TestCsars.VALID_MINIMAL_DOCKER_TEMPLATE, log);
        lamp = new EffectiveModel(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, log);

        File sourceDir = new File(resourcesPath, "csars/yaml/valid/lamp-noinput");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        Set<RootNode> nodes = lamp.getNodes();

        when(context.getPluginFileAccess()).thenReturn(fileAccess);
        when(context.getLogger((Class<?>) any(Class.class))).thenReturn(LoggerFactory.getLogger("Dummy Logger"));
        when(context.getModel()).thenReturn(effectiveModel);

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
        File targetFile = new File(targetDir + "/output/scripts/", FILEPRAEFIX_DEPLOY + deploy_name +
            FILESUFFIX_DEPLOY);
        String deployScript = FileUtils.readFileToString(targetFile);
        String expectedOutput = SHEBANG + "\n" + SOURCE_UTIL_ALL + "\n" +
            "check \"cf\"\n" +
            "cf create-service {plan} {service} my_db\n" +
            "check python\n" +
            "python replace.py ../../app1/my_app/configure_myphpapp.sh /var/www/html/ /home/vcap/app/htdocs/\n" +
            "python replace.py ../../app1/my_app/create_myphpapp.sh /var/www/html/ /home/vcap/app/htdocs/\n" +
            "cf push my-app -f ../manifest.yml\n" +
            "python executeCommand.py my-app /home/vcap/app/htdocs/my_app/configure_myphpapp.sh\n" +
            "python executeCommand.py my-app /home/vcap/app/htdocs/my_app/create_myphpapp.sh\n" +
            "python configureMysql.py ../../app1/my_db/createtable.sql\n";

        assertEquals(expectedOutput, deployScript);
    }
/* Nullpointer for Properties
    @Test
    public void checkModel() throws Exception {
        CloudFoundryLifecycle cloudFoundry = new CloudFoundryLifecycle(context);
        assertTrue(cloudFoundry.checkModel());
    }

    @Test
    public void checkTransformation() throws Exception {
        CloudFoundryLifecycle cloudFoundry = new CloudFoundryLifecycle(context);
        cloudFoundry.prepare();
        cloudFoundry.transform();
    }
    */
}
