package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Provider;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;
import org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.FILESUFFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.SERVICE_FILE_PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.deploy_name;
import static org.opentosca.toscana.plugins.util.TestUtil.setUpMockTransformationContext;

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

    private File targetDir;
    private final String appName = "testapp";
    private final String expectedDeployContent = "cf create-service cleardb spark my_db";
    private final String outputPath = AbstractLifecycle.SCRIPTS_DIR_PATH;
    private final Provider provider = new Provider(Provider
        .CloudFoundryProviderType.PIVOTAL);
    private PluginFileAccess fileAccess;

    private TransformationContext context;

    @Before
    public void setUp() throws IOException {
        EffectiveModel lamp = new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, logMock());
        this.context = setUpMockTransformationContext(lamp);
        envUser = System.getenv(CF_ENVIRONMENT_USER);
        envPw = System.getenv(CF_ENVIRONMENT_PW);
        envHost = System.getenv(CF_ENVIRONMENT_HOST);
        envOrga = System.getenv(CF_ENVIRONMENT_ORGA);
        envSpace = System.getenv(CF_ENVIRONMENT_SPACE);

        app = new Application(appName, 1, context);
        app.setProvider(provider);
        connection = createConnection();
        app.setConnection(connection);

        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        fileAccess = new PluginFileAccess(sourceDir, targetDir, logMock());
    }

    @Test
    public void checkService() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);

        app.addService("my_db", ServiceTypes.MYSQL);
        List<Application> applications = new ArrayList<>();
        applications.add(app);
        fileCreator = new FileCreator(fileAccess, applications, context);

        fileCreator.createFiles();
        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + deploy_name + FILESUFFIX_DEPLOY);
        String deployContent = FileUtils.readFileToString(targetFile);
        assertThat(deployContent, CoreMatchers.containsString(expectedDeployContent));
    }

    @Test
    public void getAllServices() throws Exception {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        app.addService("my_db1", ServiceTypes.MYSQL);
        List<Application> applications = new ArrayList<>();
        applications.add(app);
        fileCreator = new FileCreator(fileAccess, applications, context);

        fileCreator.createFiles();
        File all_services = new File(targetDir, SERVICE_FILE_PATH);
        String all_services_expected_content = "# following services you could choose:";
        String all_services_content = FileUtils.readFileToString(all_services, Charset.defaultCharset());
        assertTrue(all_services.exists());
        assertThat(all_services_content, CoreMatchers.containsString(all_services_expected_content));
    }

    private Connection createConnection() {
        assumeNotNull(envUser, envHost, envOrga, envPw, envSpace);
        connection = new Connection(envUser,
            envPw,
            envHost,
            envOrga,
            envSpace, context);

        return connection;
    }
}


