package org.opentosca.toscana.core.parse.converter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.parse.converter.util.ToscaStructure;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.Interface;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.requirement.Requirement;

/**
 Responsible for resolving symbolic TOSCA links in the ServiceGraph - instead of symbolic links,
 appropriate edges get created
 */
public class LinkResolver {

    /**
     For every encountered TOSCA symbolic link, removes link and creates an appropriate edge.
     */
    public static void resolveLinks(ServiceGraph graph) {
        resolveRequirements(graph);
        resolveRepositories(graph);
        resolveImplementationArtifacts(graph);
    }

    private static void resolveRepositories(ServiceGraph graph) {
        Map<String, RootNode> nodes = new TypeWrapper().wrapNodes(graph);
        for (RootNode node : nodes.values()) {
            for (Artifact artifact : node.getArtifacts()) {
                MappingEntity artifactEntity = artifact.getBackingEntity();
                Optional<Entity> repository = artifactEntity.getChild(Artifact.REPOSITORY.name);
                if (repository.isPresent()) {
                    String url = ((ScalarEntity) repository.get()).getValue();
                    EntityId targetId = ToscaStructure.REPOSITORIES.descend(url);
                    Optional<Entity> target = graph.getEntity(targetId);
                    target.ifPresent(baseEntity -> graph.replaceEntity(repository.get(), baseEntity));
                }
            }
        }
    }

    private static void resolveRequirements(ServiceGraph graph) {
        Iterator<Entity> nodeIt = graph.iterator(ToscaStructure.NODE_TEMPLATES);
        while (nodeIt.hasNext()) {
            Entity nodeEntity = nodeIt.next();
            Optional<Entity> requirementsEntity = nodeEntity.getChild(RootNode.REQUIREMENTS.name);
            if (requirementsEntity.isPresent()) {
                Collection<Entity> requirements = requirementsEntity.get().getChildren();
                for (Entity requirement : requirements) {
                    MappingEntity mappingRequirement = (MappingEntity) requirement;
                    ScalarEntity fulfillerEntity = (ScalarEntity) mappingRequirement.getChild(Requirement.NODE_NAME).get();
                    String fulfillerName = fulfillerEntity.getValue();
                    EntityId fulfillerId = ToscaStructure.NODE_TEMPLATES.descend(fulfillerName);
                    Entity fulfiller = graph.getEntity(fulfillerId).orElseThrow(
                        () -> new IllegalStateException(String.format(
                            "No node with name '%s' found, but required as fulfiller in requirement", fulfillerName)
                        ));
                    graph.removeVertex(fulfillerEntity);
                    graph.addConnection(mappingRequirement, fulfiller, Requirement.NODE_NAME);
                }
            }
        }
    }

    private static void resolveImplementationArtifacts(ServiceGraph graph) {
        Map<String, RootNode> nodes = new TypeWrapper().wrapNodes(graph);
        for (RootNode node : nodes.values()) {
            for (Interface thisInterface : node.getInterfaces()) {
                for (Operation operation : thisInterface.getOperations()) {
                    Optional<Artifact> optionalArtifact = operation.getArtifact();
                    if (optionalArtifact.isPresent()) {
                        Artifact operationArtifact = optionalArtifact.get();
                        for (Artifact nodeArtifact : node.getArtifacts()) {
                            if (operationArtifact.getFilePath().equals(nodeArtifact.getEntityName())) {
                                graph.replaceEntity(operationArtifact.getBackingEntity(), nodeArtifact.getBackingEntity());
                            }
                        }
                    }
                }
            }
        }
    }
}
