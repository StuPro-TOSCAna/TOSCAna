package org.opentosca.toscana.core.parse;

import java.io.FileNotFoundException;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class CsarParseServiceImplTest extends BaseSpringTest {

    @Autowired
    private CsarParseService csarParser;
    @Autowired
    private TestCsars testCsars;

    @Test
    public void parseValidCsar() throws Exception {
        Csar csar = testCsars.getCsar(TestCsars.VALID_EMPTY_TOPOLOGY);
        EffectiveModel model = csarParser.parse(csar);
        assertNotNull(model);
    }

    @Test(expected = InvalidCsarException.class)
    public void parseEntrypointMissing() throws FileNotFoundException, InvalidCsarException {
        Csar csar = testCsars.getCsar(TestCsars.INVALID_ENTRYPOINT_MISSING);
        csarParser.parse(csar);
    }

    @Test(expected = InvalidCsarException.class)
    public void parseEntrypointAmbiguous() throws FileNotFoundException, InvalidCsarException {
        Csar csar = testCsars.getCsar(TestCsars.INVALID_ENTRYPOINT_AMBIGUOUS);
        csarParser.parse(csar);
    }
}
