package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.PlatformService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM1;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM2;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM3;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM_NOT_SUPPORTED;

public class TransformationFilesystemDaoTest extends BaseUnitTest {

    private TransformationDao transformationDao;
    @Mock
    private CsarDao csarDao;
    @Mock
    private PlatformService platformService;
    @Mock
    private Log log;

    private Csar csar1 = new CsarImpl("csar1", log);
    private Transformation transformation;
    private File transformationRootDir;

    @Before
    public void setUp() throws FileNotFoundException {
        transformationDao = new TransformationFilesystemDao(platformService);
        transformationDao.setCsarDao(csarDao);
        transformation = new TransformationImpl(csar1, PLATFORM1, log);
        transformationRootDir = transformationDao.getRootDir(transformation);
    }

    @Test
    public void getRootDir() throws Exception {
        when(csarDao.getTransformationsDir(csar1)).thenReturn(new File(new File(tmpdir, csar1.getIdentifier()),
            CsarFilesystemDao.TRANSFORMATION_DIR));
        when(csarDao.getRootDir(csar1)).thenReturn(new File(tmpdir, csar1.getIdentifier()));
        File expectedParent = new File(csarDao.getRootDir(csar1), CsarFilesystemDao.TRANSFORMATION_DIR);
        File expected = new File(expectedParent, PLATFORM1.id);
        File actual = transformationDao.getRootDir(transformation);

        assertEquals(expected, actual);
    }

    /**
     When creating a new transformation, existing files in the transformation directory must be deleted
     */
    @Test
    public void createDeletesOldFilesAndCreatesBlankDir() throws Exception {
        List<File> files = createRandomFiles(transformationRootDir);

        assertNotEquals(0, transformationRootDir.list().length);
        when(platformService.isSupported(PLATFORM1)).thenReturn(true);
        transformationDao.create(csar1, PLATFORM1);

        for (File file : files) {
            assertFalse(file.exists());
        }
        assertTrue(transformationRootDir.exists());
        File[] result = transformationRootDir.listFiles();
        assertEquals(1, result.length);  // old files deleted, empty content dir got created
        assertEquals(TransformationFilesystemDao.CONTENT_DIR, result[0].getName());
        assertEquals(0, result[0].list().length);
    }

    /**
     tests whether all files in the csar's transformation directory are removed upon transformation deletion
     */
    @Test
    public void delete() throws Exception {
        // create some random files in transformation1 dir of csar1
        createRandomFiles(transformationRootDir);

        transformationDao.delete(transformation);
        // check if all files got deleted 
        assertFalse(transformationRootDir.exists());
    }

    @Test
    public void findFromSpecificCsar() throws Exception {
        when(csarDao.getTransformationsDir(csar1)).thenReturn(tmpdir);
        createRandomFiles(new File(tmpdir, PLATFORM1.id));
        createRandomFiles(new File(tmpdir, PLATFORM2.id));
        createRandomFiles(new File(tmpdir, PLATFORM_NOT_SUPPORTED.id));
        when(platformService.findPlatformById(PLATFORM1.id)).thenReturn(Optional.of(PLATFORM1));
        when(platformService.findPlatformById(PLATFORM2.id)).thenReturn(Optional.of(PLATFORM2));
        List<Transformation> transformations = transformationDao.find(csar1);

        assertEquals(2, transformations.size());
    }

    @Test
    public void findSpecificTransformation() throws Exception {
        when(csarDao.getTransformationsDir(csar1)).thenReturn(tmpdir);
        when(platformService.findPlatformById(PLATFORM1.id)).thenReturn(Optional.of(PLATFORM1));
        when(platformService.findPlatformById(PLATFORM2.id)).thenReturn(Optional.of(PLATFORM2));
        createRandomFiles(new File(tmpdir, PLATFORM1.id));
        createRandomFiles(new File(tmpdir, PLATFORM2.id));

        Transformation transformation = transformationDao.find(csar1, PLATFORM1).get();
        assertNotNull(transformation);
        assertEquals(csar1, transformation.getCsar());
        assertEquals(PLATFORM1, transformation.getPlatform());

        Optional<Transformation> notStoredTransformation = transformationDao.find(csar1, PLATFORM3);
        assertFalse(notStoredTransformation.isPresent());
    }

    /**
     When reading a transformation from disk which has an unknown platform, application shall not crash but remove the
     illegal transformation directory from disk.
     */
    @Test
    public void readTransformationFromDiskWithIllegalPlatform() throws IOException {
        when(csarDao.getTransformationsDir(csar1)).thenReturn(tmpdir);
        Transformation t = new TransformationImpl(csar1, PLATFORM_NOT_SUPPORTED, log);
        createRandomFiles(transformationDao.getRootDir(t));

        assertFalse(transformationDao.find(csar1, PLATFORM_NOT_SUPPORTED).isPresent());
        assertFalse(transformationDao.getRootDir(t).exists());
    }

    /**
     Creates some files in given dir
     */
    private List<File> createRandomFiles(File dir) {
        dir.mkdirs();
        List<File> files = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            File randomFile = new File(dir, String.valueOf(i));
            files.add(randomFile);
            try {
                randomFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return files;
    }
}
