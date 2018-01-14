package org.opentosca.toscana.core.parse.graphconverter;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.StandardLifecycle;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 Tests the graph normalization, in other words: Tests whether short notations are correctly converted to their
 corresponding extended notation.
 */
public class GraphNormalizerIT extends BaseIntegrationTest {

    private final static File BASE_PATH = new File("src/integration/resources/converter/normalization");
    private final static File REPOSITORY = new File(BASE_PATH, "repository_norm.yaml");
    private final static File OPERATION = new File(BASE_PATH, "operation_norm.yaml");

    @Test
    public void repositoryNormalization() {
        ServiceModel model = new ServiceModel(REPOSITORY);
        Set<BaseEntity<?>> repositories = model.getEntity(ToscaStructure.REPOSITORIES).get().getChildren();
        BaseEntity repository = repositories.iterator().next();
        Optional<BaseEntity> url = repository.getChild("url");
        assertTrue(url.isPresent());
        assertEquals("http://test.repo.com/", url.get().get());
    }

    @Test
    public void operationNormalization() {
        ServiceModel model = new ServiceModel(OPERATION);
        EntityId lifecycleId = ToscaStructure.NODE_TEMPLATES.descend("test-node")
            .descend(RootNode.INTERFACES.name)
            .descend(RootNode.STANDARD_LIFECYCLE.name);
        EntityId createId = lifecycleId.descend(StandardLifecycle.CREATE.name);
        BaseEntity createEntity = model.getEntity(createId).get();
        Operation create = new ToscaFactory().wrapEntity((MappingEntity) createEntity, Operation.class);
        assertTrue(create.getArtifact().isPresent());
        Artifact createArtifact = create.getArtifact().get();
        assertEquals("test-artifact", createArtifact.getFilePath());
        
        EntityId startId = lifecycleId.descend(StandardLifecycle.START.name);
        BaseEntity startEntity = model.getEntity(startId).get();
        Operation start = new ToscaFactory().wrapEntity((MappingEntity) startEntity, Operation.class);
        assertTrue(start.getArtifact().isPresent());
        Artifact startArtifact = start.getArtifact().get();
        assertEquals("test-artifact2", startArtifact.getFilePath());


    }
}
