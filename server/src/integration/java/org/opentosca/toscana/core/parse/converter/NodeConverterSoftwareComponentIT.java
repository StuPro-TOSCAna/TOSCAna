package org.opentosca.toscana.core.parse.converter;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.core.parse.CsarParseService;
import org.opentosca.toscana.core.parse.CsarParseServiceImpl;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.node.SoftwareComponent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.opentosca.toscana.core.parse.converter.NodeConverterResources.SOFTWARE_COMPONENT;

public class NodeConverterSoftwareComponentIT extends BaseIntegrationTest {

    private CsarParseService parser = new CsarParseServiceImpl();

    @Test
    public void softwareComponent() throws Exception {
        EffectiveModel model = parser.parse(SOFTWARE_COMPONENT);
        SoftwareComponent softwareComponent = (SoftwareComponent) model.getNodes().toArray()[0];
        Credential credential = softwareComponent.getAdminCredential().get();
        assertEquals("securePassword", credential.getToken());
        assertEquals("alice", credential.getUser().get());
        assertEquals("3.5.1", softwareComponent.getComponentVersion().get());
    }
}
