package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.util.Map;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryProvider;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryServiceType;
import org.opentosca.toscana.plugins.cloudfoundry.client.CloudFoundryConnection;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILESUFFIX_DEPLOY;

public class CloudFoundryServiceTest extends BaseUnitTest {

    private CloudFoundryConnection cloudFoundryConnection;
    private CloudFoundryApplication app;
    private CloudFoundryProvider provider;
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

    @Before
    public void setUp() {
        envUser = System.getenv("TEST_CF_USER");
        envPw = System.getenv("TEST_CF_PW");
        envHost = System.getenv("TEST_CF_HOST");
        envOrga = System.getenv("TEST_CF_ORGA");
        envSpace = System.getenv("TEST_CF_SPACE");

        app = new CloudFoundryApplication(appName);
    }

    @Test
    public void checkService() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);

        app.addService("my_db", CloudFoundryServiceType.MYSQL);

        try {
            cloudFoundryConnection = new CloudFoundryConnection(envUser,
                envPw,
                envHost,
                envOrga,
                envSpace);

            provider = new CloudFoundryProvider(CloudFoundryProvider.CloudFoundryProviderType.PIVOTAL);
            provider.setOfferedService(cloudFoundryConnection.getServices());
        } catch (Exception e) {
            assumeTrue(false);
        }

        app.setProvider(provider);

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
    public void checkServiceCredentials() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);

        try {
            cloudFoundryConnection = new CloudFoundryConnection(envUser,
                envPw,
                envHost,
                envOrga,
                envSpace);

            provider = new CloudFoundryProvider(CloudFoundryProvider.CloudFoundryProviderType.PIVOTAL);
            provider.setOfferedService(cloudFoundryConnection.getServices());
        } catch (Exception e) {
            assumeTrue(false);
        }

        JSONObject env = cloudFoundryConnection.getServiceCredentials("mydb", "myphpapp");
        
        System.out.println(env);
        
    }
        
}


