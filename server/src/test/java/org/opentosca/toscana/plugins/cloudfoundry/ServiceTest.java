package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.testutils.TestUtils;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Provider;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;
import org.opentosca.toscana.plugins.cloudfoundry.client.InjectionHandler;
import org.opentosca.toscana.plugins.cloudfoundry.visitors.NodeVisitors;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.FileCreator.FILESUFFIX_DEPLOY;

public class ServiceTest extends BaseUnitTest {

    public final static String CF_ENVIRONMENT_USER = "TEST_CF_USER";
    public final static String CF_ENVIRONMENT_PW = "TEST_CF_PW";
    public final static String CF_ENVIRONMENT_HOST = "TEST_CF_HOST";
    public final static String CF_ENVIRONMENT_ORGA = "TEST_CF_ORGA";
    public final static String CF_ENVIRONMENT_SPACE = "TEST_CF_SPACE";

    private Connection connection;
    private Application app;
    private FileCreator fileCreator;

    private String envUser;
    private String envPw;
    private String envHost;
    private String envOrga;
    private String envSpace;

    @Mock
    private Log log;
    private File targetDir;
    private final String appName = "testapp";
    private final String expectedDeployContent = "cf create-service cleardb spark my_db";
    private final String outputPath = AbstractLifecycle.SCRIPTS_DIR_PATH;
    private final Provider provider = new Provider(Provider
        .CloudFoundryProviderType.PIVOTAL);
    private Application myApp = new Application();
    private PluginFileAccess fileAccess;

    @Before
    public void setUp() {
        envUser = System.getenv(CF_ENVIRONMENT_USER);
        envPw = System.getenv(CF_ENVIRONMENT_PW);
        envHost = System.getenv(CF_ENVIRONMENT_HOST);
        envOrga = System.getenv(CF_ENVIRONMENT_ORGA);
        envSpace = System.getenv(CF_ENVIRONMENT_SPACE);

        app = new Application(appName);
        app.setProvider(provider);
        connection = createConnection();
        app.setConnection(connection);

        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
    }

    @Test
    public void checkService() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);

        app.addService("my_db", ServiceTypes.MYSQL);
        fileCreator = new FileCreator(fileAccess, app);

        fileCreator.createFiles();
        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + appName + FILESUFFIX_DEPLOY);
        String deployContent = FileUtils.readFileToString(targetFile);
        assertThat(deployContent, CoreMatchers.containsString(expectedDeployContent));
    }

    @Test
    public void checkPushApplication() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        setUpMyApp();

        InjectionHandler injectionHandler = new InjectionHandler(fileAccess, myApp);
        boolean isPushed = injectionHandler.deploy();

        assertTrue(isPushed);
    }

    @Test
    public void checkReadingServiceCredentials() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        JSONObject env = null;
        try {
            env = connection.getServiceCredentials("cleardb", "my_app");
        } catch (JSONException e) {
            e.printStackTrace();
            assumeTrue(false);
        } catch (Exception e) {
            assumeTrue(false);
        }
        assertThat(env.toString(), CoreMatchers.containsString("username"));
    }

    @Test
    public void checkServiceCredentialInjection() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        setUpMyApp();

        InjectionHandler injectionHandler = new InjectionHandler(fileAccess, myApp);
        injectionHandler.deploy();

        injectionHandler.getServiceCredentials();

        Map<String, String> environmentVariables = myApp.getEnvironmentVariables();
        String contentEnvVariables = "";
        for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
            contentEnvVariables = String.format("%s%s: %s\n", contentEnvVariables, entry.getKey(), entry.getValue());
        }

        assertThat(contentEnvVariables, not(CoreMatchers.containsString("database_user: TODO")));
    }

    private Connection createConnection() {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        connection = new Connection(envUser,
            envPw,
            envHost,
            envOrga,
            envSpace);

        return connection;
    }

    private void setUpMyApp() throws IOException, JSONException {
        NodeVisitors visitor = new NodeVisitors(myApp);
        EffectiveModel lamp = TestEffectiveModels.getLampModel();
        ArrayList<String> paths = new ArrayList<>();
        String resourcesPath = "src/test/resources/";
        File sourceDir = new File(resourcesPath + "csars/yaml/valid/lamp-input/");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        log = TestUtils.getMockLog();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        Set<RootNode> nodes = lamp.getNodes();

        paths.add("my_app/myphpapp.php");
        paths.add("my_app/mysql-credentials.php");
        paths.add("my_app/create_myphpapp.sh");
        paths.add("my_app/configure_myphpapp.sh");
        paths.add("my_db/createtable.sql");

        for (VisitableNode node : nodes) {
            node.accept(visitor);
        }
        myApp = visitor.getFilledApp();
        assumeNotNull(connection);
        myApp.setConnection(connection);
        myApp.setProvider(provider);
        String pathToApplication = myApp.getPathToApplication();
        myApp.setPathToApplication(targetDir + "/" + pathToApplication + "/");
        FileCreator fileCreator = new FileCreator(fileAccess, myApp);
        fileCreator.createFiles();
    }
}


