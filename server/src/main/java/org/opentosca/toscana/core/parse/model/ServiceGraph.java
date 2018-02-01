package org.opentosca.toscana.core.parse.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.parse.ToscaTemplateException;
import org.opentosca.toscana.core.parse.converter.GraphNormalizer;
import org.opentosca.toscana.core.parse.converter.LinkResolver;
import org.opentosca.toscana.core.parse.converter.TypeWrapper;
import org.opentosca.toscana.core.parse.converter.util.ToscaStructure;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.Parameter;

import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class ServiceGraph extends SimpleDirectedGraph<Entity, Connection> {

    private final MappingEntity root;
    private final Log log;
    private final Logger logger;

    // guard that makes sure graph finalization can only be done once
    private boolean finalized = false;
    private Map<String, Property> inputs;

    public ServiceGraph(Log log) {
        super((sourceVertex, targetVertex) -> new Connection(targetVertex.getName(), sourceVertex, targetVertex));
        this.log = log;
        this.logger = log.getLogger(getClass());
        logger.info("Constructing service graph");
        root = new MappingEntity(ToscaStructure.SERVICE_TEMPLATE, this);
        addVertex(root);
    }

    public ServiceGraph(File template, Log log) {
        this(log);
        try {
            logger.info("Parsing service template");
            Node snakeNode = new Yaml().compose(new FileReader(template));
            EntityId id = new EntityId(new ArrayList<>());
            ToscaStructure.buildBasicStructure(this); // in case this has not already been established automatically
            logger.info("Populating service graph");
            populateGraph(snakeNode, id);
            if (inputsValid()) {
                finalizeGraph();
            }
        } catch (FileNotFoundException e) {
            logger.error(String.format("Template '%s' does not exist - failed to construct ServiceGraph", template), e);
            throw new IllegalStateException();
        }
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
                Node childNode = sequenceNode.getValue().get(i);
                String childName;
                if (childNode instanceof MappingNode) {
                    NodeTuple childTuple = ((MappingNode) childNode).getValue().get(0);
                    childName = ((ScalarNode) childTuple.getKeyNode()).getValue();
                    childNode = childTuple.getValueNode();
                } else {
                    childName = String.valueOf(i);
                }
                EntityId childId = id.descend(childName);
                populateGraph(childNode, childId);
            }
        }
    }

    /**
     Normalizes the graph and resolves symbolic links.
     Must not be called before all required inputs have been set.
     */
    public void finalizeGraph() {
        if (finalized) return;
        logger.info("Finalizing service graph");
        finalized = true;
        if (!inputsValid()) {
            logger.error("Inputs must be set and valid before graph can get finalized.");
            throw new IllegalStateException();
        }
        GraphNormalizer.normalize(this, log);
        LinkResolver.resolveLinks(this, log);
    }

    /**
     @return true if all declared tosca inputs have a value assigned
     (or have a default value, or are flagged as not required), false otherwise.
     */
    public boolean inputsValid() {
        Map<String, Property> inputs = getInputs();
        return inputs.values().stream()
            .allMatch(Property::isValid);
    }

    public Map<String, Property> getInputs() {
        if (inputs == null) {
            logger.info("Collecting TOSCA inputs from service graph");
            inputs = new HashMap<>();
            Collection<Entity> inputEntities = getChildren(ToscaStructure.INPUTS);
            for (Entity inputEntity : inputEntities) {
                Parameter input = TypeWrapper.wrapEntity((MappingEntity) inputEntity, Parameter.class);
                inputs.put(input.getEntityName(), input);
            }
            logger.debug("Found {} TOSCA inputs in graph", inputs.size());
            inputs.keySet().forEach(key -> logger.debug("  > '{}'", key));
        }
        return inputs;
    }

    public Map<String, Property> getOutputs() {
        return getChildren(ToscaStructure.OUTPUTS)
            .stream()
            .map(o -> (Parameter) TypeWrapper.wrapEntity((MappingEntity) o, Parameter.class))
            .collect(Collectors.toMap(Parameter::getKey, Function.identity()));
    }

    /**
     Adds a new entity to the graph. Also adds the edge to its parent entity.
     If one ore more parent entities do not exist, automatically adds intermediate entities.
     If equivalent entity already exists, does nothing.
     */
    public void addEntity(Entity entity) {
        Entity parent = root;
        Entity child;
        EntityId id = entity.getId();
        EntityId currentId = new EntityId(new ArrayList<>());
        for (String segment : id.getPath()) {
            currentId = currentId.descend(segment);
            child = parent.getChild(segment).orElse(null);
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

    public Optional<Entity> getEntity(List<String> path) {
        Entity current = root;
        for (String segment : path) {
            Optional<Entity> child = current.getChild(segment);
            if (child.isPresent()) {
                current = child.get();
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(current);
    }

    public Optional<Entity> getEntity(EntityId id) {
        return getEntity(id.getPath());
    }

    public Entity getEntityOrThrow(EntityId id) {
        Optional<Entity> optionalEntity = getEntity(id);
        return optionalEntity.orElseThrow(() -> new ToscaTemplateException(
            String.format("Entity '%s' is referenced but does not exist", id)
        ));
    }

    /**
     Returns an iterator for a set of entities referenced by given EntityId.
     */
    public Iterator<Entity> iterator(EntityId id) {
        Entity entities = this.getEntityOrThrow(id);
        return entities.getChildren().iterator();
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

    public Collection<Entity> getChildren(EntityId id) {
        Optional<Entity> entity = getEntity(id);
        if (entity.isPresent()) {
            return entity.get().getChildren();
        } else {
            return new HashSet<>();
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
