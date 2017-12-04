package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.CsarParseService;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CsarServiceImplTest extends BaseUnitTest {

    private final String identifier = "my-awesome-csar";

    private CsarService csarService;
    @Mock
    private CsarDao csarDao;
    @Mock
    private CsarParseService parseService;
    private Csar csar;

    @Before
    public void setUp() {
        csarService = new CsarServiceImpl(csarDao, parseService);
        csar = new CsarImpl(identifier, mock(Log.class));
    }

    @Test
    public void submitCsar() throws Exception {
        File file = TestCsars.VALID_MINIMAL_DOCKER;
        InputStream stream = new FileInputStream(file);
        when(csarDao.create(identifier, stream)).thenReturn(csar);

        Csar result = csarService.submitCsar(identifier, stream);
        assertEquals(csar, result);
    }

    @Test
    public void deleteCsar() throws Exception {
        csarService.deleteCsar(csar);
        verify(csarDao).delete(csar.getIdentifier());
    }
}
