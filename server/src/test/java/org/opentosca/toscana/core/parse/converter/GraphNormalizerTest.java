package org.opentosca.toscana.core.parse.converter;

import java.util.Collection;
import java.util.Optional;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.converter.util.ToscaStructure;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.StandardLifecycle;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.opentosca.toscana.core.parse.TestTemplates.Normalization.OPERATION;
import static org.opentosca.toscana.core.parse.TestTemplates.Normalization.REPOSITORY;

/**
 Tests the graph normalization, in other words: Tests whether short notations are correctly converted to their
 corresponding extended notation.
 */
public class GraphNormalizerTest extends BaseUnitTest {

    @Test
    public void repositoryNormalization() {
        ServiceGraph graph = new ServiceGraph(REPOSITORY, log);
        Collection<Entity> repositories = graph.getEntity(ToscaStructure.REPOSITORIES).get().getChildren();
        Entity repository = repositories.iterator().next();
        Optional<Entity> url = repository.getChild("url");
        assertTrue(url.isPresent());
        Assert.assertEquals("http://test.repo.com/", ((ScalarEntity) url.get()).getValue());
    }

    @Test
    public void operationNormalization() {
        ServiceGraph graph = new ServiceGraph(OPERATION, log);
        EntityId lifecycleId = ToscaStructure.NODE_TEMPLATES.descend("test-node")
            .descend(RootNode.INTERFACES.name)
            .descend(RootNode.STANDARD_LIFECYCLE.name);
        EntityId createId = lifecycleId.descend(StandardLifecycle.CREATE.name);
        Entity createEntity = graph.getEntity(createId).get();
        Operation create = new TypeWrapper().wrapEntity((MappingEntity) createEntity, Operation.class);
        assertTrue(create.getArtifact().isPresent());
        Artifact createArtifact = create.getArtifact().get();
        assertEquals("test-artifact", createArtifact.getFilePath());

        EntityId startId = lifecycleId.descend(StandardLifecycle.START.name);
        Entity startEntity = graph.getEntity(startId).get();
        Operation start = new TypeWrapper().wrapEntity((MappingEntity) startEntity, Operation.class);
        assertTrue(start.getArtifact().isPresent());
        Artifact startArtifact = start.getArtifact().get();
        assertEquals("test-artifact2", startArtifact.getFilePath());
    }
}
