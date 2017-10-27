package org.opentosca.toscana.core.util.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.dummy.DummyCsar;
import org.opentosca.toscana.core.dummy.DummyTransformation;
import org.opentosca.toscana.core.testutils.CategoryAwareSpringRunner;
import org.opentosca.toscana.core.testutils.TestCategory;
import org.opentosca.toscana.core.testutils.TestCategories;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(CategoryAwareSpringRunner.class)
@ContextConfiguration
public class StatusServiceTest {

    @MockBean
    public CsarDao dao;

    public StatusService statusService;

    private DummyCsar testCsar = new DummyCsar("test");
    private DummyCsar test2Csar = new DummyCsar("tezt");

    @Before
    public void setUp() throws Exception {
        when(dao.findAll()).thenReturn(Arrays.asList(test2Csar, testCsar));
        statusService = new StatusServiceImpl(dao);
        for (int i = 0; i < 10; i++) {
            for (DummyCsar c : new DummyCsar[]{test2Csar, testCsar}) {
                c.getTransformations().put("p-" + i,
                    new DummyTransformation(
                        new Platform("p", "p", new HashSet<>()),
                        TransformationState.CREATED)
                );
            }
        }
    }

    @Test
    public void testIdleState() throws Exception {
        assertTrue(statusService.getSystemStatus() == SystemStatus.IDLE);
    }

    @Test
    public void testErroredState() throws Exception {
        testCsar.getTransformations().get("p-1").setState(TransformationState.ERROR);
        assertTrue(statusService.getSystemStatus() == SystemStatus.ERROR);
    }

    @Test
    public void testErroredAndTransformingState() throws Exception {
        testCsar.getTransformations().get("p-1").setState(TransformationState.TRANSFORMING);
        testCsar.getTransformations().get("p-2").setState(TransformationState.ERROR);
        assertTrue(statusService.getSystemStatus() == SystemStatus.ERROR);
    }

    @Test
    public void testTransformingState() throws Exception {
        testCsar.getTransformations().get("p-1").setState(TransformationState.TRANSFORMING);
        assertTrue(statusService.getSystemStatus() == SystemStatus.TRANSFORMING);
    }
}
