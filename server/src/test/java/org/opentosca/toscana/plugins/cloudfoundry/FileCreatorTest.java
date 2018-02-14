package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.Provider;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.cloudfoundry.client.Connection;
import org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_HOST;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_ORGA;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_PW;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_SPACE;
import static org.opentosca.toscana.plugins.cloudfoundry.ServiceTest.CF_ENVIRONMENT_USER;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.APPLICATIONS_SECTION;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.ENVIRONMENT;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.RANDOM_ROUTE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.ManifestAttributes.SERVICE;
import static org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.BuildpackDetector.BUILDPACK_FILEPATH_PHP;
import static org.opentosca.toscana.plugins.cloudfoundry.application.buildpacks.BuildpackDetector.BUILDPACK_OBJECT_PHP;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.APPLICATION_FOLDER;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.CLI_PATH_TO_MANIFEST;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.ENVIRONMENT_CONFIG_FILE;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.FILESUFFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.MANIFEST_NAME;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.MANIFEST_PATH;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.NAMEBLOCK;
import static org.opentosca.toscana.plugins.cloudfoundry.filecreator.FileCreator.deploy_name;
import static org.opentosca.toscana.plugins.scripts.BashScript.SHEBANG;
import static org.opentosca.toscana.plugins.scripts.BashScript.SOURCE_UTIL_ALL;
import static org.opentosca.toscana.plugins.scripts.BashScript.SUBCOMMAND_EXIT;
import static org.opentosca.toscana.plugins.util.TestUtil.setUpMockTransformationContext;

public class FileCreatorTest extends BaseUnitTest {
    private FileCreator fileCreator;
    private Application testApp;

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
    private PluginFileAccess fileAccess;

    private Connection connection;
    private String envUser;
    private String envPw;
    private String envHost;
    private String envOrga;
    private String envSpace;

    private TransformationContext context;

