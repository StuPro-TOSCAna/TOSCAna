package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.dummy.DummyCsar;
import org.opentosca.toscana.core.dummy.ExecutionDummyPlugin;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.testdata.TestPlugins;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactService;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TransformationServiceImplTest extends BaseSpringTest {

    @Autowired
    private TransformationService service;
    @Autowired
    private TestCsars testCsars;
    @Autowired
    private ArtifactService ams;
    @Mock
    private Log log;

    private Csar csar;

    private ExecutionDummyPlugin passingDummy = TestPlugins.PASSING_DUMMY;
    private ExecutionDummyPlugin failingDummy = TestPlugins.FAILING_DUMMY;
    private ExecutionDummyPlugin passingDummyFw = TestPlugins.PASSING_WRITING_DUMMY;
    private ExecutionDummyPlugin failingDummyFw = TestPlugins.FAILING_WRITING_DUMMY;

    @Before
    public void setUp() throws FileNotFoundException {
        csar = testCsars.getCsar(TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
    }

    @Test
    public void createTransformation() throws Exception {
        Transformation transformation = service.createTransformation(csar, TestPlugins.PLATFORM1);
        Transformation expected = new TransformationImpl(csar, TestPlugins.PLATFORM1, log);
        assertTrue(csar.getTransformations().containsValue(expected));
        assertEquals(expected, transformation);
    }

    @Test
    public void startTransformationInvalidState() throws Exception {
        Transformation t = service.createTransformation(csar, TestPlugins.PLATFORM1);
        t.setState(TransformationState.ERROR);
        assertFalse(service.startTransformation(t));
    }

    @Test
    public void startTransformationPropertiesNotSet() throws Exception {
        DummyCsar csar = new DummyCsar("test");
        csar.modelSpecificProperties = new HashSet<>();
        csar.modelSpecificProperties
            .add(new Property("test", PropertyType.TEXT));
        Transformation t = service.createTransformation(csar, passingDummy.getPlatform());
        assertFalse(service.startTransformation(t));
    }

    @Test
    public void transformationCreationNoProps() throws Exception {
        Transformation t = service.createTransformation(csar, passingDummy.getPlatform());
        assertNotNull(csar.getTransformations().get(passingDummy.getPlatform().id));
        assertNotNull(t);
        assertEquals(TransformationState.READY, t.getState());
    }

    @Test(expected = PlatformNotFoundException.class)
    public void transformationCreationPlatformNotFound() throws PlatformNotFoundException {
        service.createTransformation(csar, TestPlugins.PLATFORM_NOT_SUPPORTED);
    }

    @Test
    public void transformationCreationInputNeeded() throws Exception {
        DummyCsar csar = new DummyCsar("test");
        csar.modelSpecificProperties = new HashSet<>();
        csar.modelSpecificProperties
            .add(new Property("test", PropertyType.TEXT));
        Transformation t = service.createTransformation(csar, passingDummy.getPlatform());
        assertNotNull(csar.getTransformations().get(passingDummy.getPlatform().id));
        assertNotNull(t);
        assertEquals(TransformationState.INPUT_REQUIRED, t.getState());
    }

    @Test
    public void startTransformation() throws Exception {
        Transformation t = startTransformationInternal(TransformationState.DONE, passingDummy.getPlatform());
        assertNotNull(t);
    }

    @Test
    public void startTransformationExecutionFail() throws Exception {
        startTransformationInternal(TransformationState.ERROR, failingDummy.getPlatform());
    }

    @Test
    public void startTransformationWithArtifacts() throws Exception {
        Transformation transformation = startTransformationInternal(TransformationState.DONE, passingDummyFw.getPlatform());
        lookForArtifactArchive(transformation);
    }

    @Test
    public void startTransformationWithArtifactsExecutionFail() throws Exception {
        startTransformationInternal(TransformationState.ERROR, failingDummyFw.getPlatform());
    }

    @Test
    public void executionStopWithSleep() throws Exception {
        Transformation t = service.createTransformation(csar, passingDummy.getPlatform());
        assertTrue(service.startTransformation(t));
        letTimePass();
        assertTrue(t.getState() == TransformationState.TRANSFORMING);
        assertTrue(service.abortTransformation(t));
        letTimePass();
        assertTrue("Transformation State is " + t.getState(),
            t.getState() == TransformationState.ERROR);
    }

    @Test
    public void executionStopWhenAlreadyDone() throws Exception {
        //Start a passing transformation
        startTransformation();
        //Wait for it to finish
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(!service.abortTransformation(t));
    }

    @Test
    public void stopNotStarted() throws Exception {
        transformationCreationNoProps();
        Transformation t = csar.getTransformations().get("passing");
        assertTrue(!service.abortTransformation(t));
    }

    @Test
    public void deleteTransformation() throws Exception {
        Transformation transformation = new TransformationImpl(csar, TestPlugins.PLATFORM1, log);
        csar.getTransformations().put(TestPlugins.PLATFORM1.id, transformation);
        service.deleteTransformation(transformation);

        assertFalse(csar.getTransformations().containsValue(transformation));
    }

    private Transformation startTransformationInternal(TransformationState expectedState, Platform platform) throws InterruptedException, FileNotFoundException, PlatformNotFoundException {
        Csar csar = testCsars.getCsar(TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK);
        Transformation t = service.createTransformation(csar, platform);
        assertTrue(service.startTransformation(t));
        letTimePass();
        waitForTransformationStateChange(t);
        assertEquals(expectedState, t.getState());
        return t;
    }

    private void waitForTransformationStateChange(Transformation t) throws InterruptedException {
        while (t.getState() == TransformationState.TRANSFORMING) {
            letTimePass();
        }
    }

    public void lookForArtifactArchive(Transformation transformation) {
        String filename = transformation.getCsar().getIdentifier() + "-" + transformation.getPlatform().id + "_";
        boolean found = false;
        for (File file : ams.getArtifactDir().listFiles()) {
            if (file.getName().startsWith(filename)) {
                found = true;
                break;
            }
        }
        assertTrue("Could not find artifact ZIP in Folder", found);
    }

    private void letTimePass() throws InterruptedException {
        Thread.sleep(25);
    }
}
