package org.opentosca.toscana.core.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class PluginFileAccessTest extends BaseSpringTest {

    private PluginFileAccess access;
    private Csar csar;
    private Transformation transformation;
    private File csarContentDir;
    private File transformationRootDir;
    @Autowired
    private TestCsars testCsars;
    @Autowired
    private CsarDao csarDao;
    @Autowired
    private TransformationDao transformationDao;
    @Autowired
    private TransformationService transformationService;

    private String streamContent;
    private String streamTargetRelativePath;
    private InputStream inputStream;
    private File streamTargetFile;

    @Before
    public void setUp() throws IOException, PlatformNotFoundException {
        csar = testCsars.getCsar(TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        csarContentDir = csarDao.getContentDir(csar);
        transformation = transformationService.createTransformation(csar, TestPlugins.PLATFORM1);
        TestPlugins.createFakeTransformationsOnDisk(csarDao.getTransformationsDir(csar), TestPlugins.PLATFORMS);
        transformationRootDir = transformationDao.getRootDir(transformation);
        access = new PluginFileAccess(transformation, csarDao.getContentDir(csar), transformationDao.getRootDir(transformation));
        streamContent = "this is a test content";
        inputStream = IOUtils.toInputStream(streamContent, "UTF-8");
        streamTargetRelativePath = "testfile";
        streamTargetFile = new File(transformationRootDir, streamTargetRelativePath);
    }

    @Test
    public void copyFile() throws Exception {
        String filename = "testfile";
        File file = new File(csarContentDir, "testfile");
        file.createNewFile();

        File expectedFile = new File(transformationDao.getRootDir(transformation), filename);
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

        File expectedDir = new File(transformationRootDir, dirname);
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
        access.access(streamTargetRelativePath).append(streamContent).close();
        assertTrue(streamTargetFile.isFile());
        assertEquals(streamContent, FileUtils.readFileToString(streamTargetFile));
    }

    @Test(expected = IOException.class)
    public void writePathIsDirectoryThrowsException() throws IOException {
        streamTargetFile.mkdir();
        access.access(streamTargetRelativePath);
    }

    @Test
    public void writeSubDirectoriesGetAutomaticallyCreated() throws IOException {
        String path = "test/some/subdirs/filename";
        access.access(path).append(streamContent).close();
        File targetFile = new File(transformationRootDir, path);
        assertTrue(targetFile.isFile());
        assertEquals(streamContent, FileUtils.readFileToString(targetFile));
    }

    @Test
    public void readSuccessful() throws IOException {
        String path = "file";
        File file = new File(csarContentDir, path);
        FileUtils.copyInputStreamToFile(inputStream, file);
        String result = access.read(path);

        assertNotNull(result);

        assertEquals(streamContent, result);
    }

    @Test(expected = IOException.class)
    public void readFileNotExists() throws IOException {
        String path = "nonexistent-file";
        access.read(path);
    }
}
