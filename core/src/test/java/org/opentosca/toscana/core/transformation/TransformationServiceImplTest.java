package org.opentosca.toscana.core.transformation;

import org.junit.Before;
import org.junit.Test;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.TestProfiles;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.dummy.DummyCsar;
import org.opentosca.toscana.core.dummy.ExecutionDummyPlugin;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.core.transformation.properties.RequirementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileNotFoundException;
import java.util.HashSet;

import static org.junit.Assert.*;

@ActiveProfiles(TestProfiles.DUMMY_PLUGIN_SERVICE_TEST)
public class TransformationServiceImplTest extends BaseSpringTest {
    
    @Autowired
    private TransformationService service;
    @Autowired
    private TestCsars testCsars;
    
    private Csar csar;
    
    private ExecutionDummyPlugin passingDummy = TestPlugins.PASSING_DUMMY;
    private ExecutionDummyPlugin failingDummy = TestPlugins.FAILING_DUMMY;



    @Before
    public void setUp() throws FileNotFoundException {
        csar = testCsars.getCsar(TestCsars.CSAR_YAML_VALID_SIMPLETASK);
        
    }
    
    @Test
    public void createTransformation() throws Exception {
        service.createTransformation(csar, TestPlugins.PLATFORM1);
        Transformation expected = new TransformationImpl(csar,TestPlugins.PLATFORM1);
        assertTrue(csar.getTransformations().containsValue(expected));
        
    }

    @Test
    public void testStartTransformationInvalidState() throws Exception {
        service.createTransformation(csar, TestPlugins.PLATFORM1);
        Transformation t = csar.getTransformations().get(TestPlugins.PLATFORM1.id);
        t.setState(TransformationState.ERROR);
        assertTrue(!service.startTransformation(t));
    }

    @Test
    public void testStartTransformationPropertiesNotSet() throws Exception {
        DummyCsar csar = new DummyCsar("test");
        csar.modelSpecificProperties = new HashSet<>();
        csar.modelSpecificProperties
            .add(new Property("test", PropertyType.TEXT, RequirementType.TRANSFORMATION));
        service.createTransformation(csar, passingDummy.getPlatformDetails());
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(!service.startTransformation(t));
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
        DummyCsar csar = new DummyCsar("test");
        csar.modelSpecificProperties = new HashSet<>();
        csar.modelSpecificProperties
            .add(new Property("test", PropertyType.TEXT, RequirementType.TRANSFORMATION));
        service.createTransformation(csar, passingDummy.getPlatformDetails());
        assertTrue(csar.getTransformations().get("passing") != null);
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(t.getState() == TransformationState.INPUT_REQUIRED);
    }




    @Test
    public void testStartTransformationValidState() throws Exception {
        DummyCsar csar = new DummyCsar("test");
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
    
    @Test
    public void deleteTransformation() throws Exception {
        Transformation transformation = new TransformationImpl(csar,TestPlugins.PLATFORM1);
        csar.getTransformations().put(TestPlugins.PLATFORM1.id, transformation);
        service.deleteTransformation(transformation);
        
        assertFalse(csar.getTransformations().containsValue(transformation));
    }

}
