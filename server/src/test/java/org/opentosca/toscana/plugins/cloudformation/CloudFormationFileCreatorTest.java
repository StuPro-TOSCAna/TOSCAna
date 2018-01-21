package org.opentosca.toscana.plugins.cloudformation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.FILEPATH_TARGET;
import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.SCRIPTS_DIR_PATH;
import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.UTIL_DIR_PATH;

public class CloudFormationFileCreatorTest extends BaseUnitTest {
    private CloudFormationFileCreator fileCreator;
    private CloudFormationModule cfnModule;

    @Mock
    private Log log;
    private File targetDir;
    private File FILEPATH_SOURCE_TEST_FILE;
    private File FILEPATH_TARGET_TEST_FILE;
    private final String BASH_FILE_ENDING = ".sh";
    private final String FILENAME_CREATE_BUCKET = "create-bucket";
    private final String FILENAME_UPLOAD_FILE = "upload-file";
    private static final String FILENAME_DEPLOY = "deploy";
    private static final String FILENAME_UPLOAD = "file-upload";
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
        
        when(log.getLogger(any(Class.class))).thenReturn(mock(Logger.class));
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        cfnModule = new CloudFormationModule(fileAccess);
        fileCreator = new CloudFormationFileCreator(log.getLogger(CloudFormationFileCreator.class), cfnModule);
    }

    @Test
    public void createScripts() throws Exception {
//        cfnModule.putFileToBeUploaded( "");
        fileCreator.createScripts();

        File deployScript = new File(targetDir,
            SCRIPTS_DIR_PATH + FILENAME_DEPLOY + BASH_FILE_ENDING);
//        File fileUploadScript = new File(targetDir,
//            SCRIPTS_DIR_PATH + FILENAME_UPLOAD + BASH_FILE_ENDING);
        assertTrue(deployScript.exists());
//        assertTrue(fileUploadScript.exists());
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
    public void copyFiles() throws Exception {     
        cfnModule.putFileToBeUploaded(FILENAME_TEST_FILE);
        fileCreator.copyFiles();
        assertTrue(FILEPATH_TARGET_TEST_FILE.exists());
    }

    /**
     * Writes a test file in the sourceDir.
     */
    private void writeTestFile() throws Exception{
        Writer writer = new OutputStreamWriter(new FileOutputStream(FILEPATH_SOURCE_TEST_FILE));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();
    }
}
