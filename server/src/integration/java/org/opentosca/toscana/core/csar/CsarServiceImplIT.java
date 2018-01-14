package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.opentosca.toscana.core.BaseSpringIntegrationTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogImpl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class CsarServiceImplIT extends BaseSpringIntegrationTest {

    private final String identifier = "my-awesome-csar";

    private CsarService csarService;
    @Autowired
    private CsarDao csarDao;
    private Csar csar;

    @Before
    public void setUp() {
        csarService = new CsarServiceImpl(csarDao);
        Log log = new LogImpl(new File(tmpdir, "log"));
        csar = new CsarImpl(identifier, log);
    }

    @Test
    public void submitCsar() throws Exception {
        File file = TestCsars.VALID_MINIMAL_DOCKER;
        InputStream stream = new FileInputStream(file);
//        when(csarDao.create(identifier, stream)).thenReturn(csar);
//        when(csarDao.getContentDir(csar)).thenReturn(tmpdir);

        Csar result = csarService.submitCsar(identifier, stream);
        assertEquals(csar, result);
    }

    @Test
    public void deleteCsar() throws Exception {
        submitCsar();
        List<Csar> csars = csarService.getCsars();
        assertEquals(1, csars.size());
        csarService.deleteCsar(csar);
        csars = csarService.getCsars();
        assertEquals(0, csars.size());
    }
}
