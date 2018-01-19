package org.opentosca.toscana.core.parse.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.parse.converter.TypeConverter;
import org.opentosca.toscana.core.parse.converter.util.AttributeNotSetException;
import org.opentosca.toscana.core.parse.converter.util.ToscaStructure;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.slf4j.Logger;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class ServiceGraph extends SimpleDirectedGraph<Entity, Connection> {

    private final MappingEntity root;
    private final Log log;
    private final Logger logger;

    public ServiceGraph(Log log) {
        super((sourceVertex, targetVertex) -> new Connection(targetVertex.getName(), sourceVertex, targetVertex));
        this.log = log;
        this.logger = log.getLogger(getClass());
        root = new MappingEntity(ToscaStructure.SERVICE_TEMPLATE, this);
        addVertex(root);
    }

    public ServiceGraph(Node snakeNode, Log log) {
        this(log);
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

    /**
     Adds a new entity to the graph. Also adds the edge to its parent entity.
     If one ore more parent entities do not exist, automatically adds intermediate entities.
     IF equivalent entity already exists, does nothing.
     */
    public void addEntity(Entity entity) {
        Entity parent = root;
        Entity child;
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

    public Optional<Entity> getChild(Entity source, ToscaKey<?> key) {
        if (key.getPredecessor().isPresent()) {
            Optional<Entity> intermediateEntity = getChild(source, key.getPredecessor().get());
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
    public Optional<Entity> getChild(Entity source, String key) {
        for (Connection connection : outgoingEdgesOf(source)) {
            if (source instanceof SequenceEntity) {
                Optional<Entity> child = getChild(connection.getTarget(), key);
                if (child.isPresent()) {
                    return child;
                }
            } else if (connection.getKey().equals(key)) {
                return Optional.of(connection.getTarget());
            }
        }
        return Optional.empty();
    }

    public Entity getChildOrThrow(Entity source, String key) {
        Optional<Entity> optionalEntity = getChild(source, key);
        return optionalEntity.orElseThrow(() -> new IllegalStateException(
            String.format("Entity '%s' is referenced but does not exist", source.getId().descend(key))
        ));
    }

    public Entity getChildOrThrow(Entity source, ToscaKey key) {
        Optional<Entity> optionalEntity = getChild(source, key);
        return optionalEntity.orElseThrow(() -> new IllegalStateException(
            String.format("Entity '%s' is referenced but does not exist", source.getId().descend(key))
        ));
    }

    public Optional<Entity> getEntity(List<String> path) {
        Entity current = root;
        for (String segment : path) {
            Optional<Entity> child = getChild(current, segment);
            if (child.isPresent()) {
                current = child.get();
            }
        }
        return Optional.of(current);
    }

    public Optional<Entity> getEntity(EntityId id) {
        return getEntity(id.getPath());
    }

    public Entity getEntityOrThrow(EntityId id) {
        Optional<Entity> optionalEntity = getEntity(id);
        return optionalEntity.orElseThrow(() -> new IllegalStateException(
            String.format("Entity '%s' is referenced but does not exist", id)
        ));
    }

    public Set<Entity> getChildren(Entity entity) {
        Set<Entity> children = new HashSet<>();
        if (entity != null) {
            for (Connection connection : outgoingEdgesOf(entity)) {
                children.add(connection.getTarget());
            }
        }
        return children;
    }

    public Set<Entity> getChildren(EntityId id) {
        Optional<Entity> entity = getEntity(id);
        return getChildren(entity.orElse(null));
    }

    public Entity getParent(Entity entity) {
        EntityId parentId = entity.getId().ascend();
        return getEntity(parentId).get();
    }

    /**
     Replaces given source with given target. Source node is removed while target node is added to graph.
     All outgoing and incoming edges of source get attached to the target.
     */
    public void replaceEntity(Entity source, Entity target) {
        Set<Pair<Entity, String>> predecessors = incomingEdgesOf(source).stream()
            .map(c -> Pair.of(c.getSource(), c.getKey()))
            .collect(Collectors.toSet());
        Set<Pair<Entity, String>> successors = outgoingEdgesOf(source).stream()
            .map(c -> Pair.of(c.getTarget(), c.getKey()))
            .collect(Collectors.toSet());
        removeVertex(source);
        addVertex(target);
        predecessors.forEach(p -> addConnection(p.getLeft(), target, p.getRight()));
        successors.forEach(p -> addConnection(target, p.getLeft(), p.getRight()));
    }

    public void addConnection(Entity source, Entity target, String connectionName) {
        Connection connection = new Connection(connectionName, source, target);
        addEdge(source, target, connection);
    }

    public <T> Set<T> getCollection(Entity entity, ToscaKey<T> key) {
        Set<T> values = new HashSet<>();
        Optional<Entity> child = getChild(entity, key);
        if (child.isPresent()) {
            for (Entity grandChild : child.get().getChildren()) {
                try {
                    T value = TypeConverter.convert(grandChild, key);
                    values.add(value);
                } catch (AttributeNotSetException e) {
                    logger.warn("Trying to access an unset attribute - skipping.", e);
                }
            }
        }
        return values;
    }

    public Log getLog() {
        return log;
    }
}
