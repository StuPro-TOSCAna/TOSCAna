package org.opentosca.toscana.core.parse.model;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.opentosca.toscana.core.parse.TestTemplates.Nodes.JAVA;
import static org.opentosca.toscana.core.parse.TestTemplates.Nodes.SOFTWARE_COMPONENT;

public class NodeConvertTest extends BaseUnitTest {

    @Test
    public void softwareComponent() {
        EffectiveModel model = new EffectiveModelFactory().create(SOFTWARE_COMPONENT, logMock());
        SoftwareComponent softwareComponent = (SoftwareComponent) model.getNodes().iterator().next();
        Credential credential = softwareComponent.getAdminCredential().get();
        assertEquals("securePassword", credential.getToken());
        assertEquals("alice", credential.getUser().get());
        assertEquals("3.5.1", softwareComponent.getComponentVersion().get());
    }

    @Test
    public void java() {
        EffectiveModel model = new EffectiveModelFactory().create(JAVA, logMock());
        JavaApplication app = (JavaApplication) model.getNodeMap().get("app");
        assertEquals("test-vm_options", app.getVmOptions().get());
        assertEquals("test-arguments", app.getArguments().get());
        Artifact artifact = app.getJar();
        assertEquals("test-artifact-path", artifact.getFilePath());
        JavaRuntime jre = (JavaRuntime) model.getNodeMap().get("jre");
        assertEquals("1.8", jre.getComponentVersion().get());
    }
}
