package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CsarFilesystemDaoTest extends BaseSpringTest {

    private final static Logger logger = LoggerFactory.getLogger(CsarFilesystemDaoTest.class.getName());

    @Autowired
    private CsarDao csarDao;

    @Test
    public void create() throws Exception {
        String identifier = "my-csar-checkStateNoPropsSet";
        File csarFile = TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK;
        InputStream csarStream = new FileInputStream(csarFile);
        csarDao.create(identifier, csarStream);
        File csarFolder = new File(tmpdir, identifier);
        File contentFolder = new File(csarFolder, CsarFilesystemDao.CONTENT_DIR);
        File transformationFolder = new File(csarFolder, CsarFilesystemDao.TRANSFORMATION_DIR);
        assertTrue(contentFolder.isDirectory());
        assertTrue(transformationFolder.isDirectory());
        assertTrue(contentFolder.list().length > 3); // not elegant but lazy..
    }

    @Test
    public void delete() throws Exception {
        String identifier = createFakeCsarDirectories(1)[0];
        csarDao.delete(identifier);
        File csarDir = new File(tmpdir, identifier);
        assertFalse(csarDir.exists());
    }

    @Test
    public void find() throws Exception {
        String identifier = createFakeCsarDirectories(1)[0];
        Csar csar = csarDao.find(identifier);
        assertNotNull(csar);
        assertEquals(identifier, csar.getIdentifier());
    }

    @Test
    public void returnedCsarHasPopulatedTransformations() {
        // test whether CsarDao calls TransformationDao internally to populate list of transformations
        String identifier = createFakeCsarDirectories(1)[0];
        File csarDir = new File(tmpdir, identifier);
        File transformationsDir = new File(csarDir, "transformations");
        TestPlugins.createFakeTransformationsOnDisk(transformationsDir, TestPlugins.PLATFORMS);

        Csar csar = csarDao.find(identifier);

        assertEquals(TestPlugins.PLATFORMS.size(), csar.getTransformations().size());
    }

    @Test
    public void findAll() throws Exception {
        int numberOfCsars = 10;
        createFakeCsarDirectories(numberOfCsars);

        List<Csar> csarList = csarDao.findAll();
        assertEquals("Correct amount of csars returned", numberOfCsars, csarList.size());
    }

    private String[] createFakeCsarDirectories(int numberOfCsars) {
        String[] identifiers = new String[10];
        for (int i = 0; i < numberOfCsars; i++) {
            identifiers[i] = "test" + i;
            File fakeApp = new File(tmpdir, identifiers[i]);
            fakeApp.mkdir();
            File fakeContentDir = new File(fakeApp, CsarFilesystemDao.CONTENT_DIR);
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
        File randomFile = new File(tmpdir, simpleFileName);
        randomFile.createNewFile();

        List<Csar> csarList = csarDao.findAll();
        assertEquals("Correct amount of csars returned", numberOfCsars, csarList.size());

        boolean readWrongData = false;
        for (Csar csar : csarList) {
            if (csar.getIdentifier().equals(simpleFileName)) {
                readWrongData = true;
                break;
            }
        }
        assertFalse(readWrongData);
    }
}
