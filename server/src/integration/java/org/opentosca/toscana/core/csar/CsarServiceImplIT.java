package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.opentosca.toscana.core.BaseSpringIntegrationTest;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogImpl;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CsarServiceImplIT extends BaseSpringIntegrationTest {

    private final String identifier = "my-awesome-csar";

    private CsarService csarService;
    @Autowired
    private CsarDao csarDao;
    private Csar csar;

    @Before
    public void setUp() throws InvalidCsarException {
        EffectiveModelFactory modelFactory = mock(EffectiveModelFactory.class);
        EffectiveModel model = modelMock();
        when(modelFactory.create(any(Csar.class))).thenReturn(model);
        csarService = new CsarServiceImpl(csarDao);
        Log log = new LogImpl(new File(tmpdir, "log"));
        csar = new CsarImpl(new File(""), identifier, log);
    }

    @Test
    public void submitCsar() throws Exception {
        File file = TestCsars.VALID_MINIMAL_DOCKER;
        InputStream stream = new FileInputStream(file);

        Csar result = csarService.submitCsar(identifier, stream);
        assertEquals(csar, result);
    }

    @Test(expected = CsarIdNotUniqueException.class)
    public void submitCsarTwice() throws FileNotFoundException, CsarIdNotUniqueException {
        File file = TestCsars.VALID_EMPTY_TOPOLOGY;
        InputStream stream = new FileInputStream(file);

        try {
            csarService.submitCsar(identifier, stream);
        } catch (CsarIdNotUniqueException e) {
            e.printStackTrace();
            fail();
        }
        stream = new FileInputStream(file);
        csarService.submitCsar(identifier, stream);
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
