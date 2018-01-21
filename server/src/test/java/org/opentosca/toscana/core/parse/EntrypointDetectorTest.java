package org.opentosca.toscana.core.parse;

import java.io.FileNotFoundException;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;

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
        new EffectiveModel(csar, csarDao.getContentDir(csar));
    }

    @Test(expected = InvalidCsarException.class)
    public void parseEntrypointMissing() throws FileNotFoundException, InvalidCsarException {
        Csar csar = testCsars.getCsar(TestCsars.INVALID_ENTRYPOINT_MISSING);
        new EffectiveModel(csar, csarDao.getContentDir(csar));
    }

    @Test(expected = InvalidCsarException.class)
    public void parseEntrypointAmbiguous() throws FileNotFoundException, InvalidCsarException {
        Csar csar = testCsars.getCsar(TestCsars.INVALID_ENTRYPOINT_AMBIGUOUS);
        new EffectiveModel(csar, csarDao.getContentDir(csar));
    }
}
