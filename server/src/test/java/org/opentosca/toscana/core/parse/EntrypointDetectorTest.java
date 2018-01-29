package org.opentosca.toscana.core.parse;

import java.io.FileNotFoundException;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EntrypointDetectorTest extends BaseSpringTest {

    @Autowired
    private CsarDao csarDao;
    @Autowired
    private TestCsars testCsars;

    @Test
    public void parseValidCsar() throws Exception {
        Csar csar = testCsars.getCsar(TestCsars.VALID_EMPTY_TOPOLOGY);
        new EffectiveModelFactory().create(csar);
    }

    @Test(expected = InvalidCsarException.class)
    public void parseEntrypointMissing() throws FileNotFoundException, InvalidCsarException {
        Csar csar = testCsars.getCsar(TestCsars.INVALID_ENTRYPOINT_MISSING);
        new EffectiveModelFactory().create(csar);
    }

    @Test(expected = InvalidCsarException.class)
    public void parseEntrypointAmbiguous() throws FileNotFoundException, InvalidCsarException {
        Csar csar = testCsars.getCsar(TestCsars.INVALID_ENTRYPOINT_AMBIGUOUS);
        new EffectiveModelFactory().create(csar);
    }
}