    @Before
    public void setUp() throws IOException {
        EffectiveModel lamp = new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, logMock());
        this.context = setUpMockTransformationContext(lamp);
        appName = "testApp";
        testApp = new Application("testApp", 1, context);
        testApp.setName(appName);
        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        fileAccess = new PluginFileAccess(sourceDir, targetDir, logMock());
        List<Application> applications = new ArrayList<>();
        applications.add(testApp);
        fileCreator = new FileCreator(fileAccess, applications, context);
    }

    @Test
    public void createFiles() throws Exception {
        testApp.setPathToApplication(mainApplicationPath);
        testApp.addService(service1, ServiceTypes.MYSQL);
        testApp.setPathToApplication(mainApplicationPath);
        fileCreator.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        File deployFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + deploy_name + FILESUFFIX_DEPLOY);
        File buildPackAdditions = new File(targetDir, "/" + APPLICATION_FOLDER + testApp.getApplicationNumber() + "/" + BUILDPACK_FILEPATH_PHP);
        File environmentConfig = new File(targetDir, outputPath + appName + ENVIRONMENT_CONFIG_FILE);

        assertTrue(targetFile.exists());
        assertTrue(deployFile.exists());
        assertTrue(buildPackAdditions.exists());
        assertTrue(environmentConfig.exists());
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
        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + deploy_name + FILESUFFIX_DEPLOY);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedDeployContent = SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            "check \"cf\"\n" +
            "cf push " + appName + CLI_PATH_TO_MANIFEST + MANIFEST_NAME + "\n";
        assertEquals(expectedDeployContent, manifestContent);
    }

    @Test
    public void buildpackAdditons() throws Exception {
        String expectedPath = "/" + APPLICATION_FOLDER + testApp.getApplicationNumber() + "/" + BUILDPACK_FILEPATH_PHP;
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

    @Test
    public void checkMultipleApplicationsManifest() throws Exception {
        Application app1 = new Application("app1", 1, context);
        Application app2 = new Application("app2", 2, context);

        app1.addService(service1, ServiceTypes.MYSQL);
        app2.addService(service2, ServiceTypes.MYSQL);
        app1.addAttribute("attr1", "value1");
        app1.addAttribute("attr2", "value2");
        app2.addAttribute("attr1", "value1");
        app2.addAttribute("attr2", "value2");
        app1.addEnvironmentVariables("EnvTest", "5");

        List<Application> applications = new ArrayList<>();
        applications.add(app1);
        applications.add(app2);

        FileCreator fileCreatorMult = new FileCreator(fileAccess, applications, context);
        fileCreatorMult.createFiles();
        File targetFile = new File(targetDir, MANIFEST_PATH);
        String manifestContent = FileUtils.readFileToString(targetFile);
        String expectedContent = "---\n" +
            "applications:\n" +
            "- name: app1\n" +
            "  path: ../app1\n" +
            "  attr2: value2\n" +
            "  attr1: value1\n" +
            "  random-route: true\n" +
            "  env:\n" +
            "    EnvTest: 5\n" +
            "  services:\n" +
            "    - cleardb\n" +
            "- name: app2\n" +
            "  path: ../app2\n" +
            "  attr2: value2\n" +
            "  attr1: value1\n" +
            "  random-route: true\n" +
            "  services:\n" +
            "    - p-mysql\n";

        assertEquals(expectedContent, manifestContent);
    }

    @Test
    public void checkMultipleApplicationsDeployScript() throws Exception {
        Application app1 = new Application("app1", 1, context);
        Application app2 = new Application("app2", 2, context);

        app1.addService(service1, ServiceTypes.MYSQL);
        app2.addService(service2, ServiceTypes.MYSQL);

        List<Application> applications = new ArrayList<>();
        applications.add(app1);
        applications.add(app2);

        FileCreator fileCreatorMult = new FileCreator(fileAccess, applications, context);
        fileCreatorMult.createFiles();
        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + deploy_name + FILESUFFIX_DEPLOY);
        String deployscriptContent = FileUtils.readFileToString(targetFile);
        String expectedContent = SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            "check \"cf\"\n" +
            "cf create-service {plan} {service} cleardb\n" +
            "cf create-service {plan} {service} p-mysql\n" +
            "cf push app1 -f ../manifest.yml\n" +
            "cf push app2 -f ../manifest.yml\n";

        assertEquals(expectedContent, deployscriptContent);
    }

    @Test
    public void checkMultipleApplicationServices() throws IOException, JSONException {
        String serviceName = "mydb";
        envUser = System.getenv(CF_ENVIRONMENT_USER);
        envPw = System.getenv(CF_ENVIRONMENT_PW);
        envHost = System.getenv(CF_ENVIRONMENT_HOST);
        envOrga = System.getenv(CF_ENVIRONMENT_ORGA);
        envSpace = System.getenv(CF_ENVIRONMENT_SPACE);

        connection = createConnection();
        Application app = new Application("app", 1, context);
        Application secondApp = new Application("appSec", 2, context);
        app.setProvider(new Provider(Provider
            .CloudFoundryProviderType.PIVOTAL));
        app.setConnection(connection);

        secondApp.setProvider(new Provider(Provider
            .CloudFoundryProviderType.PIVOTAL));
        secondApp.setConnection(connection);

        app.addService(service1, ServiceTypes.MYSQL);
        secondApp.addService(serviceName, ServiceTypes.MYSQL);

        EffectiveModel lamp = new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, logMock());
        RootNode webApplicationNode = null;
        RootNode mysqlDatabaseNode = null;
        for (RootNode node : lamp.getNodes()) {
            if (node instanceof WebApplication) {
                webApplicationNode = node;
            }
            if (node instanceof MysqlDatabase) {
                mysqlDatabaseNode = node;
            }
        }

        app.addConfigMysql("my_db/configSql.sql");
        app.addExecuteFile("my_app/configure_myphpapp.sh", webApplicationNode);

        secondApp.addConfigMysql("database/config.sql");
        secondApp.addExecuteFile("database/dbinit.sh", mysqlDatabaseNode);

        List<Application> applications = new ArrayList<>();
        applications.add(app);
        applications.add(secondApp);
        FileCreator fileCreator = new FileCreator(fileAccess, applications, context);
        fileCreator.createFiles();

        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + deploy_name + FILESUFFIX_DEPLOY);
        String deployscriptContent = FileUtils.readFileToString(targetFile);
        String expectedContent =
            "check python\n" +
                "python replace.py ../../app1/my_app/configure_myphpapp.sh /var/www/html/ /home/vcap/app/htdocs/\n" +
                "python replace.py ../../app2/database/dbinit.sh /var/www/html/ /home/vcap/app/htdocs/\n" +
                "cf push app -f ../manifest.yml\n" +
                "cf push appSec -f ../manifest.yml\n" +
                "python readCredentials.py app cleardb mysql " + service1 + "\n" +
                "python executeCommand.py app /home/vcap/app/htdocs/my_app/configure_myphpapp.sh\n" +
                "python configureMysql.py ../../app1/my_db/configSql.sql\n" +
                "python readCredentials.py appSec cleardb mysql " + serviceName + "\n" +
                "python executeCommand.py appSec /home/vcap/app/database/dbinit.sh\n" +
                "python configureMysql.py ../../app2/database/config.sql\n";

        assertTrue(deployscriptContent.contains(expectedContent));
        //assertEquals(expectedContent, deployscriptContent);

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
