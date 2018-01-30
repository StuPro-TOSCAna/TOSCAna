package org.opentosca.toscana.core.parse.converter;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
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
import static org.opentosca.toscana.core.parse.TestTemplates.SymbolicLinks.ARTIFACT;
import static org.opentosca.toscana.core.parse.TestTemplates.SymbolicLinks.REPOSITORY;
import static org.opentosca.toscana.core.parse.TestTemplates.SymbolicLinks.REQUIREMENT;

public class LinkResolverTest extends BaseUnitTest {

    @Test
    public void resolveRequirementLink() {
        EffectiveModel model = new EffectiveModelFactory().create(REQUIREMENT, logMock());
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
        EffectiveModel model = new EffectiveModelFactory().create(REPOSITORY, logMock());
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
        EffectiveModel model = new EffectiveModelFactory().create(ARTIFACT, logMock());
        WebServer node = (WebServer) model.getNodeMap().get("test-node");
        Optional<Operation> create = node.getStandardLifecycle().getCreate();
        assertTrue(create.isPresent());
        Optional<Artifact> artifact = create.get().getArtifact();
        assertTrue(artifact.isPresent());
        assertEquals("test-file", artifact.get().getFilePath());
    }
}
