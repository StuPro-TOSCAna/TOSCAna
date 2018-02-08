package org.opentosca.toscana.core.parse.converter;

import java.util.Optional;

import org.opentosca.toscana.core.parse.converter.util.ToscaStructure;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogFormat;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.artifact.Repository;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.requirement.Requirement;

import org.slf4j.Logger;

/**
 Responsible for converting tosca short notations (one-line notation) to its corresponding extended (ergo: normalized) form
 */
public class GraphNormalizer {

    private static Logger logger;

    public static void normalize(ServiceGraph graph, Log log) {
        logger = log.getLogger(GraphNormalizer.class);
        logger.info("Expanding short notations to extended notations");
        normalizeRepositories(graph);
        normalizeArtifacts(graph);
        normalizeOperations(graph);
        normalizeRequirements(graph);
    }

    private static synchronized void normalizeRepositories(ServiceGraph graph) {
        logger.debug(LogFormat.indent(1, "repositories"));
        for (Entity repository : graph.getChildren(ToscaStructure.REPOSITORIES)) {
            logger.debug(LogFormat.indent(2, repository.getId()));
            normalize(graph, repository, Repository.URL.name);
        }
    }

    private static void normalizeArtifacts(ServiceGraph graph) {
        logger.debug(LogFormat.indent(1, "artifacts"));
        for (Entity node : graph.getChildren(ToscaStructure.NODE_TEMPLATES)) {
            Optional<Entity> artifacts = node.getChild(RootNode.ARTIFACTS);
            if (artifacts.isPresent()) {
                for (Entity artifactEntity : artifacts.get().getChildren()) {
                    normalize(graph, artifactEntity, Artifact.FILE_PATH.name);
                }
            }
        }
    }

    private static void normalizeOperations(ServiceGraph graph) {
        logger.debug(LogFormat.indent(1, "operations"));
        for (Entity node : graph.getChildren(ToscaStructure.NODE_TEMPLATES)) {
            Optional<Entity> interfaces = node.getChild(RootNode.INTERFACES.name);
            if (interfaces.isPresent()) {
                for (Entity thisInterface : interfaces.get().getChildren()) {
                    for (Entity operation : thisInterface.getChildren()) {
                        logger.debug(LogFormat.indent(2, operation.getId()));
                        normalize(graph, operation, Operation.IMPLEMENTATION.name, Operation.PRIMARY.name);
                        Optional<Entity> implementation = graph.getEntity(operation.getId().descend(Operation.IMPLEMENTATION.name));
                        implementation.ifPresent(e -> normalize(graph, e, Operation.PRIMARY.name));
                        Optional<Entity> shortArtifact = graph.getEntity(operation.getId().descend(Operation.PRIMARY));
                        shortArtifact.ifPresent(artifact -> normalize(graph, artifact, Artifact.FILE_PATH.name));
                    }
                }
            }
        }
    }

    private static void normalizeRequirements(ServiceGraph graph) {
        logger.debug(LogFormat.indent(1, "requirements"));
        for (Entity node : graph.getChildren(ToscaStructure.NODE_TEMPLATES)) {
            Optional<Entity> requirements = node.getChild(RootNode.REQUIREMENTS);
            if (requirements.isPresent()) {
                for (Entity requirement : requirements.get().getChildren()) {
                    logger.info(LogFormat.indent(2, requirement.getId()));
                    normalize(graph, requirement, Requirement.NODE_NAME);
                }
            }
        }
    }

    private static void normalize(ServiceGraph graph, Entity entity, String... referencedKeys) {
        if (entity instanceof ScalarEntity) {
            ScalarEntity shortEntity = (ScalarEntity) entity;
            MappingEntity normalizedEntity = new MappingEntity(shortEntity.getId(), graph);
            EntityId referencedId = shortEntity.getId();
            for (String referencedKey : referencedKeys) {
                referencedId = referencedId.descend(referencedKey);
            }
            ScalarEntity referencedEntity = new ScalarEntity(shortEntity.getValue(), referencedId, graph);
            graph.replaceEntity(shortEntity, normalizedEntity);
            graph.addEntity(referencedEntity);
        }
    }
}
