package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryProvider;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryServiceType;
import org.opentosca.toscana.plugins.cloudfoundry.client.CloudFoundryConnection;
import org.opentosca.toscana.plugins.cloudfoundry.client.CloudFoundryInjectionHandler;
import org.opentosca.toscana.plugins.cloudfoundry.visitors.CloudFoundryNodeVisitor;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILESUFFIX_DEPLOY;

public class CloudFoundryServiceTest extends BaseUnitTest {

    private CloudFoundryConnection cloudFoundryConnection;
    private CloudFoundryApplication app;
    private CloudFoundryFileCreator fileCreator;

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
    private final CloudFoundryProvider provider = new CloudFoundryProvider(CloudFoundryProvider
        .CloudFoundryProviderType.PIVOTAL);
    private CloudFoundryApplication myApp = new CloudFoundryApplication();

    @Before
    public void setUp() {
        envUser = System.getenv("TEST_CF_USER");
        envPw = System.getenv("TEST_CF_PW");
        envHost = System.getenv("TEST_CF_HOST");
        envOrga = System.getenv("TEST_CF_ORGA");
        envSpace = System.getenv("TEST_CF_SPACE");

        app = new CloudFoundryApplication(appName);
        app.setProvider(provider);
        cloudFoundryConnection = createConnection();
        app.setConnection(cloudFoundryConnection);
    }

    @Test
    public void checkService() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);

        app.addService("my_db", CloudFoundryServiceType.MYSQL);

        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        fileCreator = new CloudFoundryFileCreator(fileAccess, app);

        fileCreator.createFiles();
        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + appName + FILESUFFIX_DEPLOY);
        String deployContent = FileUtils.readFileToString(targetFile);
        assertThat(deployContent, CoreMatchers.containsString(expectedDeployContent));
    }

    @Test
    public void checkPushApplication() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        setUpMyApp();

        CloudFoundryInjectionHandler injectionHandler = new CloudFoundryInjectionHandler(myApp);
        injectionHandler.deploy();
    }

    @Test
    public void checkReadingServiceCredentials() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        JSONObject env = null;
        try {
            env = cloudFoundryConnection.getServiceCredentials("cleardb", "my_app");
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

        CloudFoundryInjectionHandler injectionHandler = new CloudFoundryInjectionHandler(myApp);
        injectionHandler.deploy();

        injectionHandler.getServiceCredentials();

        Map<String, String> environmentVariables = myApp.getEnvironmentVariables();
        String contentEnvVariables = "";
        for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
            contentEnvVariables = String.format("%s%s: %s\n", contentEnvVariables, entry.getKey(), entry.getValue());
        }

        assertThat(contentEnvVariables, not(CoreMatchers.containsString("database_user: TODO")));
    }

    private CloudFoundryConnection createConnection() {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        cloudFoundryConnection = new CloudFoundryConnection(envUser,
            envPw,
            envHost,
            envOrga,
            envSpace);

        return cloudFoundryConnection;
    }

    private void setUpMyApp() throws IOException, JSONException {
        CloudFoundryNodeVisitor visitor = new CloudFoundryNodeVisitor(myApp);
        EffectiveModel lamp = TestEffectiveModels.getLampModel();
        ArrayList<String> paths = new ArrayList<>();
        String resourcesPath = "src/test/resources/";
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
        paths.add("my_db/createtable.sql");

        for (VisitableNode node : nodes) {
            node.accept(visitor);
        }
        myApp = visitor.getFilledApp();
        assumeNotNull(cloudFoundryConnection);
        myApp.setConnection(cloudFoundryConnection);
        myApp.setProvider(provider);
        String pathToApplication = myApp.getPathToApplication();
        myApp.setPathToApplication(targetDir + "/" + pathToApplication + "/");
        CloudFoundryFileCreator fileCreator = new CloudFoundryFileCreator(fileAccess, myApp);
        fileCreator.createFiles();
    }
}


