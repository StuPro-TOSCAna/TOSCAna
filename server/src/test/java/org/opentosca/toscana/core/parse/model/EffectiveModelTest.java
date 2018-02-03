package org.opentosca.toscana.core.parse.model;

import java.util.Optional;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.node.Compute;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EffectiveModelTest extends BaseUnitTest {

    private EffectiveModel model;
    private Compute compute;

    @Before
    public void setUp() {
        this.model = new EffectiveModelFactory().create(TestCsars.VALID_SINGLE_COMPUTE_WINDOWS_TEMPLATE, logMock());
        this.compute = (Compute) model.getNodes().iterator().next();
    }

    /**
     Test the generic 'setter' functionality (appears trivial but that's a deception)
     */
    @Test
    public void setPropertyTest() {
        String expected = "test-public-address";
        compute.setPublicAddress(expected);
        Optional<String> publicAddress = compute.getPublicAddress();
        assertTrue(publicAddress.isPresent());
        assertEquals(expected, publicAddress.get());
    }

    @Test
    public void setEnumTest() {
        OsCapability os = compute.getOs();
        OsCapability.Distribution expected = OsCapability.Distribution.FEDORA;
        os.setDistribution(expected);

        Optional<OsCapability.Distribution> result = os.getDistribution();
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    /**
     Tests the generic functionality of automatic setting the default value upon element creation
     */
    @Test
    public void setDefaultTest() {
        ScalableCapability scalable = compute.getScalable();
        assertNotNull(scalable);
    }
}
