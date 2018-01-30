package org.opentosca.toscana.core.parse.model;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.node.SoftwareComponent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.opentosca.toscana.core.parse.TestTemplates.Nodes.SOFTWARE_COMPONENT;

public class NodeConverterSoftwareComponentTest extends BaseUnitTest {

    @Test
    public void softwareComponent() {
        EffectiveModel model = new EffectiveModelFactory().create(SOFTWARE_COMPONENT, logMock());
        SoftwareComponent softwareComponent = (SoftwareComponent) model.getNodes().iterator().next();
        Credential credential = softwareComponent.getAdminCredential().get();
        assertEquals("securePassword", credential.getToken());
        assertEquals("alice", credential.getUser().get());
        assertEquals("3.5.1", softwareComponent.getComponentVersion().get());
    }
}
