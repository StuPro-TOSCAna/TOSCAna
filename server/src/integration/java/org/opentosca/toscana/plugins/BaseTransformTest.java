package org.opentosca.toscana.plugins;

import java.io.File;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.plugin.lifecycle.ToscanaPlugin;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 This class represents a integration test that tries to transform a Transformation of a model using
 a plugin.
 <p>
 This is intended to execute transformations without initializing the whole core component.
 <p>
 A important note:
 A transformation executed with this test never produces a log file.
 That means: The resulting transformation directory will not contain a log file!
 */
public abstract class BaseTransformTest<LifecycleT extends AbstractLifecycle> extends BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTransformTest.class);

    protected final ToscanaPlugin<LifecycleT> plugin;
    protected EffectiveModel model;
    protected PropertyInstance inputs;
    protected TransformationContext context;
    protected File workingDir;
    protected File contentDir;

    /**
     Create a new Test instance using the given plugin
     */
    public BaseTransformTest(ToscanaPlugin plugin) {
        checkAssumptions();
        this.plugin = plugin;
    }

    /**
     Initilaizes the test environment. This means:
     <ul>
     <li>The Working directory gets created</li>
     <li>The model gets built (<code>getModel()</code> is called)</li>
     <li>The artifacts for the model get copied into the content directory (<code>copyArtifacts()</code> is called)</li>
     <li>The transformation context gets initialized</li>
     </ul>
     */
    @Before
    public void setUp() throws Exception {
        createDirectories();

        model = getModel();
        inputs = getInputs(this.model);
        
        copyArtifacts(contentDir);
        context = initContext();
    }


    /**
     This method is responsible for executing a transformation. The following steps get executed:
     <ol>
     <li><code>transform()</code> gets called on the plugin</li>
     <li>if the Transformation fails (a exception is thrown) <code>onFailure()</code> is called</li>
     <li>if the Transformation succeeds <code>onSuccess()</code> is called.</li>
     </ol>
     Depending on the expected behaviour one of the two methods should contain <code>fail()</code>
     For example if you expect the transformation to pass the <code>onFailure()</code>
     method should call <code>fail()</code>
     */
    @Test
    public void performTransformation() throws Exception {
        logger.info("Starting Transformation");
        try {
            LifecycleT lifecycle = plugin.getInstance(context);
            plugin.transform(lifecycle);
        } catch (Exception e) {
            logger.error("Transformation Failed", e);
            onFailure(workingDir, e);
            return;
        }
        logger.info("Transformation Succeeded");
        onSuccess(workingDir);
    }

    /**
     Creates the content and working directories
     */
    protected void createDirectories() throws Exception {
        workingDir = new File(tmpdir, "workdir");
        contentDir = new File(tmpdir, Csar.CONTENT_DIR);
        workingDir.mkdirs();
        contentDir.mkdirs();
    }

    /**
     initializes the transformation context
     */
    protected TransformationContext initContext() throws Exception {
        Csar csar = new CsarImpl(tmpdir, "csarId", logMock());
        Transformation t = new TransformationImpl(csar, plugin.getPlatform(), logMock(), model);
        Transformation transformation = spy(t);
        when(transformation.getInputs()).thenReturn(inputs);
        return new TransformationContext(transformation, workingDir);
    }

    /**
     This method should be used to copy the model artifacts from the resource folder into the content directory
     <p>
     If the model used does not have any artifacts this can stay empty.
     <p>
     The parameter given is the content directory in witch the files should be stored
     */
    protected void copyArtifacts(File contentDir) throws Exception {
        //Can be implemented by subclasses but is not required!
    }

    /**
     this method gets called at the beginning of the execute transformation
     test it can be used to place JUnit assumptions ath this point
     */
    protected void checkAssumptions() {
        // No assumptions by default
    }

    /**
     This method is intended to build and return the effective model used in this test
     */
    protected abstract EffectiveModel getModel() throws Exception;

    /**
     This method gets called if the transformation passes. it should be used to validate the results in the output
     directory
     */
    protected abstract void onSuccess(File outputDir) throws Exception;

    /**
     This method gets called on failure of the transformation.
     <p>
     In case the failure is not intended this method should just call the <code>fail()</code> method
     */
    protected abstract void onFailure(File outputDir, Exception e) throws Exception;

    /**
     This method is intended to return the properties the "user" has entered. They should be pre defined of course
     */
    protected abstract PropertyInstance getInputs(EffectiveModel model) throws Exception;
}
