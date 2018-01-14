package org.opentosca.toscana.core.transformation;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.testdata.TestPlugins.FAILING_DUMMY;
import static org.opentosca.toscana.core.testdata.TestPlugins.FAILING_WRITING_DUMMY;
import static org.opentosca.toscana.core.testdata.TestPlugins.PASSING_DUMMY;
import static org.opentosca.toscana.core.testdata.TestPlugins.PASSING_WRITING_DUMMY;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM1;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM_NOT_SUPPORTED;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM_PASSING_DUMMY;

public class TransformationServiceImplTest extends BaseSpringTest {

    private static final int WAIT_DELAY_MS = 100;
    private static final int TEST_EXECUTION_TIMEOUT_MS = 10000;

    private static final Logger logger = LoggerFactory.getLogger(TransformationServiceImplTest.class);
    @Autowired
    private TransformationService service;
    @Autowired
    private TestCsars testCsars;
    @Mock
    private Log log;

    private Csar csar;

    @Before
    public void setUp() throws FileNotFoundException {
        csar = testCsars.getCsar(TestCsars.VALID_MINIMAL_DOCKER);
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void createTransformation() throws Exception {
        Transformation expected = new TransformationImpl(csar, PLATFORM1, log);
        Transformation transformation = service.createTransformation(csar, PLATFORM1);
        assertTrue(csar.getTransformation(PLATFORM1.id).isPresent());
        assertEquals(expected, transformation);
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void startTransformationInvalidState() throws Exception {
        Transformation t = service.createTransformation(csar, PLATFORM1);
        t.setState(TransformationState.ERROR);
        assertFalse(service.startTransformation(t));
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void startTransformationPropertiesNotSet() throws Exception {
        Transformation t = service.createTransformation(csar, PASSING_DUMMY.getPlatform());
        t = spy(t);
        when(t.getState()).thenReturn(TransformationState.INPUT_REQUIRED);
        when(t.allRequiredPropertiesSet()).thenReturn(false);
        assertFalse(service.startTransformation(t));
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void transformationCreationNoProps() throws Exception {
        Transformation t = service.createTransformation(csar, PASSING_DUMMY.getPlatform());
        assertTrue(csar.getTransformation(PASSING_DUMMY.getPlatform().id).isPresent());
        assertNotNull(t);
        assertEquals(TransformationState.READY, t.getState());
    }

    @Test(expected = PlatformNotFoundException.class)
    public void transformationCreationPlatformNotFound() throws PlatformNotFoundException {
        service.createTransformation(csar, PLATFORM_NOT_SUPPORTED);
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void transformationCreationInputNeeded() throws Exception {
        Csar csar = spy(this.csar);

        //Generate Mock Property
        Set<Property> propSet = new HashSet<>();
        propSet.add(new Property("mock_prop", PropertyType.NAME));

        when(csar.getModelSpecificProperties()).thenReturn(propSet);

        Transformation t = service.createTransformation(csar, PLATFORM_PASSING_DUMMY);
        assertTrue(csar.getTransformation(PLATFORM_PASSING_DUMMY.id).isPresent());
        assertNotNull(t);
        assertEquals(TransformationState.INPUT_REQUIRED, t.getState());
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void startTransformation() throws Exception {
        Transformation t = startTransformationInternal(TransformationState.DONE, PASSING_DUMMY.getPlatform());
        assertNotNull(t);
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void startTransformationExecutionFail() throws Exception {
        startTransformationInternal(TransformationState.ERROR, FAILING_DUMMY.getPlatform());
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void startTransformationWithArtifacts() throws Exception {
        Transformation transformation = startTransformationInternal(TransformationState.DONE, PASSING_WRITING_DUMMY.getPlatform());
        Optional<TargetArtifact> targetArtifactOptional = transformation.getTargetArtifact();
        assertTrue(targetArtifactOptional.isPresent());
        TargetArtifact targetArtifact = targetArtifactOptional.get();
        assertFalse(targetArtifact.name.matches(TransformationFilesystemDao.ARTIFACT_FAILED_REGEX));
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void startTransformationWithArtifactsExecutionFail() throws Exception {
        startTransformationInternal(TransformationState.ERROR, FAILING_WRITING_DUMMY.getPlatform());
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void executionStopWithSleep() throws Exception {
        Transformation t = service.createTransformation(csar, PASSING_DUMMY.getPlatform());
        assertTrue(service.startTransformation(t));
        waitForTransformationStateChange(t, TransformationState.TRANSFORMING);
        assertEquals(TransformationState.TRANSFORMING, t.getState());
        assertTrue(service.abortTransformation(t));
        letTimePass();
        assertEquals("Transformation State is " + t.getState(),
            TransformationState.ERROR, t.getState());
    }

//    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
//    public void executionStopWhenAlreadyDone() throws Exception {
//        //Start a passing transformation
//        startTransformation();
//        //Wait for it to finish
//        Transformation t = csar.getTransformation(PASSING_DUMMY.getPlatform().id).get();
//        waitForTransformationStateChange(t, TransformationState.DONE);
//        assertFalse(service.abortTransformation(t));
//    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void stopNotStarted() throws Exception {
        transformationCreationNoProps();
        Transformation t = csar.getTransformations().get(PASSING_DUMMY.getPlatform().id);
        assertFalse(service.abortTransformation(t));
    }

    @Test(timeout = TEST_EXECUTION_TIMEOUT_MS)
    public void deleteTransformation() throws Exception {
        Transformation transformation = new TransformationImpl(csar, PLATFORM1, log);
        csar.getTransformations().put(PLATFORM1.id, transformation);
        service.deleteTransformation(transformation);

        assertFalse(csar.getTransformations().containsValue(transformation));
    }

    private Transformation startTransformationInternal(TransformationState expectedState, Platform platform) throws InterruptedException, FileNotFoundException, PlatformNotFoundException {
        csar = testCsars.getCsar(TestCsars.VALID_MINIMAL_DOCKER);
        Transformation t = service.createTransformation(csar, platform);
        assertTrue(service.startTransformation(t));
        letTimePass();
        waitForTransformationStateChange(t, expectedState);
        assertEquals(expectedState, t.getState());
        return t;
    }

    private void waitForTransformationStateChange(Transformation t, TransformationState expected) throws InterruptedException {
        while (t.getState() != expected) {
            logger.debug("Current state {}", t.getState());
            letTimePass();
        }
    }

    private void letTimePass() throws InterruptedException {
        Thread.sleep(WAIT_DELAY_MS);
    }
}
