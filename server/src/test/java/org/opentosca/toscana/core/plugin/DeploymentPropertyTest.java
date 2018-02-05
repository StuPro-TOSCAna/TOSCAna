package org.opentosca.toscana.core.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationImpl;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class DeploymentPropertyTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentPropertyTest.class);

    private static final Platform NOT_SUPPORTED =
        new Platform("deployment_off", "Deployment not Supported");
    private static final Platform SUPPORTED =
        new Platform("deployment_on", "Deployment Supported", true, new HashSet<>());

    private Platform platform;
    private String input;
    private boolean expected;
    private Class<? extends Exception> expectedException;

    public DeploymentPropertyTest(
        String name,
        Platform platform,
        String input,
        boolean expected,
        Class<? extends Exception> expectedExcpetion
    ) {
        //This only exists to suppress codacy warnings!
        logger.debug("Running Test: '{}'", name);
        this.platform = platform;
        this.input = input;
        this.expected = expected;
        this.expectedException = expectedExcpetion;
    }

    @Test
    public void check() {
        try {
            File input = new File(this.tmpdir, "in");
            File output = new File(this.tmpdir, "out");
            PropertyInstance instance = new PropertyInstance(new HashSet<>(platform.properties), mock(Transformation.class));
            if (this.input != null) {
                instance.set(Platform.DEPLOY_AFTER_TRANSFORMATION_KEY, this.input);
            }
            Csar csar = new CsarImpl(input, "csarId", logMock());
            Transformation t = new TransformationImpl(csar, platform, logMock(), mock(EffectiveModel.class));
            Transformation transformation = spy(t);
            when(transformation.getInputs()).thenReturn(instance);
            TransformationContext context = new TransformationContext(transformation, output);

            Assert.assertEquals(expected, context.performDeployment());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            if (expectedException == null || !expectedException.isInstance(e)) {
                fail();
            }
        }
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"Unsupported with True", NOT_SUPPORTED, "true", false, NoSuchPropertyException.class},
            {"Unsupported with False", NOT_SUPPORTED, "false", false, NoSuchPropertyException.class},
            {"Unsupported with null", NOT_SUPPORTED, null, false, null},
            {"Unsupported with Invalid Input", NOT_SUPPORTED, "abc", false, NoSuchPropertyException.class},
            {"Supported with True", SUPPORTED, "true", true, null},
            {"Supported with False", SUPPORTED, "false", false, null},
            {"Supported with null", SUPPORTED, null, false, null},
            {"Supported with Invalid Input", SUPPORTED, "abc", false, NoSuchPropertyException.class},
        });
    }
}
