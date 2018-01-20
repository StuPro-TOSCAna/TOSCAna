package org.opentosca.toscana.core.parse.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.model.EntityId;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceEntity extends Entity {

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
        Set<Entity> entities = graph.getChildren(this);
        List<Entity> orderedEntities = new LinkedList<>(entities);
        orderedEntities.sort((b1, b2) -> b1.getName().compareTo(b2.getName()));
        for (Entity entity : orderedEntities) {
            if (entity instanceof ScalarEntity) {
                children.add(entity);
            } else {
                Iterator<Entity> it = entity.getChildren().iterator();
                Entity child = it.next();
                children.add(child);
            }
        }
        return children;
    }

    @Override
    public String toString() {
        return (String.format("SequenceEntity (id=%s", getId()));
    }
}
