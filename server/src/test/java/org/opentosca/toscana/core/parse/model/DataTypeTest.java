package org.opentosca.toscana.core.parse.model;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.TestTemplates;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.node.WebApplication;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataTypeTest extends BaseUnitTest {

    @Test
    public void portTest() {
        EffectiveModel model = new EffectiveModelFactory().create(TestTemplates.Datatypes.PORT, logMock());
        WebApplication app = (WebApplication) model.getNodes().iterator().next();
        EndpointCapability endpoint = app.getAppEndpoint();
        assertEquals(new Port(3000), endpoint.getPort().get());
        Port expected = new Port(4000);
        endpoint.setPort(expected);
        assertEquals(expected, endpoint.getPort().get());
    }
}
