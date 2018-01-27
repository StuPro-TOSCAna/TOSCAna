package org.opentosca.toscana.core.parse.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.model.EntityId;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceEntity extends Entity implements CollectionEntity {

    private final SequenceNode sequenceNode;

    public SequenceEntity(SequenceNode node, EntityId id, ServiceGraph graph) {
        super(id, graph);
        this.sequenceNode = node;
    }

    public List<String> getValues() {
        List<String> values = new ArrayList<>();
        for (Node node : sequenceNode.getValue()) {
            String value = ((ScalarNode) node).getValue();
            values.add(value);
        }
        return values;
    }

    @Override
    public List<Entity> getChildren() {
        List<Entity> children = new ArrayList<>();
        Collection<Entity> entities = super.getChildren();
        List<Entity> orderedEntities = new LinkedList<>(entities);
        // TODO sort per connection name, not entity name. 
        orderedEntities.sort((b1, b2) -> b1.getName().compareTo(b2.getName()));
        for (Entity entity : orderedEntities) {
            children.add(entity);
        }
        return children;
    }

    /**
     Returns the associated child for given name and source entity.

     @return null if no child associated with given name was found
     */
    @Override
    public Optional<Entity> getChild(String key) {
        for (Connection connection : graph.outgoingEdgesOf(this)) {
            Entity child = connection.getTarget();
            if (key.equals(child.getName())) {
                return Optional.of(child);
            }
        }
        return Optional.empty();
    }

    @Override
    public void addChild(Entity entity) {
        // TODO
    }

    @Override
    public String toString() {
        return (String.format("SequenceEntity (id=%s", getId()));
    }
}
