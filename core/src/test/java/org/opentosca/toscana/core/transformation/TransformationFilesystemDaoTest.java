package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.testutils.TestCategory;
import org.opentosca.toscana.core.testutils.TestCategories;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.*;

@ActiveProfiles("dummy_plugins")
public class TransformationFilesystemDaoTest extends BaseSpringTest {

    @Autowired
    private TestCsars testCsars;
    @Autowired
    private TransformationDao transformationDao;
    @Autowired
    private CsarDao csarDao;

    private Csar csar1;
    private Csar csar2;
    private Csar csar3;
    private Csar csar4;
    private Transformation transformation1;
    private File transformation1Dir;

    @Before
    public void setUp() throws FileNotFoundException {
        csar1 = testCsars.getCsar("csar1", TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        csar2 = testCsars.getCsar("csar2", TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        csar3 = testCsars.getCsar("csar3", TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        csar4 = testCsars.getCsar("csar4", TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        transformation1 = new TransformationImpl(csar1, TestPlugins.PLATFORM1);
        transformation1Dir = transformationDao.getRootDir(transformation1);
    }

    @Test
    public void getRootDir() throws Exception {
        File expectedParent = new File(csarDao.getRootDir(csar1), CsarFilesystemDao.TRANSFORMATION_DIR);
        File expected = new File(expectedParent, TestPlugins.PLATFORM1.id);
        File actual = transformationDao.getRootDir(transformation1);

        assertEquals(expected, actual);
    }

    /**
     * When creating a new transformation, existing files in the transformation directory must be deleted
     */
    @Test
    public void createDeletesOldFilesAndCreatesBlankDir() throws Exception {
        List<File> files = createRandomFiles(transformation1Dir);

        transformationDao.create(csar1, TestPlugins.PLATFORM1);

        for (File file : files) {
            assertFalse(file.exists());
        }
        assertTrue(transformation1Dir.exists());
        assertEquals(0, transformation1Dir.list().length);
    }

    /**
     * tests whether all files in the csar's transformation directory are removed upon transformation deletion
     */
    @Test
    public void delete() throws Exception {
        // create some random files in transformation1 dir of csar1
        createRandomFiles(transformation1Dir);

        transformationDao.delete(transformation1);
        // check if all files got deleted 
        assertFalse(transformation1Dir.exists());
    }

    @Test
    public void findFromSpecificCsar() throws Exception {
        createRandomFiles(transformationDao.getRootDir(new TransformationImpl(csar1, TestPlugins.PLATFORM1)));
        createRandomFiles(transformationDao.getRootDir(new TransformationImpl(csar1, TestPlugins.PLATFORM2)));
        createRandomFiles(transformationDao.getRootDir(new TransformationImpl(csar2, TestPlugins.PLATFORM1)));
        createRandomFiles(transformationDao.getRootDir(new TransformationImpl(csar3, TestPlugins.PLATFORM1)));

        List<Transformation> transformations = transformationDao.find(csar1);

        assertEquals(2, transformations.size());
    }

    @Test
    public void findSpecificTransformation() throws Exception {
        createRandomFiles(transformationDao.getRootDir(new TransformationImpl(csar1, TestPlugins.PLATFORM1)));
        createRandomFiles(transformationDao.getRootDir(new TransformationImpl(csar1, TestPlugins.PLATFORM2)));
        createRandomFiles(transformationDao.getRootDir(new TransformationImpl(csar2, TestPlugins.PLATFORM1)));
        createRandomFiles(transformationDao.getRootDir(new TransformationImpl(csar3, TestPlugins.PLATFORM1)));

        Transformation transformation = transformationDao.find(csar2, TestPlugins.PLATFORM1).get();

        assertNotNull(transformation);
        assertEquals(csar2, transformation.getCsar());
        assertEquals(TestPlugins.PLATFORM1, transformation.getPlatform());

        Optional<Transformation> notStoredTransformation = transformationDao.find(csar4, TestPlugins.PLATFORM4);
        assertFalse(notStoredTransformation.isPresent());
    }

    /**
     * When reading a transformation from disk which has an unknown platform, application shall not crash but remove the
     * illegal transformation directory from disk.
     */
    @Test
    public void readTransformationFromDiskWithIllegalPlatform() throws IOException {
        Transformation t = new TransformationImpl(csar1, TestPlugins.PLATFORM_NOT_SUPPORTED);
        createRandomFiles(transformationDao.getRootDir(t));

        assertFalse(transformationDao.find(csar1, TestPlugins.PLATFORM_NOT_SUPPORTED).isPresent());
        assertFalse(transformationDao.getRootDir(t).exists());
    }

    /**
     * Creates some files in given dir
     */
    private List<File> createRandomFiles(File dir) throws IOException {
        dir.mkdir();
        List<File> files = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            File randomFile = new File(dir, String.valueOf(i));
            files.add(randomFile);
            randomFile.createNewFile();
        }
        return files;
    }
}
