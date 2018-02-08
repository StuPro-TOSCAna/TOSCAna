package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.plugin.lifecycle.LifecyclePhase;
import org.opentosca.toscana.core.testdata.TestCsars;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 Tests whether custom node types are handled correctly: The toscana node type definitions must get injected into
 the user service template or else the winery parser will complain (unknown type..)
 */
public class CustomNodeTypeIT extends BaseSpringTest {

    @Autowired
    private CsarService service;

    @Test
    public void customNodeTypeTest() throws FileNotFoundException, CsarIdNotUniqueException {
        File csarFile = TestCsars.VALID_TASKTRANSLATOR;
        InputStream stream = new FileInputStream(csarFile);
        Csar csar = service.submitCsar("csarId", stream);
        LifecyclePhase phase = csar.getLifecyclePhase(Csar.Phase.VALIDATE);
        assertEquals(LifecyclePhase.State.DONE, phase.getState());
    }
}
