package org.opentosca.toscana.core.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationService;

import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PluginFileAccessTest extends BaseSpringTest {

    private PluginFileAccess access;
    private Transformation transformation;
    private File csarContentDir;
    private File transformationContentDir;
    @Autowired
    private TestCsars testCsars;
    @Autowired
    private CsarDao csarDao;
    @Autowired
    private TransformationDao transformationDao;
    @Autowired
    private TransformationService transformationService;

    private String fileContent;
    private String targetFilePath;
    private InputStream inputStream;
    private File targetFile;

    @Before
    public void setUp() throws IOException, PlatformNotFoundException {
        Csar csar = testCsars.getCsar(TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        csarContentDir = csarDao.getContentDir(csar);
        transformation = transformationService.createTransformation(csar, TestPlugins.PLATFORM1);
        TestPlugins.createFakeTransformationsOnDisk(csarDao.getTransformationsDir(csar), TestPlugins.PLATFORMS);
        transformationContentDir = transformationDao.getContentDir(transformation);
        access = new PluginFileAccess(csarDao.getContentDir(csar), transformationDao.getContentDir(transformation),
            transformation.getLog());
        fileContent = "this is a test content";
        inputStream = IOUtils.toInputStream(fileContent, "UTF-8");
        targetFilePath = "testFile";
        targetFile = new File(transformationContentDir, targetFilePath);
    }

    @Test
    public void copyFile() throws Exception {
        String filename = "testFile";
        File file = new File(csarContentDir, "testFile");
        file.createNewFile();

        File expectedFile = new File(transformationDao.getContentDir(transformation), filename);
        assertFalse(expectedFile.exists());
        access.copy(filename);
        assertTrue(expectedFile.exists());
    }

    @Test
    public void copyDirRecursively() throws IOException {
        String dirname = "dir";
        File dir = new File(csarContentDir, dirname);
        dir.mkdir();
        for (int i = 0; i < 10; i++) {
            new File(dir, String.valueOf(i)).createNewFile();
        }

        File expectedDir = new File(transformationContentDir, dirname);
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
        access.access(targetFilePath).append(fileContent).close();
        assertTrue(targetFile.isFile());
        assertEquals(fileContent, FileUtils.readFileToString(targetFile));
    }

    @Test(expected = IOException.class)
    public void writePathIsDirectoryThrowsException() throws IOException {
        targetFile.mkdir();
        access.access(targetFilePath);
    }

    @Test
    public void writeSubDirectoriesGetAutomaticallyCreated() throws IOException {
        String path = "test/some/subdirs/filename";
        access.access(path).append(fileContent).close();
        File targetFile = new File(transformationContentDir, path);
        assertTrue(targetFile.isFile());
        assertEquals(fileContent, FileUtils.readFileToString(targetFile));
    }

    @Test
    public void readSuccessful() throws IOException {
        String path = "file";
        File file = new File(csarContentDir, path);
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
