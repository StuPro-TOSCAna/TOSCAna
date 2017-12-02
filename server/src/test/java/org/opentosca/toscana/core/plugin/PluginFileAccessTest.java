package org.opentosca.toscana.core.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PluginFileAccessTest extends BaseUnitTest {

    private PluginFileAccess access;
    @Mock
    private Log log;
    private File sourceDir;
    private File targetDir;
    private String fileContent = "this is a test content";
    private String targetFileName = "filename";
    private File targetFile;

    @Before
    public void setUp() {
        sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        targetFile = new File(targetDir, targetFileName);
        sourceDir.mkdir();
        targetDir.mkdir();
        when(log.getLogger(any(Class.class))).thenReturn(mock(Logger.class));
        access = new PluginFileAccess(sourceDir, targetDir, log);
    }

    @Test
    public void copyFile() throws Exception {
        String filename = "testFile";
        File file = new File(sourceDir, "testFile");
        file.createNewFile();
        File expectedFile = new File(targetDir, filename);

        assertFalse(expectedFile.exists());
        access.copy(filename);
        assertTrue(expectedFile.exists());
    }

    @Test
    public void copyDirRecursively() throws IOException {
        String dirname = "dir";
        File dir = new File(sourceDir, dirname);
        dir.mkdir();
        for (int i = 0; i < 10; i++) {
            new File(dir, String.valueOf(i)).createNewFile();
        }

        File expectedDir = new File(targetDir, dirname);
        assertFalse(expectedDir.exists());
        access.copy(dirname);
        assertTrue(expectedDir.exists());
        for (int i = 0; i < 10; i++) {
            assertTrue(new File(expectedDir, String.valueOf(i)).exists());
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void copyInvalidPathThrowsException() throws IOException {
        String file = "nonexistent_file";
        access.copy(file);
    }

    @Test
    public void copySourceToGivenTargetSuccessful() throws IOException {
        String filename = "some-file";
        File file = new File(sourceDir, filename);
        file.createNewFile();
        String alternativeDirName = "some-dir/nested/even-deeper";
        File alternateDirectory = new File(targetDir, alternativeDirName);
        File targetFile = new File(alternateDirectory, filename);

        String relativeTargetPath = String.format("%s/%s", alternativeDirName, filename);
        access.copy(filename, relativeTargetPath);

        assertTrue(targetFile.exists());
    }

    @Test
    public void write() throws Exception {
        String secondString = "second_test_string";
        access.access(targetFileName).appendln(fileContent).close();
        access.access(targetFileName).append(secondString).close();
        assertTrue(targetFile.isFile());
        List<String> result = IOUtils.readLines(new FileInputStream(targetFile));
        assertEquals(fileContent, result.get(result.size() - 2));
        assertEquals(secondString, result.get(result.size() - 1));
    }

    @Test(expected = IOException.class)
    public void writePathIsDirectoryThrowsException() throws IOException {
        targetFile.mkdir();
        access.access(targetFileName);
    }

    @Test
    public void writeSubDirectoriesGetAutomaticallyCreated() throws IOException {
        String path = "test/some/subdirs/filename";
        access.access(path).append(fileContent).close();
        File targetFile = new File(targetDir, path);
        assertTrue(targetFile.isFile());
        assertEquals(fileContent, FileUtils.readFileToString(targetFile));
    }

    @Test
    public void readSuccessful() throws IOException {
        String path = "file";
        File file = new File(sourceDir, path);
        InputStream inputStream = IOUtils.toInputStream(fileContent, "UTF-8");
        FileUtils.copyInputStreamToFile(inputStream, file);
        String result = access.read(path);

        assertNotNull(result);

        assertEquals(fileContent, result);
    }

    @Test(expected = IOException.class)
    public void readFileNotExists() throws IOException {
        String path = "nonexistent-file";
        access.read(path);
    }

    @Test
    public void getAbsolutePathSuccess() throws IOException {
        String filename = "some-source-file";
        File sourceFile = new File(targetDir, filename);
        sourceFile.createNewFile();

        String result = access.getAbsolutePath(filename);
        assertEquals(sourceFile.getAbsolutePath(), result);
    }

    @Test(expected = FileNotFoundException.class)
    public void getAbsolutePathNoSuchFile() throws FileNotFoundException {
        String filename = "nonexistent-file";
        access.getAbsolutePath(filename);
        fail("getAbsolutePath() should have raised FileNotFoundException.");
    }

    @Test
    public void delete() throws IOException {
        String filename = "some-file";
        File file = new File(targetDir, filename);
        file.createNewFile();
        assertTrue(file.exists());

        access.delete(filename);
        assertFalse(file.exists());
    }

    @Test
    public void createFolder() {
        String folder = "some-folder/some-subfolder/some-subsubfolder";
        access.createDirectories(folder);
        File expectedFolder = new File(targetDir, folder);
        assertTrue(expectedFolder.exists());
    }
}
