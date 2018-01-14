package org.opentosca.toscana.core.parse.graphconverter;

import java.util.Optional;

import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.artifact.Repository;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.Operation;

/**
 Responsible for converting tosca short notations (one-line notation) to its corresponding extended (ergo: normalized) form
 */
public class GraphNormalizer {

    public static void normalize(ServiceModel serviceModel) {
        ServiceGraph graph = serviceModel.getGraph();
        normalizeRepositories(graph);
        normalizeOperations(graph);
    }

    private static void normalizeRepositories(ServiceGraph graph) {
        for (BaseEntity repository : graph.getChildren(ToscaStructure.REPOSITORIES)) {
            normalize(graph, repository, Repository.URL.name);
        }
    }

    private static void normalizeOperations(ServiceGraph graph) {
        for (BaseEntity node : graph.getChildren(ToscaStructure.NODE_TEMPLATES)) {
            Optional<BaseEntity<?>> interfaces = node.getChild(RootNode.INTERFACES.name);
            if (interfaces.isPresent()) {
                for (BaseEntity<?> thisInterface : interfaces.get().getChildren()) {
                    for (BaseEntity operation : thisInterface.getChildren()) {
                        normalize(graph, operation, Operation.IMPLEMENTATION.name, Operation.PRIMARY.name);
                        Optional<BaseEntity<Object>> implementation = graph.getEntity(operation.getId().descend(Operation.IMPLEMENTATION.name));
                        implementation.ifPresent(e -> normalize(graph, e, Operation.PRIMARY.name));
                        Optional<BaseEntity<Object>> shortArtifact = graph.getEntity(operation.getId().descend(Operation.PRIMARY));
                        normalize(graph, shortArtifact.get(), Artifact.FILE_PATH.name);
                    }
                }
            }
        }
    }

    private static void normalize(ServiceGraph graph, BaseEntity entity, String... referencedKeys) {
        if (entity instanceof ScalarEntity) {
            ScalarEntity shortEntity = (ScalarEntity) entity;
            MappingEntity normalizedEntity = new MappingEntity(shortEntity.getId(), graph);
            EntityId referencedId = shortEntity.getId();
            for (String referencedKey : referencedKeys) {
                referencedId = referencedId.descend(referencedKey);
            }
            ScalarEntity referencedEntity = new ScalarEntity(shortEntity.get(), referencedId, graph);
            graph.replaceEntity(shortEntity, normalizedEntity);
            graph.addEntity(referencedEntity);
        }
    }
}
