package org.opentosca.toscana.core.parse.graphconverter;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.artifact.Repository;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.requirement.HostRequirement;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LinkResolverIT extends BaseIntegrationTest {

    private final static File BASE_PATH = new File("src/integration/resources/converter/symbolic_links");
    private final static File REQUIREMENT = new File(BASE_PATH, "requirement_link.yaml");
    private final static File REPOSITORY = new File(BASE_PATH, "repository_link.yaml");
    private final static File ARTIFACT = new File(BASE_PATH, "implementation_link.yaml");

    @Test
    public void resolveRequirementLink() {
        EffectiveModel model = new EffectiveModel(REQUIREMENT, log);
        WebServer node = (WebServer) model.getNodeMap().get("test-node1");
        HostRequirement requirement = node.getHost();
        assertNotNull(requirement);
        Set<Compute> fulfillers = requirement.getFulfillers();
        assertEquals(1, fulfillers.size());
        Compute fulfiller = fulfillers.iterator().next();
        assertEquals("test-node2", fulfiller.getEntityName());
    }

    @Test
    public void resolveRepositoryLink() {
        EffectiveModel model = new EffectiveModel(REPOSITORY, log);
        WebServer node = (WebServer) model.getNodeMap().get("test-node");
        Set<Artifact> artifacts = node.getArtifacts();
        Artifact artifact = artifacts.iterator().next();
        assertNotNull(artifact);
        Optional<Repository> repo = artifact.getRepository();
        assertTrue(repo.isPresent());
        assertEquals("http://test.repo.com/", repo.get().getUrl());
    }
    
    @Test
    public void resolveImplementationLink() {
        EffectiveModel model = new EffectiveModel(ARTIFACT, log);
        WebServer node = (WebServer) model.getNodeMap().get("test-node");
        Optional<Operation> create = node.getStandardLifecycle().getCreate();
        assertTrue(create.isPresent());
        Optional<Artifact> artifact = create.get().getArtifact();
        assertTrue(artifact.isPresent());
        assertEquals("test-file", artifact.get().getFilePath());
    }
    
}
