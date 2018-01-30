package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.cloudfoundry.application.Application;
import org.opentosca.toscana.plugins.cloudfoundry.application.ServiceTypes;
import org.opentosca.toscana.plugins.cloudfoundry.application.deployment.Deployment;
import org.opentosca.toscana.plugins.scripts.BashScript;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.opentosca.toscana.plugins.scripts.BashScript.SHEBANG;
import static org.opentosca.toscana.plugins.scripts.BashScript.SOURCE_UTIL_ALL;
import static org.opentosca.toscana.plugins.scripts.BashScript.SUBCOMMAND_EXIT;
import static org.opentosca.toscana.plugins.util.TestUtil.setUpMockTransformationContext;

public class DeploymentTest extends BaseUnitTest {

    private Application testApp;

    @Mock
    private Log log;
    private File targetDir;
    private BashScript deployScript;
    private String appName;
    private final String outputPath = AbstractLifecycle.SCRIPTS_DIR_PATH;
    private final String service = "cleardb";
    private PluginFileAccess fileAccess;

    private TransformationContext context;

    @Before
    public void setUp() throws IOException {
        EffectiveModel lamp = new EffectiveModelFactory().create(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, logMock());
        this.context = setUpMockTransformationContext(lamp);
        appName = "testApp";
        testApp = new Application("testApp", 1, context);
        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        deployScript = new BashScript(fileAccess, "deploy_" + appName);
    }

    @Test
    public void configureSql() throws IOException {
        String pythonFilename = "configureMysql.py";
        String pathToSqlFile = "../../test/configMysql.sql";
        Deployment deployment = new Deployment(deployScript, testApp, fileAccess, context);
        deployment.configureSql(pathToSqlFile);
        File targetFile = new File(targetDir, outputPath + pythonFilename);
        File deployFile = new File(targetDir, outputPath + "deploy_" + appName + ".sh");
        String contentDeploy = FileUtils.readFileToString(deployFile);

        String expectedDeployContent = String.format(SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            "check python\n" +
            "python %s %s\n", pythonFilename, pathToSqlFile);

        assertTrue(targetFile.exists());
        assertEquals(expectedDeployContent, contentDeploy);
    }

    @Test
    public void readCredentials() throws IOException {
        String pythonFilename = "readCredentials.py";
        Deployment deployment = new Deployment(deployScript, testApp, fileAccess, context);
        deployment.readCredentials(appName, service, ServiceTypes.MYSQL);
        File targetFile = new File(targetDir, outputPath + pythonFilename);
        File deployFile = new File(targetDir, outputPath + "deploy_" + appName + ".sh");
        String contentDeploy = FileUtils.readFileToString(deployFile);

        String expectedDeployContent = String.format(SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            "check python\n" +
            "python %s %s %s %s\n", pythonFilename, appName, service, ServiceTypes.MYSQL.getName());

        assertTrue(targetFile.exists());
        assertEquals(expectedDeployContent, contentDeploy);
    }

    @Test
    public void executeFile() throws IOException {
        String pythonFilename = "executeCommand.py";
        Deployment deployment = new Deployment(deployScript, testApp, fileAccess, context);
        String pathToFile = "/home/vcap/app/testApp/command.sh";
        deployment.executeFile(appName, pathToFile);
        File targetFile = new File(targetDir, outputPath + pythonFilename);
        File deployFile = new File(targetDir, outputPath + "deploy_" + appName + ".sh");
        String contentDeploy = FileUtils.readFileToString(deployFile);

        String expectedDeployContent = String.format(SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            "check python\n" +
            "python %s %s %s\n", pythonFilename, appName, pathToFile);

        assertTrue(targetFile.exists());
        assertEquals(expectedDeployContent, contentDeploy);
    }

    @Test
    public void replaceStrings() throws IOException {
        String pythonFilename = "replace.py";
        Deployment deployment = new Deployment(deployScript, testApp, fileAccess, context);
        String pathToFile = "../../testApp/move.sh";
        String findStr = "testAlt";
        String replaceStr = "testNeu";
        deployment.replaceStrings(pathToFile, findStr, replaceStr);
        File targetFile = new File(targetDir, outputPath + pythonFilename);
        File deployFile = new File(targetDir, outputPath + "deploy_" + appName + ".sh");
        String contentDeploy = FileUtils.readFileToString(deployFile);

        String expectedDeployContent = String.format(SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            "check python\n" +
            "python %s %s %s %s\n", pythonFilename, pathToFile, findStr, replaceStr);

        assertTrue(targetFile.exists());
        assertEquals(expectedDeployContent, contentDeploy);
    }
}
