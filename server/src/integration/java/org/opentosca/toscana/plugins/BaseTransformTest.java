package org.opentosca.toscana.plugins;

import java.io.File;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.toscana.core.testutils.TestUtils.getMockLog;

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
public abstract class BaseTransformTest extends BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTransformTest.class);

    protected final TransformationPlugin plugin;
    protected EffectiveModel model;
    protected TransformationContext context;
    protected File workingDir;
    protected File contentDir;

    /**
     Create a new Test instance using the given plugin
     */
    public BaseTransformTest(TransformationPlugin plugin) {
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

        this.model = getModel();
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
        checkAssumptions();
        logger.info("Starting Transformation");
        try {
            plugin.transform(context);
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
        contentDir = new File(tmpdir, "contentdir");
        workingDir.mkdirs();
        contentDir.mkdirs();
    }

    /**
     initializes the transformation context
     */
    protected TransformationContext initContext() throws Exception {
        return new TransformationContext(contentDir, workingDir, getMockLog(), model, getProperties());
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
    protected abstract PropertyInstance getProperties() throws Exception;
}
