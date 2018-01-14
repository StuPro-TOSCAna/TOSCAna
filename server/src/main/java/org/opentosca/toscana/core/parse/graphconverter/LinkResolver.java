package org.opentosca.toscana.core.parse.graphconverter;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.artifact.Repository;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.Interface;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.requirement.Requirement;

/**
 Responsible for resolving symbolic TOSCA links in the ServiceGraph - instead of symbolic links,
 appropriate edges get created
 */
public class LinkResolver {

    private final ServiceModel model;
    private final ServiceGraph graph;

    public LinkResolver(ServiceModel model) {
        this.model = model;
        this.graph = model.getGraph();
    }

    /**
     For every encountered TOSCA symbolic link, removes link and creates an appropriate edge.
     */
    public void resolveLinks() {
        resolveRequirements();
        resolveRepositories();
        resolveImplementationArtifacts();
    }

    private void resolveRepositories() {
        Map<String, RootNode> nodes = new ToscaFactory().wrapNodes(model);
        for (RootNode node : nodes.values()) {
            for (Artifact artifact : node.getArtifacts()) {
                MappingEntity artifactEntity = artifact.getBackingEntity();
                Optional<BaseEntity<?>> repository = artifactEntity.getChild(Artifact.REPOSITORY.name);
                if (repository.isPresent()) {
                    String url = ((ScalarEntity) repository.get()).get();
                    EntityId targetId = ToscaStructure.REPOSITORIES.descend(url);
                    Optional<BaseEntity<Repository>> target = graph.getEntity(targetId);
                    if (target.isPresent()) {
                        graph.replaceEntity(repository.get(), target.get());
                    }
                }
            }
        }
    }

    private void resolveRequirements() {
        Iterator<MappingEntity> nodeIt = model.iterator(ToscaStructure.NODE_TEMPLATES);
        while (nodeIt.hasNext()) {
            MappingEntity nodeEntity = nodeIt.next();
            Optional<BaseEntity<?>> requirementsEntity = nodeEntity.getChild(RootNode.REQUIREMENTS.name);
            if (requirementsEntity.isPresent()) {
                Set<BaseEntity<?>> requirements = requirementsEntity.get().getChildren();
                for (BaseEntity requirement : requirements) {
                    if (requirement instanceof ScalarEntity) {
                        ScalarEntity explicitRequirement = (ScalarEntity) requirement;
                        String fulfillerName = explicitRequirement.get();
                        EntityId fulfillerId = ToscaStructure.NODE_TEMPLATES.descend(fulfillerName);
                        BaseEntity<Object> fulfiller = model.getEntity(fulfillerId).orElseThrow(
                            () -> new IllegalStateException(String.format(
                                "No node with name '%s' found, but required as fulfiller in requirement", fulfillerName)
                            ));
                        MappingEntity resolvedRequirement = new MappingEntity(requirement.getId(), graph);
                        graph.replaceEntity(requirement, resolvedRequirement);
                        graph.addConnection(resolvedRequirement, fulfiller, Requirement.NODE_NAME);
                    } else {
                        throw new UnsupportedOperationException("Extended (implicit) notation for requirement assignment not supported yet");
                    }
                }
            }
        }
    }

    private void resolveImplementationArtifacts() {
        Map<String, RootNode> nodes = new ToscaFactory().wrapNodes(model);
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
