package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.util.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CsarFilesystemDaoTest extends BaseUnitTest {

    private CsarFilesystemDao csarDao;
    @Mock
    private TransformationDao transformationDao;
    @Mock
    private Preferences preferences;
    private File generalCsarsDir;

    @Before
    public void setUp() {
        when(preferences.getDataDir()).thenReturn(tmpdir);
        csarDao = new CsarFilesystemDao(preferences, transformationDao);
        csarDao.init();
        generalCsarsDir = new File(tmpdir, CsarFilesystemDao.CSARS_DIR);
    }

    @Test
    public void create() throws Exception {
        String identifier = "my-csar-checkStateNoPropsSet";
        File csarFile = TestCsars.VALID_MINIMAL_DOCKER;
        InputStream csarStream = new FileInputStream(csarFile);
        csarDao.create(identifier, csarStream);
        File csarFolder = new File(generalCsarsDir, identifier);
        File contentFolder = new File(csarFolder, CsarImpl.CONTENT_DIR);
        File transformationFolder = new File(csarFolder, CsarFilesystemDao.TRANSFORMATION_DIR);
        assertTrue(contentFolder.isDirectory());
        assertTrue(transformationFolder.isDirectory());
        assertTrue(contentFolder.list().length == 1); // not elegant but lazy..
    }

    @Test
    public void deleteCsarRemovesDataOnDisk() throws Exception {
        String identifier = createFakeCsarDirectories(1)[0];
        csarDao.delete(identifier);
        File csarDir = new File(generalCsarsDir, identifier);
        assertFalse(csarDir.exists());
    }

    @Test
    public void deleteCsarRemovesCsarFromCache() {
        String identifier = createFakeCsarDirectories(1)[0];
        csarDao = new CsarFilesystemDao(preferences, transformationDao);
        csarDao.init(); // reads from filesystem
        Optional<Csar> csar = csarDao.find(identifier);
        assertTrue(csar.isPresent());
        csarDao.delete(identifier);
        csar = csarDao.find(identifier);
        assertFalse(csar.isPresent());
    }

    @Test
    public void find() {
        String identifier = createFakeCsarDirectories(1)[0];
        // create new CsarDao -- disk is initialized only on startup
        csarDao = new CsarFilesystemDao(preferences, transformationDao);
        csarDao.init();
        Optional<Csar> csar = csarDao.find(identifier);
        assertTrue(csar.isPresent());
        assertEquals(identifier, csar.get().getIdentifier());
    }

    @Test
    public void returnedCsarHasPopulatedTransformations() {
        // test whether CsarDao calls TransformationDao internally to populate list of transformations
        String identifier = createFakeCsarDirectories(1)[0];
        Csar csar = new CsarImpl(new File(""), identifier, logMock());

        csarDao = new CsarFilesystemDao(preferences, transformationDao);
        List<Transformation> transformations = TestPlugins.PLATFORMS.stream()
            .map(platform -> new TransformationImpl(csar, platform, logMock(), modelMock()))
            .collect(Collectors.toList());
        when(transformationDao.find(any())).thenReturn(transformations);
        csarDao.init();
        Optional<Csar> result = csarDao.find(identifier);

        assertTrue(result.isPresent());
        assertEquals(TestPlugins.PLATFORMS.size(), result.get().getTransformations().size());
    }

    @Test
    public void findAll() {
        int numberOfCsars = 10;
        createFakeCsarDirectories(numberOfCsars);

        csarDao = new CsarFilesystemDao(preferences, transformationDao);
        csarDao.init();
        List<Csar> csarList = csarDao.findAll();
        assertEquals("Correct amount of csars returned", numberOfCsars, csarList.size());
    }

    private String[] createFakeCsarDirectories(int numberOfCsars) {
        String[] identifiers = new String[10];
        for (int i = 0; i < numberOfCsars; i++) {
            identifiers[i] = "test" + i;
            File fakeApp = new File(generalCsarsDir, identifiers[i]);
            fakeApp.mkdir();
            File fakeContentDir = new File(fakeApp, CsarImpl.CONTENT_DIR);
            fakeContentDir.mkdir();
            File fakeTransformationDir = new File(fakeApp, CsarFilesystemDao.TRANSFORMATION_DIR);
            fakeTransformationDir.mkdir();
        }
        return identifiers;
    }

    /**
     Test whether only directories in the DATA_ROOT dir are used to instantiate a csar object
     */
    @Test
    public void findAllOnlyDirs() throws Exception {
        int numberOfCsars = 10;
        createFakeCsarDirectories(numberOfCsars);
        // create some random file in data_dir
        String simpleFileName = "randomFile";
        File randomFile = new File(generalCsarsDir, simpleFileName);
        randomFile.createNewFile();

        csarDao = new CsarFilesystemDao(preferences, transformationDao);
        csarDao.init();
        List<Csar> csarList = csarDao.findAll();
        assertEquals("Correct amount of csars returned", numberOfCsars, csarList.size());

        csarList.stream().forEach(csar -> assertNotEquals(simpleFileName, csar.getIdentifier()));
    }
}
