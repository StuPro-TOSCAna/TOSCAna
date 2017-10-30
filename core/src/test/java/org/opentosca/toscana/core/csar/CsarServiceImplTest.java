package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CsarServiceImplTest extends BaseSpringTest {

    @Autowired
    private CsarService csarService;

    private final String identifier = "my-awesome-csar";

    @Test
    public void submitCsar() throws Exception {
        File file = TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK;
        InputStream stream = new FileInputStream(file);
        csarService.submitCsar(identifier, stream);

        File csarDir = new File(tmpdir, identifier);
        File contentDir = new File(csarDir, CsarFilesystemDao.CONTENT_DIR);

        assertTrue(csarDir.isDirectory());
        assertTrue(contentDir.isDirectory());
        assertTrue(contentDir.list().length > 3); // lazy..
    }

    @Test
    public void deleteCsar() throws Exception {
        File csarDir = new File(tmpdir, identifier);
        Csar csar = new CsarImpl(identifier, mock(Log.class));

        csarDir.mkdir();
        assertTrue(csarDir.isDirectory());

        csarService.deleteCsar(csar);
        assertFalse(csarDir.isDirectory());
    }
}
