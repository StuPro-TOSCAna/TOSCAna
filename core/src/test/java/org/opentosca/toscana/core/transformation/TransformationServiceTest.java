package org.opentosca.toscana.core.transformation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.dummy.DummyCsar;
import org.opentosca.toscana.core.dummy.ExecutionDummyPlugin;
import org.opentosca.toscana.core.plugin.PluginServiceImpl;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.core.transformation.properties.RequirementType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TransformationServiceTest {

    private CsarDao dao;

    private DummyCsar csar = new DummyCsar("test");
    private TransformationServiceImpl service;

    private ExecutionDummyPlugin passingDummy
        = new ExecutionDummyPlugin("passing", false);
    private ExecutionDummyPlugin failingDummy
        = new ExecutionDummyPlugin("failing", true);

    @Before
    public void setUp() throws Exception {
        dao = mock(CsarDao.class);
        when(dao.findAll()).thenReturn(Collections.singletonList(csar));
        service = new TransformationServiceImpl(dao,
            new PluginServiceImpl(Arrays.asList(passingDummy, failingDummy)));
    }

    @Test
    public void transformationCreationNoProps() throws Exception {
        service.createTransformation(csar, passingDummy.getPlatformDetails());
        assertTrue(csar.getTransformations().get("passing") != null);
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(t.getState() == TransformationState.CREATED);
    }

    @Test
    public void transformationCreationInputNeeded() throws Exception {
        csar.modelSpecificProperties = new HashSet<>();
        csar.modelSpecificProperties
            .add(new Property("test", PropertyType.TEXT, RequirementType.TRANSFORMATION));
        service.createTransformation(csar, passingDummy.getPlatformDetails());
        assertTrue(csar.getTransformations().get("passing") != null);
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(t.getState() == TransformationState.INPUT_REQUIRED);
    }


    @Test
    public void testStartTransformationInvalidState() throws Exception {
        service.createTransformation(csar, passingDummy.getPlatformDetails());
        Transformation t = csar.getTransformations().get("passing");
        t.setState(TransformationState.ERROR);
        assertTrue(!service.startTransformation(t));
    }

    @Test
    public void testStartTransformationPropertiesNotSet() throws Exception {
        csar.modelSpecificProperties = new HashSet<>();
        csar.modelSpecificProperties
            .add(new Property("test", PropertyType.TEXT, RequirementType.TRANSFORMATION));
        service.createTransformation(csar, passingDummy.getPlatformDetails());
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(!service.startTransformation(t));
    }

    @Test
    public void testStartTransformationValidState() throws Exception {
        service.createTransformation(csar, passingDummy.getPlatformDetails());
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(service.startTransformation(t));
        Thread.sleep(100);
        waitForTransformationStateChange(t);
        assertTrue(t.getState() == TransformationState.DONE);
    }
    
    @Test
    public void testStartTransformationValidStateExecutionFail() throws Exception {
        service.createTransformation(csar, failingDummy.getPlatformDetails());
        Transformation t = csar.getTransformations().get("failing");
        assertTrue(service.startTransformation(t));
        Thread.sleep(100);
        waitForTransformationStateChange(t);
        assertTrue(t.getState() == TransformationState.ERROR);
    }

    @Test
    public void testExecutionStopWithSleep() throws Exception {
        service.createTransformation(csar, passingDummy.getPlatformDetails());
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(service.startTransformation(t));
        Thread.sleep(100);
        assertTrue(t.getState() == TransformationState.TRANSFORMING);
        assertTrue(service.abortTransformation(t));
        Thread.sleep(100);
        assertTrue("Transformation State is " + t.getState(),
            t.getState() == TransformationState.ERROR);
    }

    @Test
    public void testExecutionStopWhenAlreadyDone() throws Exception {
        //Start a passing transformation
        testStartTransformationValidState();
        //Wait for it to finish
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(!service.abortTransformation(t));
    }

    @Test
    public void testStopNotStarted() throws Exception {
        transformationCreationNoProps();
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(!service.abortTransformation(t));
    }

    private void waitForTransformationStateChange(Transformation t) throws InterruptedException {
        while (t.getState() == TransformationState.TRANSFORMING) {
            Thread.sleep(100);
        }
    }
}
