package org.opentosca.toscana.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.core.BaseJUnitTest;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PluginFileAccessTest extends BaseJUnitTest {

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
    public void write() throws Exception {
        access.access(targetFileName).append(fileContent).close();
        assertTrue(targetFile.isFile());
        assertEquals(fileContent, FileUtils.readFileToString(targetFile));
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
}
