package org.opentosca.toscana.core.parse.model;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.TestTemplates;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.node.Compute;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScalableCapabilityTest extends BaseUnitTest {

    @Test
    public void scalableTest() {
        EffectiveModel model = new EffectiveModelFactory().create(TestTemplates.Capabilities.SCALABLE, logMock());
        Compute compute = (Compute) model.getNodes().iterator().next();
        ScalableCapability scalable = compute.getScalable();
        assertEquals(5, (int) scalable.getMinInstances());
        assertEquals(7, (int) scalable.getDefaultInstances().get());
        assertEquals(Integer.MAX_VALUE, (int) scalable.getMaxInstances());
    }
}
