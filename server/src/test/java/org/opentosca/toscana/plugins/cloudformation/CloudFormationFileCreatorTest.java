package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.logging.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.SCRIPTS_DIR_PATH;
import static org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle.UTIL_DIR_PATH;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.CAPABILITY_IAM;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.CLI_COMMAND_CREATESTACK;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.CLI_PARAM_CAPABILITIES;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.CLI_PARAM_STACKNAME;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.CLI_PARAM_TEMPLATEFILE;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.FILENAME_CREATE_STACK;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.FILENAME_DEPLOY;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.FILENAME_UPLOAD;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.RELATIVE_DIRECTORY_PREFIX;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationFileCreator.TEMPLATE_YAML;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.FILEPATH_TARGET;
import static org.opentosca.toscana.plugins.scripts.BashScript.SHEBANG;
import static org.opentosca.toscana.plugins.scripts.BashScript.SOURCE_UTIL_ALL;
import static org.opentosca.toscana.plugins.scripts.BashScript.SUBCOMMAND_EXIT;

public class CloudFormationFileCreatorTest extends BaseUnitTest {
    private CloudFormationFileCreator fileCreator;
    private CloudFormationModule cfnModule;

    @Mock
    private Log log;
    private File targetDir;
    private File FILEPATH_SOURCE_TEST_FILE;
    private File FILEPATH_TARGET_TEST_FILE;
    private String FILEPATH_TARGET_TEST_FILE_LOCAL;
    private final String BASH_FILE_ENDING = ".sh";
    private final String FILENAME_CREATE_BUCKET = "create-bucket";
    private final String FILENAME_UPLOAD_FILE = "upload-file";
    private static final String FILENAME_TEST_FILE = "test-text-file.txt";

    @Before
    public void setUp() throws Exception {
        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();

        FILEPATH_SOURCE_TEST_FILE = new File(sourceDir, FILENAME_TEST_FILE);
        FILEPATH_TARGET_TEST_FILE = new File(targetDir, FILEPATH_TARGET + FILENAME_TEST_FILE);
        writeTestFile();
        FILEPATH_TARGET_TEST_FILE_LOCAL = RELATIVE_DIRECTORY_PREFIX + FILENAME_TEST_FILE;

        when(log.getLogger(any(Class.class))).thenReturn(mock(Logger.class));
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        cfnModule = new CloudFormationModule(fileAccess, "us-west-2", new BasicAWSCredentials("", ""));
        TransformationContext context = mock(TransformationContext.class);
        when(context.getLogger((Class<?>) any(Class.class)))
            .thenReturn(LoggerFactory.getLogger("File Creator Test Logger"));
        fileCreator = new CloudFormationFileCreator(context, cfnModule);
    }

    @Test
    public void testWriteScripts() throws Exception {
        cfnModule.putFileToBeUploaded(FILENAME_TEST_FILE);
        fileCreator.writeScripts();

        File deployScript = new File(targetDir,
            SCRIPTS_DIR_PATH + FILENAME_DEPLOY + BASH_FILE_ENDING);
        File fileUploadScript = new File(targetDir,
            SCRIPTS_DIR_PATH + FILENAME_UPLOAD + BASH_FILE_ENDING);
        File createStackScript = new File(targetDir,
            SCRIPTS_DIR_PATH + FILENAME_CREATE_STACK + BASH_FILE_ENDING);

        assertTrue(deployScript.exists());
        assertTrue(fileUploadScript.exists());

        String expectedDeployContent = SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            "check \"aws\"\n" +
            "source file-upload.sh\n" +
            "source create-stack.sh\n";
        String expectedFileUploadContent = SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            "createBucket " + cfnModule.getBucketName() + " " + cfnModule.getAWSRegion() + "\n" +
            "uploadFile " + cfnModule.getBucketName() + " \"" + FILENAME_TEST_FILE + "\" \"" +
            FILEPATH_TARGET_TEST_FILE_LOCAL + "\"" + "\n";
        String expectedCreateStackContent = SHEBANG + "\n" +
            SOURCE_UTIL_ALL + "\n" +
            SUBCOMMAND_EXIT + "\n" +
            CLI_COMMAND_CREATESTACK + CLI_PARAM_STACKNAME + cfnModule.getStackName()
            + " " + CLI_PARAM_TEMPLATEFILE + "../" + TEMPLATE_YAML + " "
            + CLI_PARAM_CAPABILITIES + " " + CAPABILITY_IAM + "\n";
        String actualDeployContent = FileUtils.readFileToString(deployScript, StandardCharsets.UTF_8);
        String actualFileUploadContent = FileUtils.readFileToString(fileUploadScript, StandardCharsets.UTF_8);
        String actualCreateStackContent = FileUtils.readFileToString(createStackScript, StandardCharsets.UTF_8);

        assertEquals(expectedDeployContent, actualDeployContent);
        assertEquals(expectedFileUploadContent, actualFileUploadContent);
        assertEquals(expectedCreateStackContent, actualCreateStackContent);
    }

    @Test
    public void copyUtilScripts() throws Exception {
        fileCreator.copyUtilScripts();

        File createBucketUtilScript = new File(targetDir,
            UTIL_DIR_PATH + FILENAME_CREATE_BUCKET + BASH_FILE_ENDING);
        File uploadFileUtilScript = new File(targetDir,
            UTIL_DIR_PATH + FILENAME_UPLOAD_FILE + BASH_FILE_ENDING);
        assertTrue(createBucketUtilScript.exists());
        assertTrue(uploadFileUtilScript.exists());
    }

    @Test
    public void copyFiles() {
        cfnModule.putFileToBeUploaded(FILENAME_TEST_FILE);
        fileCreator.copyFiles();
        assertTrue(FILEPATH_TARGET_TEST_FILE.exists());
    }

    /**
     Writes a test file in the sourceDir.
     */
    private void writeTestFile() throws Exception {
        Writer writer = new OutputStreamWriter(new FileOutputStream(FILEPATH_SOURCE_TEST_FILE));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();
    }
}
