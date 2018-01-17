package org.opentosca.toscana.core.parse.graphconverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.parse.graphconverter.util.ToscaStructure;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class ServiceGraph extends SimpleDirectedGraph<BaseEntity, Connection> {

    // TODO 
    private final MappingEntity root = new MappingEntity(ToscaStructure.SERVICE_TEMPLATE, this);

    @Deprecated
    public ServiceGraph() {
        super((sourceVertex, targetVertex) -> new Connection(targetVertex.getName(), sourceVertex, targetVertex));
        addVertex(root);
    }

    public ServiceGraph(Node snakeNode) {
        super((sourceVertex, targetVertex) -> new Connection(targetVertex.getName(), sourceVertex, targetVertex));
        addVertex(root);
        EntityId id = new EntityId(new ArrayList<>());
        populateGraph(snakeNode, id);
    }

    private void populateGraph(Node node, EntityId id) {
        if (node instanceof ScalarNode) {
            ScalarNode scalarNode = (ScalarNode) node;
            ScalarEntity scalarEntity = new ScalarEntity(scalarNode.getValue(), id, this);
            addEntity(scalarEntity);
        } else if (node instanceof MappingNode) {
            MappingNode mappingNode = (MappingNode) node;
            MappingEntity mappingEntity = new MappingEntity(id, this);
            addEntity(mappingEntity);
            for (NodeTuple tuple : mappingNode.getValue()) {
                String key = ((ScalarNode) tuple.getKeyNode()).getValue();
                Node childNode = tuple.getValueNode();
                EntityId childId = id.descend(key);
                populateGraph(childNode, childId);
            }
        } else if (node instanceof SequenceNode) {
            SequenceNode sequenceNode = (SequenceNode) node;
            SequenceEntity sequenceEntity = new SequenceEntity(sequenceNode, id, this);
            addEntity(sequenceEntity);
            for (int i = 0; i < sequenceNode.getValue().size(); i++) {
                EntityId childId = id.descend(String.valueOf(i));
                Node childNode = sequenceNode.getValue().get(i);
                populateGraph(childNode, childId);
            }
        }
    }

    // TODO documentation

    /**
     Adds a new entity to the graph. Also adds the edge to its parent entity.
     If one ore more parent entities do not exist, automatically adds intermediate entities.
     IF equivalent entity already exists, does nothing.
     */
    public void addEntity(BaseEntity entity) {
        BaseEntity parent = root;
        BaseEntity child;
        EntityId id = entity.getId();
        EntityId currentId = new EntityId(new ArrayList<>());
        for (String segment : id.getPath()) {
            currentId = currentId.descend(segment);
            child = getChild(parent, segment).orElse(null);
            if (child == null) {
                if (id.equals(currentId)) {
                    child = entity;
                } else {
                    child = new MappingEntity(currentId, this);
                }
                boolean added = addVertex(child);
                if (added) {
                    addEdge(parent, child);
                }
            }
            parent = child;
        }
    }

    public Optional<BaseEntity> getChild(BaseEntity source, ToscaKey<?> key) {
        if (key.getPredecessor().isPresent()) {
            Optional<BaseEntity> intermediateEntity = getChild(source, key.getPredecessor().get());
            if (intermediateEntity.isPresent()) {
                source = intermediateEntity.get();
            } else {
                return Optional.empty();
            }
        }
        return getChild(source, key.name);
    }

    /**
     Returns the associated child for given name and source entity.

     @return null if no child associated with given name was found
     */
    public Optional<BaseEntity> getChild(BaseEntity source, String key) {
        for (Connection connection : outgoingEdgesOf(source)) {
            if (source instanceof SequenceEntity) {
                Optional<BaseEntity> child = getChild(connection.getTarget(), key);
                if (child.isPresent()) {
                    return child;
                }
            } else if (connection.getKey().equals(key)) {
                return Optional.of(connection.getTarget());
            }
        }
        return Optional.empty();
    }

    public BaseEntity getChildOrThrow(BaseEntity source, String key) {
        Optional<BaseEntity> optionalEntity = getChild(source, key);
        return optionalEntity.orElseThrow(() -> new IllegalStateException(
            String.format("Entity '%s' is referenced but does not exist", source.getId().descend(key))
        ));
    }

    public Optional<BaseEntity> getEntity(List<String> path) {
        BaseEntity current = root;
        for (String segment : path) {
            Optional<BaseEntity> child = getChild(current, segment);
            if (child.isPresent()) {
                current = child.get();
            }
        }
        return Optional.of(current);
    }

    public BaseEntity getEntityOrThrow(EntityId id) {
        Optional<BaseEntity> optionalEntity = getEntity(id);
        return optionalEntity.orElseThrow(() -> new IllegalStateException(
            String.format("Entity '%s' is referenced but does not exist", id)
        ));
    }

    public Optional<BaseEntity> getEntity(EntityId id) {
        return getEntity(id.getPath());
    }

    public Set<BaseEntity> getChildren(BaseEntity entity) {
        Set<BaseEntity> children = new HashSet<>();
        if (entity != null) {
            for (Connection connection : outgoingEdgesOf(entity)) {
                children.add(connection.getTarget());
            }
        }
        return children;
    }

    public Set<BaseEntity> getChildren(EntityId id) {
        Optional<BaseEntity> entity = getEntity(id);
        return getChildren(entity.orElse(null));
    }

    public BaseEntity getParent(BaseEntity entity) {
        EntityId parentId = entity.getId().ascend();
        return getEntity(parentId).get();
    }

    /**
     Replaces given source with given target. Source node is removed while target node is added to graph.
     All outgoing and incoming edges of source get attached to the target.
     */
    public void replaceEntity(BaseEntity source, BaseEntity target) {
        Set<Pair<BaseEntity, String>> predecessors = incomingEdgesOf(source).stream()
            .map(c -> Pair.of(c.getSource(), c.getKey()))
            .collect(Collectors.toSet());
        Set<Pair<BaseEntity, String>> successors = outgoingEdgesOf(source).stream()
            .map(c -> Pair.of(c.getTarget(), c.getKey()))
            .collect(Collectors.toSet());
        removeVertex(source);
        addVertex(target);
        predecessors.forEach(p -> addConnection(p.getLeft(), target, p.getRight()));
        successors.forEach(p -> addConnection(target, p.getLeft(), p.getRight()));
    }

    public void addConnection(BaseEntity source, BaseEntity target, String connectionName) {
        Connection connection = new Connection(connectionName, source, target);
        addEdge(source, target, connection);
    }
}
