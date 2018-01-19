package org.opentosca.toscana.plugins.cloudformation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static org.junit.Assert.assertTrue;
import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.SCRIPTS_DIR_PATH;
import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.UTIL_DIR_PATH;

public class CloudFormationFileCreatorTest extends BaseUnitTest {
    private CloudFormationFileCreator fileCreator;
    private CloudFormationModule cfnModule;
    private final static Logger logger = LoggerFactory.getLogger(CloudFormationPluginTest.class);

    @Mock
    private Log log;
    private File targetDir;
    private String appName;
    private final String outputPath = AbstractLifecycle.SCRIPTS_DIR_PATH;
    private final String BASH_FILE_ENDING = ".sh";
    private final String FILENAME_CREATE_BUCKET = "create-bucket";
    private final String FILENAME_UPLOAD_FILE = "uploadFile";
    private final String PATH_TO_TEST_FILE = tmpdir + "sourceDir" + "test-file.txt";
    private static final String FILENAME_DEPLOY = "deploy";
    private static final String FILENAME_UPLOAD = "file-upload";

    @Before
    public void setUp() throws Exception {
//        File sourceDir = new File(tmpdir, "sourceDir");
//        targetDir = new File(tmpdir, "targetDir");
//        sourceDir.mkdir();
//        targetDir.mkdir();
//
//        // Write a sample file to be copied
//        Writer writer = new OutputStreamWriter(new FileOutputStream(new File(PATH_TO_TEST_FILE)));
//        writer.write("abcdefghijklmnopqrstuvwxyz\n");
//        writer.close();
//
//        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
//        cfnModule = new CloudFormationModule(fileAccess);
//        fileCreator = new CloudFormationFileCreator(logger, cfnModule);
    }

    @Test
    public void createScripts() throws Exception {
//        cfnModule.putFileToBeUploaded("", "");
//        fileCreator.createScripts();
//
//        File deployScript = new File(targetDir,
//            SCRIPTS_DIR_PATH + FILENAME_DEPLOY + BASH_FILE_ENDING);
//        File fileUploadScript = new File(targetDir,
//            SCRIPTS_DIR_PATH + FILENAME_UPLOAD + BASH_FILE_ENDING);
//        File createBucketUtilScript = new File(targetDir,
//            UTIL_DIR_PATH + FILENAME_CREATE_BUCKET + BASH_FILE_ENDING);
//        File uploadFileUtilScript = new File(targetDir,
//            UTIL_DIR_PATH + FILENAME_UPLOAD_FILE + BASH_FILE_ENDING);
//
//        assertTrue(deployScript.exists());
//        assertTrue(fileUploadScript.exists());
//        assertTrue(createBucketUtilScript.exists());
//        assertTrue(uploadFileUtilScript.exists());
    }

    @Test
    public void copyFiles() throws Exception {
//        cfnModule.putFileToBeUploaded(PATH_TO_TEST_FILE, PATH_TO_TEST_FILE);
//        fileCreator.copyFiles();
//        assertTrue(new File(PATH_TO_TEST_FILE).exists());
    }
}
