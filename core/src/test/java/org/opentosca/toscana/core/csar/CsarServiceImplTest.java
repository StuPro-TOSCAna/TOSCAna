package org.opentosca.toscana.core.csar;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CsarServiceImplTest extends BaseSpringTest {

    @Autowired
    CsarService csarService;
    private File dataDir;

    @Before
    public void setUp() {
        dataDir = preferences.getDataDir();
    }

    private String identifier = "my-awesome-csar";

    @Test
    public void submitCsar() throws Exception {
        File file = TestCsars.CSAR_YAML_VALID_SIMPLETASK;
        InputStream stream = new FileInputStream(file);
        csarService.submitCsar(identifier, stream);

        File csarDir = new File(dataDir, identifier);
        File contentDir = new File(csarDir, CsarFilesystemDao.CONTENT_DIR);

        assertTrue(csarDir.isDirectory());
        assertTrue(contentDir.isDirectory());
        assertTrue(contentDir.list().length > 3); // lazy..
    }

    @Test
    public void deleteCsar() throws Exception {
        File csarDir = new File(dataDir, identifier);
        File contentDir = new File(csarDir, CsarFilesystemDao.CONTENT_DIR);
        Csar csar = new CsarImpl(identifier);

        csarDir.mkdir();
        assertTrue(csarDir.isDirectory());

        csarService.deleteCsar(csar);

        assertFalse(csarDir.isDirectory());
    }
}
