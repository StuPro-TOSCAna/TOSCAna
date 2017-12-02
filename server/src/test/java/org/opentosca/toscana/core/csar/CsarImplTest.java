package org.opentosca.toscana.core.csar;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM1;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM2;

public class CsarImplTest extends BaseUnitTest {

    private Csar csar;
    @Mock
    private Log log;
    @Mock
    private Transformation transformation1;
    @Mock
    private Transformation transformation2;

    private List<Transformation> transformations = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        csar = new CsarImpl("csarIdentifier", log);
        transformations.add(transformation1);
        transformations.add(transformation2);
        when(transformation1.getPlatform()).thenReturn(PLATFORM1);
        when(transformation2.getPlatform()).thenReturn(PLATFORM2);
        csar.setTransformations(transformations);
    }

    @Test
    public void getTransformationsForSpecificPlatform() throws Exception {
        Optional<Transformation> result = csar.getTransformation(PLATFORM1.id);
        assertTrue(result.isPresent());
        assertEquals(transformation1, result.get());
    }
}
