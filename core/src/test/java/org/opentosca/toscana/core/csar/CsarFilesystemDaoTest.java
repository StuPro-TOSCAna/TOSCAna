package org.opentosca.toscana.core.csar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.toscana.core.TestConfiguration;
import org.opentosca.toscana.core.util.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= TestConfiguration.class)
public class CsarFilesystemDaoTest {

    @Autowired
    private Preferences preferences;
    @Autowired
    private CsarDao csarDao;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void create() throws Exception {
        String identifier = "my-csar-name";
        File csarFile = new File("src/test/resources/Moodle.csar");
        InputStream csarStream = new FileInputStream(csarFile);
        csarDao.create(identifier, csarStream);
        File csarFolder = new File(preferences.getDataDir(), identifier + ".csar");
        assertTrue(csarFolder.exists());

    }

    @Test
    public void delete() throws Exception {
    }

    @Test
    public void find() throws Exception {
    }

    @Test
    public void findAll() throws Exception {
    }

}